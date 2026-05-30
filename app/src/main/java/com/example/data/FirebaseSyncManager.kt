package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

sealed class SyncResult {
    data class Success(val message: String) : SyncResult()
    data class Error(val reason: String) : SyncResult()
}

data class ConnectionResult(
    val isSuccess: Boolean,
    val latencyMs: Long,
    val message: String
)

class FirebaseSyncManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("firebase_sfa_prefs", Context.MODE_PRIVATE)
    private val client = OkHttpClient.Builder()
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    companion object {
        private const val KEY_FIREBASE_URL = "firebase_db_url"
        private const val KEY_FIREBASE_SECRET = "firebase_db_secret"
        private const val DEFAULT_SANDBOX_URL = "https://sopan-sfa-ledger-default-rtdb.asia-southeast1.firebasedatabase.app/"
    }

    fun saveConfig(url: String, secret: String) {
        var cleanUrl = url.trim()
        if (cleanUrl.isNotEmpty() && !cleanUrl.endsWith("/")) {
            cleanUrl += "/"
        }
        prefs.edit()
            .putString(KEY_FIREBASE_URL, cleanUrl)
            .putString(KEY_FIREBASE_SECRET, secret.trim())
            .apply()
    }

    fun getDatabaseUrl(): String {
        return prefs.getString(KEY_FIREBASE_URL, DEFAULT_SANDBOX_URL) ?: DEFAULT_SANDBOX_URL
    }

    fun getDatabaseSecret(): String {
        return prefs.getString(KEY_FIREBASE_SECRET, "") ?: ""
    }

    private fun buildRequestUrl(path: String): String {
        var baseUrl = getDatabaseUrl()
        if (baseUrl.isEmpty()) {
            baseUrl = DEFAULT_SANDBOX_URL
        }
        val secret = getDatabaseSecret()
        val suffix = if (secret.isNotEmpty()) "?auth=$secret" else ""
        return "${baseUrl}${path}.json${suffix}"
    }

    suspend fun testConnection(): ConnectionResult = withContext(Dispatchers.IO) {
        val startTime = System.currentTimeMillis()
        try {
            val url = buildRequestUrl("test_connection")
            // Prepare a light write request to Firebase to test validation
            val jsonPayload = JSONObject().apply {
                put("ping_utc", System.currentTimeMillis())
                put("device", "Android Client")
                put("status", "Reachable")
            }.toString()

            val request = Request.Builder()
                .url(url)
                .put(jsonPayload.toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            client.newCall(request).execute().use { response ->
                val elapsed = System.currentTimeMillis() - startTime
                if (response.isSuccessful) {
                    ConnectionResult(
                        isSuccess = true,
                        latencyMs = elapsed,
                        message = "Connected successfully. Database is fully writable!"
                    )
                } else {
                    ConnectionResult(
                        isSuccess = false,
                        latencyMs = elapsed,
                        message = "HTTP error code: ${response.code}. Check credentials / Rules."
                    )
                }
            }
        } catch (e: Exception) {
            val elapsed = System.currentTimeMillis() - startTime
            Log.e("FirebaseSync", "Connection failed", e)
            ConnectionResult(
                isSuccess = false,
                latencyMs = elapsed,
                message = "Reachable failure: ${e.localizedMessage ?: "Unknown Error"}"
            )
        }
    }

    suspend fun pushLocalDataToFirebase(repository: SfaRepository): SyncResult = withContext(Dispatchers.IO) {
        try {
            // 1. Gather local data from Flows
            val attendances = repository.allAttendance.first()
            val tourPlans = repository.allTourPlans.first()
            val orders = repository.allOrderBookings.first()
            val audits = repository.allCompetitorAudits.first()
            val adrs = repository.allADRReports.first()

            // 2. Build root JSON Object representing unified synchronization payload
            val rootObj = JSONObject()

            // Map Attendance List
            val attendanceArr = JSONArray()
            attendances.forEach { att ->
                val attObj = JSONObject().apply {
                    put("id", att.id)
                    put("timestamp", att.timestamp)
                    put("action", att.action)
                    put("clinic", att.clinic)
                    put("location", att.location)
                    put("latitude", att.latitude)
                    put("longitude", att.longitude)
                }
                attendanceArr.put(attObj)
            }
            rootObj.put("attendance", attendanceArr)

            // Map Tour Plans
            val plansArr = JSONArray()
            tourPlans.forEach { tp ->
                val tpObj = JSONObject().apply {
                    put("id", tp.id)
                    put("bsDate", tp.bsDate)
                    put("doctorOrClinic", tp.doctorOrClinic)
                    put("objectives", tp.objectives)
                    put("status", tp.status)
                }
                plansArr.put(tpObj)
            }
            rootObj.put("tour_plans", plansArr)

            // Map Order Bookings
            val ordersArr = JSONArray()
            orders.forEach { ord ->
                val ordObj = JSONObject().apply {
                    put("id", ord.id)
                    put("chemistName", ord.chemistName)
                    put("productName", ord.productName)
                    put("quantity", ord.quantity)
                    put("schemeText", ord.schemeText)
                    put("totalAmount", ord.totalAmount)
                    put("timestamp", ord.timestamp)
                }
                ordersArr.put(ordObj)
            }
            rootObj.put("order_bookings", ordersArr)

            // Map Competitor Audits
            val auditsArr = JSONArray()
            audits.forEach { aud ->
                val audObj = JSONObject().apply {
                    put("id", aud.id)
                    put("chemistName", aud.chemistName)
                    put("myzithQty", aud.myzithQty)
                    put("compAQty", aud.compAQty)
                    put("compBQty", aud.compBQty)
                    put("timestamp", aud.timestamp)
                }
                auditsArr.put(audObj)
            }
            rootObj.put("competitor_audits", auditsArr)

            // Map ADR Reports
            val adrsArr = JSONArray()
            adrs.forEach { adr ->
                val adrObj = JSONObject().apply {
                    put("id", adr.id)
                    put("patientInitials", adr.patientInitials)
                    put("patientAge", adr.patientAge)
                    put("suspectedProduct", adr.suspectedProduct)
                    put("reaction", adr.reaction)
                    put("severity", adr.severity)
                    put("reporterName", adr.reporterName)
                    put("timestamp", adr.timestamp)
                }
                adrsArr.put(adrObj)
            }
            rootObj.put("adr_reports", adrsArr)

            // Put sync timestamp
            rootObj.put("last_synchronized_at", System.currentTimeMillis())

            // 3. Put request directly to Firebase SFA branch
            val endpointUrl = buildRequestUrl("sopan_store")
            val requestBody = rootObj.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url(endpointUrl)
                .put(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    SyncResult.Success("Pushed ${attendances.size} Attendance logs, ${tourPlans.size} Tour plans, ${orders.size} POB bookings, ${audits.size} Competitor Audits & ${adrs.size} ADR safety claims successfully!")
                } else {
                    SyncResult.Error("Cloud push failed. Server returned: HTTP ${response.code} (${response.message})")
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Push error", e)
            SyncResult.Error("Network execution failure: ${e.localizedMessage ?: "Check WiFi/Data settings"}")
        }
    }

    suspend fun pullDataFromFirebase(repository: SfaRepository): SyncResult = withContext(Dispatchers.IO) {
        try {
            val endpointUrl = buildRequestUrl("sopan_store")
            val request = Request.Builder().url(endpointUrl).get().build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext SyncResult.Error("Cloud pull failed. HTTP ${response.code}")
                }
                val bodyStr = response.body?.string() ?: ""
                if (bodyStr == "null" || bodyStr.trim().isEmpty() || bodyStr.trim() == "{}") {
                    return@withContext SyncResult.Success("Cloud database is currently empty. Ready to upload first data points!")
                }

                val rootObj = JSONObject(bodyStr)

                // 1. Process Attendance
                var attendanceCount = 0
                if (rootObj.has("attendance")) {
                    val arr = rootObj.getJSONArray("attendance")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val id = o.optInt("id", 0)
                        val att = AttendanceEntity(
                            id = id,
                            timestamp = o.optLong("timestamp", System.currentTimeMillis()),
                            action = o.optString("action", "PUNCH IN"),
                            clinic = o.optString("clinic", ""),
                            location = o.optString("location", ""),
                            latitude = o.optDouble("latitude", 27.67),
                            longitude = o.optDouble("longitude", 85.32)
                        )
                        repository.insertAttendance(att)
                        attendanceCount++
                    }
                }

                // 2. Process Tour Plans
                var tourCount = 0
                if (rootObj.has("tour_plans")) {
                    val arr = rootObj.getJSONArray("tour_plans")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val id = o.optInt("id", 0)
                        val tp = TourPlanEntity(
                            id = id,
                            bsDate = o.optString("bsDate", ""),
                            doctorOrClinic = o.optString("doctorOrClinic", ""),
                            objectives = o.optString("objectives", ""),
                            status = o.optString("status", "Planned")
                        )
                        repository.insertTourPlan(tp)
                        tourCount++
                    }
                }

                // 3. Process Order Bookings
                var orderCount = 0
                if (rootObj.has("order_bookings")) {
                    val arr = rootObj.getJSONArray("order_bookings")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val id = o.optInt("id", 0)
                        val ord = OrderBookingEntity(
                            id = id,
                            chemistName = o.optString("chemistName", ""),
                            productName = o.optString("productName", ""),
                            quantity = o.optInt("quantity", 0),
                            schemeText = o.optString("schemeText", ""),
                            totalAmount = o.optDouble("totalAmount", 0.0),
                            timestamp = o.optLong("timestamp", System.currentTimeMillis())
                        )
                        repository.insertOrderBooking(ord)
                        orderCount++
                    }
                }

                // 4. Process Competitor Audits
                var auditCount = 0
                if (rootObj.has("competitor_audits")) {
                    val arr = rootObj.getJSONArray("competitor_audits")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val id = o.optInt("id", 0)
                        val aud = CompetitorAuditEntity(
                            id = id,
                            chemistName = o.optString("chemistName", ""),
                            myzithQty = o.optInt("myzithQty", 0),
                            compAQty = o.optInt("compAQty", 0),
                            compBQty = o.optInt("compBQty", 0),
                            timestamp = o.optLong("timestamp", System.currentTimeMillis())
                        )
                        repository.insertCompetitorAudit(aud)
                        auditCount++
                    }
                }

                // 5. Process ADR Reports
                var adrCount = 0
                if (rootObj.has("adr_reports")) {
                    val arr = rootObj.getJSONArray("adr_reports")
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        val id = o.optInt("id", 0)
                        val adr = ADRReportEntity(
                            id = id,
                            patientInitials = o.optString("patientInitials", ""),
                            patientAge = o.optString("patientAge", ""),
                            suspectedProduct = o.optString("suspectedProduct", ""),
                            reaction = o.optString("reaction", ""),
                            severity = o.optString("severity", "Mild"),
                            reporterName = o.optString("reporterName", ""),
                            timestamp = o.optLong("timestamp", System.currentTimeMillis())
                        )
                        repository.insertADRReport(adr)
                        adrCount++
                    }
                }

                SyncResult.Success("Pulled & integrated from Firebase: $attendanceCount Attendance, $tourCount Tour plans, $orderCount POB Bookings, $auditCount Competitor audits & $adrCount Pharmacovigilance safety logs!")
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Pull error", e)
            SyncResult.Error("Network pull exception: ${e.localizedMessage ?: "Check WiFi/Data settings"}")
        }
    }
}
