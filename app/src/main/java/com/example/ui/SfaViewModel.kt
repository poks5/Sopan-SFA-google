package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SfaViewModel(private val repository: SfaRepository) : ViewModel() {

    // Firebase Sync States
    private var syncManager: FirebaseSyncManager? = null

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow("Firebase Client Idle. Enter credentials to sync.")
    val syncStatusMessage: StateFlow<String> = _syncStatusMessage.asStateFlow()

    private val _connectionTestResult = MutableStateFlow<ConnectionResult?>(null)
    val connectionTestResult: StateFlow<ConnectionResult?> = _connectionTestResult.asStateFlow()

    fun getSyncManager(context: Context): FirebaseSyncManager {
        if (syncManager == null) {
            syncManager = FirebaseSyncManager(context.applicationContext)
        }
        return syncManager!!
    }

    fun testFirebase(context: Context) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatusMessage.value = "Pinging Database Endpoint..."
            val result = getSyncManager(context).testConnection()
            _connectionTestResult.value = result
            _syncStatusMessage.value = result.message
            _isSyncing.value = false
        }
    }

    fun pushToFirebase(context: Context) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatusMessage.value = "Uploading local records up to cloud..."
            val result = getSyncManager(context).pushLocalDataToFirebase(repository)
            when (result) {
                is SyncResult.Success -> {
                    _syncStatusMessage.value = result.message
                }
                is SyncResult.Error -> {
                    _syncStatusMessage.value = "Upload failed: ${result.reason}"
                }
            }
            _isSyncing.value = false
        }
    }

    fun pullFromFirebase(context: Context) {
        viewModelScope.launch {
            _isSyncing.value = true
            _syncStatusMessage.value = "Downloading cloud tables to local..."
            val result = getSyncManager(context).pullDataFromFirebase(repository)
            when (result) {
                is SyncResult.Success -> {
                    _syncStatusMessage.value = result.message
                }
                is SyncResult.Error -> {
                    _syncStatusMessage.value = "Download failed: ${result.reason}"
                }
            }
            _isSyncing.value = false
        }
    }

    fun saveFirebaseConfig(context: Context, url: String, secret: String) {
        getSyncManager(context).saveConfig(url, secret)
        _syncStatusMessage.value = "Cloud path parameters successfully updated!"
    }

    // Authentication States
    private val _userRole = MutableStateFlow("MR") // Default to MR
    val userRole: StateFlow<String> = _userRole.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loggedInUser = MutableStateFlow("")
    val loggedInUser: StateFlow<String> = _loggedInUser.asStateFlow()
    
    private val _loggedUserId = MutableStateFlow(0) // Added for RLS
    val loggedUserId: StateFlow<Int> = _loggedUserId.asStateFlow()

    // UI States observed from Database Room Flow
    val allAttendance = repository.allAttendance.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTourPlans = repository.allTourPlans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allOrderBookings = repository.allOrderBookings.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCompetitorAudits = repository.allCompetitorAudits.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allADRReports = repository.allADRReports.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allDcrReports = repository.allDcrReports.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Derived dashboard metrics (Dynamic calculation based on flows)
    val totalOrdersCount = allOrderBookings.map { it.size }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    val totalSalesVolume = allOrderBookings.map { list -> list.sumOf { it.totalAmount } }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    
    // Attendance helpers
    private val _isPunchedIn = MutableStateFlow(false)
    val isPunchedIn: StateFlow<Boolean> = _isPunchedIn.asStateFlow()

    private val _lastClinic = MutableStateFlow("")
    val lastClinic: StateFlow<String> = _lastClinic.asStateFlow()



    fun login(role: String, userName: String, userId: Int = 0) {
        _userRole.value = role
        _loggedInUser.value = userName
        _loggedUserId.value = userId
        _isLoggedIn.value = true
    }

    fun logout() {
        _isLoggedIn.value = false
        _loggedInUser.value = ""
    }

    fun punchIn(clinic: String, location: String, lat: Double = 27.67, lng: Double = 85.32) {
        viewModelScope.launch {
            val record = AttendanceEntity(
                action = "PUNCH IN",
                clinic = clinic,
                location = location,
                latitude = lat,
                longitude = lng
            )
            repository.insertAttendance(record)
        }
    }

    fun punchOut(clinic: String, location: String, lat: Double = 27.67, lng: Double = 85.32) {
        viewModelScope.launch {
            val record = AttendanceEntity(
                action = "PUNCH OUT",
                clinic = clinic,
                location = location,
                latitude = lat,
                longitude = lng
            )
            repository.insertAttendance(record)
        }
    }

    fun addTourPlan(bsDate: String, doctorOrClinic: String, objectives: String) {
        viewModelScope.launch {
            val plan = TourPlanEntity(
                bsDate = bsDate,
                doctorOrClinic = doctorOrClinic,
                objectives = objectives,
                status = "Planned"
            )
            repository.insertTourPlan(plan)
        }
    }

    fun updateTourPlanStatus(plan: TourPlanEntity, newStatus: String) {
        viewModelScope.launch {
            repository.updateTourPlan(plan.copy(status = newStatus))
        }
    }

    fun deleteTourPlan(id: Int) {
        viewModelScope.launch {
            repository.deleteTourPlan(id)
        }
    }

    fun addOrderBooking(chemistName: String, productName: String, quantity: Int, totalAmount: Double, schemeText: String) {
        viewModelScope.launch {
            val order = OrderBookingEntity(
                chemistName = chemistName,
                productName = productName,
                quantity = quantity,
                schemeText = schemeText,
                totalAmount = totalAmount
            )
            repository.insertOrderBooking(order)
        }
    }

    fun addCompetitorAudit(chemistName: String, myzithQty: Int, compAQty: Int, compBQty: Int) {
        viewModelScope.launch {
            val audit = CompetitorAuditEntity(
                chemistName = chemistName,
                myzithQty = myzithQty,
                compAQty = compAQty,
                compBQty = compBQty
            )
            repository.insertCompetitorAudit(audit)
        }
    }

    fun addADRReport(
        patientInitials: String,
        patientAge: String,
        suspectedProduct: String,
        reaction: String,
        severity: String,
        reporterName: String
    ) {
        viewModelScope.launch {
            val report = ADRReportEntity(
                patientInitials = patientInitials,
                patientAge = patientAge,
                suspectedProduct = suspectedProduct,
                reaction = reaction,
                severity = severity,
                reporterName = reporterName
            )
            repository.insertADRReport(report)
        }
    }

    // ==========================================
    // EXPOSURE OF NEW NEPAL SFA FLOWS
    // ==========================================
    val allDivisions = repository.allDivisions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTerritories = repository.allTerritories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allEmployees = repository.allEmployees.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Filtered data based on loggedUserId and Role
    val filteredEmployees = combine(allEmployees, loggedUserId, userRole) { employees, userId, role ->
        if (userId == 0) return@combine employees // Or handle unauthenticated state
        when (role) {
            "CEO", "Chairman" -> employees
            "Chief Business Director" -> employees.filter { it.role != "Chairman" && it.role != "CEO" }
            "Division Head" -> employees.filter { it.role == "ZSM" || it.role == "MR" || it.role == "ASM" }
            else -> employees.filter { it.id == userId || it.reportsToId == userId }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Maps for efficient lookups
    val employeesMap = allEmployees.map { list -> list.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    
    val divisionsMap = allDivisions.map { list -> list.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
        
    val territoriesMap = allTerritories.map { list -> list.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val allPartners = repository.allPartners.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allTherapeuticCategories = repository.allTherapeuticCategories.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMolecules = repository.allMolecules.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allProducts = repository.allProducts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allProductPrices = repository.allProductPrices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allProductSchemes = repository.allProductSchemes.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    init {
        // Track the current Punch Status from the most recent attendance log
        viewModelScope.launch {
            allAttendance.collect { list ->
                if (list.isNotEmpty()) {
                    val latest = list.first()
                    _isPunchedIn.value = latest.action == "PUNCH IN"
                    _lastClinic.value = latest.clinic
                }
            }
        }
        seedDatabaseIfEmpty()
    }

    private fun seedDatabaseIfEmpty() {
        viewModelScope.launch {
            val list = repository.allDivisions.first()
            if (list.isEmpty()) {
                executeSeeding()
            }
        }
    }

    private suspend fun executeSeeding() {
        // 1. Divisions
        repository.insertDivision(DivisionEntity(id = 1, name = "Rx (Prescription)"))
        repository.insertDivision(DivisionEntity(id = 2, name = "Generic Care"))
        repository.insertDivision(DivisionEntity(id = 3, name = "OTC"))
        repository.insertDivision(DivisionEntity(id = 4, name = "Critical Care"))
        repository.insertDivision(DivisionEntity(id = 5, name = "Veterinary"))
        repository.insertDivision(DivisionEntity(id = 6, name = "Surgical"))

        // 2. Territories
        repository.insertTerritory(TerritoryEntity(id = 1, type = "Zone", name = "Bagmati Zone", parentId = 0))
        repository.insertTerritory(TerritoryEntity(id = 2, type = "Zone", name = "Gandaki Zone", parentId = 0))
        repository.insertTerritory(TerritoryEntity(id = 3, type = "Zone", name = "Koshi Zone", parentId = 0))
        repository.insertTerritory(TerritoryEntity(id = 4, type = "Region", name = "Kathmandu Region", parentId = 1))
        repository.insertTerritory(TerritoryEntity(id = 5, type = "Region", name = "Pokhara Region", parentId = 2))
        repository.insertTerritory(TerritoryEntity(id = 6, type = "Region", name = "Biratnagar Region", parentId = 3))
        repository.insertTerritory(TerritoryEntity(id = 7, type = "Area", name = "Lalitpur Area", parentId = 4))
        repository.insertTerritory(TerritoryEntity(id = 8, type = "Area", name = "Bhaktapur Area", parentId = 4))
        repository.insertTerritory(TerritoryEntity(id = 9, type = "Area", name = "Pokhara City Area", parentId = 5))
        repository.insertTerritory(TerritoryEntity(id = 10, type = "Area", name = "Dharan Area", parentId = 6))
        repository.insertTerritory(TerritoryEntity(id = 11, type = "HQ", name = "Kathmandu Central HQ", parentId = 7))
        repository.insertTerritory(TerritoryEntity(id = 12, type = "HQ", name = "Pokhara Valley HQ", parentId = 9))
        repository.insertTerritory(TerritoryEntity(id = 13, type = "HQ", name = "Dharan Bazar HQ", parentId = 10))

        // 3. Employees
        repository.insertEmployee(EmployeeEntity(id = 1, name = "Er. Sopan Shrestha", code = "CH01", role = "Chairman", divisionId = 1, territoryId = 11, reportsToId = 0, joiningDate = "2020-01-01", phone = "9851012345", email = "chairman@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 2, name = "Dr. Ramesh Adhikari", code = "CEO2", role = "CEO", divisionId = 1, territoryId = 11, reportsToId = 1, joiningDate = "2021-05-10", phone = "9851023456", email = "ceo@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 3, name = "Mr. Binod Prasai", code = "MDIR3", role = "Marketing Director", divisionId = 1, territoryId = 11, reportsToId = 2, joiningDate = "2022-03-15", phone = "9841324567", email = "bprasai@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 4, name = "Mr. Sunil Thapa", code = "MHD4", role = "Marketing Head", divisionId = 1, territoryId = 11, reportsToId = 3, joiningDate = "2022-11-20", phone = "9841876543", email = "sthapa@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 5, name = "Mr. Kedar Neupane", code = "DHD5", role = "Division Head", divisionId = 1, territoryId = 11, reportsToId = 4, joiningDate = "2023-01-10", phone = "9851054321", email = "kneupane@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 6, name = "Mr. Ganesh Karki", code = "ZSM6", role = "ZSM", divisionId = 1, territoryId = 11, reportsToId = 5, joiningDate = "2023-06-01", phone = "9851098765", email = "gkarki@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 7, name = "Mr. Pradeep Pokhrel", code = "RSM7", role = "RSM", divisionId = 1, territoryId = 4, reportsToId = 6, joiningDate = "2023-09-15", phone = "9851122334", email = "ppokhrel@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 8, name = "Mr. Bikram Giri", code = "ASM8", role = "ASM", divisionId = 1, territoryId = 7, reportsToId = 7, joiningDate = "2024-02-10", phone = "9851234123", email = "bgiri@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 9, name = "Mr. Santosh Shrestha", code = "MR09", role = "MR", divisionId = 1, territoryId = 11, reportsToId = 8, joiningDate = "2024-05-01", phone = "9860124356", email = "sshrestha@sopanpharma.com"))
        repository.insertEmployee(EmployeeEntity(id = 10, name = "Mr. Dilip Kumar", code = "MR10", role = "MR", divisionId = 2, territoryId = 12, reportsToId = 8, joiningDate = "2024-06-15", phone = "9846012345", email = "dkumar@sopanpharma.com"))

        // 4. Partners
        repository.insertPartner(PartnerEntity(id = 1, name = "Nepal Med Super Stockist Depot", type = "super_stockist", parentPartnerId = 0, territoryId = 11, ownerName = "Shyam Sunder", contacts = "9851091234", panVat = "602341234", ddaLicenseNo = "DDA-SS-2034", licenseExpiry = "2028-11-20", creditLimitNpr = 5000000.0, creditDays = 45, openingBalance = 250000.0, status = "Active"))
        repository.insertPartner(PartnerEntity(id = 2, name = "Kathmandu Valley Stockist Inc.", type = "stockist", parentPartnerId = 1, territoryId = 7, ownerName = "Rajesh Gorkhali", contacts = "9841293847", panVat = "301293847", ddaLicenseNo = "DDA-ST-9923", licenseExpiry = "2027-05-15", creditLimitNpr = 1500000.0, creditDays = 30, openingBalance = 320000.0, status = "Active"))
        repository.insertPartner(PartnerEntity(id = 3, name = "Biratnagar Pharma Distributors", type = "stockist", parentPartnerId = 1, territoryId = 6, ownerName = "Bimal Agrawal", contacts = "9852023432", panVat = "402948234", ddaLicenseNo = "DDA-ST-1102", licenseExpiry = "2029-01-10", creditLimitNpr = 2000000.0, creditDays = 30, openingBalance = 150000.0, status = "Active"))
        repository.insertPartner(PartnerEntity(id = 4, name = "Nepal Cancer Hospital Pharmacy", type = "hospital_pharmacy", parentPartnerId = 2, territoryId = 11, ownerName = "Dr. Jitendra Lal", contacts = "01-5123456", panVat = "501928345", ddaLicenseNo = "DDA-H-9924", licenseExpiry = "2026-09-30", creditLimitNpr = 800000.0, creditDays = 60, openingBalance = 45000.0, status = "Active"))
        repository.insertPartner(PartnerEntity(id = 5, name = "Suryodaya Polyclinic & Chemist", type = "retail_pharmacy", parentPartnerId = 2, territoryId = 7, ownerName = "Krishna Pokharel", contacts = "9851122998", panVat = "340912110", ddaLicenseNo = "DDA-R-12345", licenseExpiry = "2027-07-22", creditLimitNpr = 300000.0, creditDays = 15, openingBalance = 120000.0, status = "Active"))
        repository.insertPartner(PartnerEntity(id = 6, name = "Bhatbhateni Wellness Pharmacy", type = "chain_pharmacy", parentPartnerId = 2, territoryId = 11, ownerName = "Min Bahadur Gurung", contacts = "9851020304", panVat = "300405060", ddaLicenseNo = "DDA-C-88210", licenseExpiry = "2028-02-15", creditLimitNpr = 3000000.0, creditDays = 45, openingBalance = 180000.0, status = "Active"))

        // 5. Therapeutic Categories
        repository.insertTherapeuticCategory(TherapeuticCategoryEntity(id = 1, name = "Antidiabetic"))
        repository.insertTherapeuticCategory(TherapeuticCategoryEntity(id = 2, name = "Gastro (Tablets)"))
        repository.insertTherapeuticCategory(TherapeuticCategoryEntity(id = 3, name = "Antibiotic"))
        repository.insertTherapeuticCategory(TherapeuticCategoryEntity(id = 4, name = "Neuropathic Pain"))
        repository.insertTherapeuticCategory(TherapeuticCategoryEntity(id = 5, name = "Antigout (Tablets)"))

        // 6. Molecules
        repository.insertMolecule(MoleculeEntity(id = 1, name = "Esomeprazole", strength = "40mg"))
        repository.insertMolecule(MoleculeEntity(id = 2, name = "Pregabalin", strength = "75mg"))
        repository.insertMolecule(MoleculeEntity(id = 3, name = "Febuxostat", strength = "40mg"))
        repository.insertMolecule(MoleculeEntity(id = 4, name = "Azithromycin", strength = "500mg"))

        // 7. Products & Prices
        repository.insertProduct(ProductEntity(id = 1, brand = "Ulshield 20/40", divisionId = 1, categoryId = 2, moleculeId = 1, form = "Tablets", packSize = "10x10 ALUALU", hsnCode = "3004.90", ddaSchedule = "H", tripsStatus = "Generics", isActive = true, launchedOn = "2022-04-10"))
        repository.insertProductPrice(ProductPriceEntity(id = 1, productId = 1, mrpNpr = 250.0, ptr = 200.0, pts = 180.0, marginRetailerPct = 20.0, marginStockistPct = 10.0, effectiveFrom = "2024-01-01"))

        repository.insertProduct(ProductEntity(id = 2, brand = "Pbin (25/50/75/150)", divisionId = 1, categoryId = 4, moleculeId = 2, form = "Capsules", packSize = "10x10 ALUALU", hsnCode = "3004.90", ddaSchedule = "G", tripsStatus = "Generics", isActive = true, launchedOn = "2023-01-15"))
        repository.insertProductPrice(ProductPriceEntity(id = 2, productId = 2, mrpNpr = 380.0, ptr = 304.0, pts = 273.6, marginRetailerPct = 20.0, marginStockistPct = 10.0, effectiveFrom = "2024-01-01"))

        repository.insertProduct(ProductEntity(id = 3, brand = "Zanstat 40/80", divisionId = 1, categoryId = 5, moleculeId = 3, form = "Tablets", packSize = "10x10 ALUALU", hsnCode = "3004.90", ddaSchedule = "A", tripsStatus = "Generics", isActive = true, launchedOn = "2023-08-01"))
        repository.insertProductPrice(ProductPriceEntity(id = 3, productId = 3, mrpNpr = 320.0, ptr = 256.0, pts = 230.4, marginRetailerPct = 20.0, marginStockistPct = 10.0, effectiveFrom = "2024-01-01"))

        repository.insertProduct(ProductEntity(id = 4, brand = "Myzith (100/200/500)", divisionId = 2, categoryId = 3, moleculeId = 4, form = "Tablets", packSize = "10x3 Blister", hsnCode = "3004.90", ddaSchedule = "H", tripsStatus = "Generics", isActive = true, launchedOn = "2021-11-20"))
        repository.insertProductPrice(ProductPriceEntity(id = 4, productId = 4, mrpNpr = 450.0, ptr = 360.0, pts = 324.0, marginRetailerPct = 20.0, marginStockistPct = 10.0, effectiveFrom = "2024-01-01"))

        // 8. Schemes Description
        repository.insertProductScheme(ProductSchemeEntity(id = 1, productId = 1, schemeText = "Buy 10 Get 1 Free (10+1 Promotion)", discountPct = 10.0, validFrom = "2026-01-01", validTo = "2026-12-31", applicablePartnerType = "retail_pharmacy"))
        repository.insertProductScheme(ProductSchemeEntity(id = 2, productId = 2, schemeText = "Bonus 5% on 5+ packs purchase", discountPct = 5.0, validFrom = "2026-03-01", validTo = "2026-09-30", applicablePartnerType = "retail_pharmacy"))

        // 9. Initial DCR Reports (Fields Activity Logs)
        repository.insertDcrReport(DcrReportEntity(
            date = "2083-02-12",
            mrName = "Sohan Shrestha",
            doctorName = "Dr. Shaswat Sharma",
            specialty = "Cardiologist",
            clinicName = "Norvic International Hospital",
            detailedProduct = "Ulshield 20/40",
            samplesGiven = 15,
            inputsDistributed = "Visual Aid & LBL Pack",
            doctorReaction = "Highly Interested",
            nextFollowUpDate = "2083-02-26",
            status = "Approved",
            managerRemarks = "Strong detailing. Continued focus required."
        ))

        repository.insertDcrReport(DcrReportEntity(
            date = "2083-02-14",
            mrName = "Bipin Thapa",
            doctorName = "Dr. Manisha Shrestha",
            specialty = "Pediatrician",
            clinicName = "Kanti Children's Hospital",
            detailedProduct = "Myzith (100/200/500)",
            samplesGiven = 8,
            inputsDistributed = "Brand Calendar",
            doctorReaction = "Interested",
            nextFollowUpDate = "2083-03-01",
            status = "Pending"
        ))

        repository.insertDcrReport(DcrReportEntity(
            date = "2083-02-15",
            mrName = "Sohan Shrestha",
            doctorName = "Dr. Arun Subedi",
            specialty = "Neurologist",
            clinicName = "Grande International Hospital",
            detailedProduct = "Pbin (25/50/75/150)",
            samplesGiven = 20,
            inputsDistributed = "Visual Aid & Literature",
            doctorReaction = "Demanded Samples",
            nextFollowUpDate = "2083-02-28",
            status = "Pending"
        ))

        repository.insertDcrReport(DcrReportEntity(
            date = "2083-02-15",
            mrName = "Bipin Thapa",
            doctorName = "Dr. Ritesh Upadhaya",
            specialty = "Orthopedic Surgeon",
            clinicName = "Nepal Orthopaedic Hospital",
            detailedProduct = "Zanstat 40/80",
            samplesGiven = 10,
            inputsDistributed = "Visual Aid",
            doctorReaction = "Indifferent",
            nextFollowUpDate = "2083-03-05",
            status = "Pending"
        ))
    }

    // ==========================================
    // SUSPEND CRUD ACTIONS IN VIEWMODEL
    // ==========================================
    fun addEmployee(employee: EmployeeEntity) {
        viewModelScope.launch {
            repository.insertEmployee(employee)
        }
    }

    fun deleteEmployee(id: Int) {
        viewModelScope.launch {
            repository.deleteEmployee(id)
        }
    }

    fun addPartner(partner: PartnerEntity) {
        viewModelScope.launch {
            repository.insertPartner(partner)
        }
    }

    fun deletePartner(id: Int) {
        viewModelScope.launch {
            repository.deletePartner(id)
        }
    }

    fun addProductEntry(product: ProductEntity, price: ProductPriceEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
            repository.insertProductPrice(price)
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun deleteProduct(id: Int) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
    }

    fun deleteOrderBooking(id: Int) {
        viewModelScope.launch {
            repository.deleteOrderBooking(id)
        }
    }

    fun addDcrReport(
        date: String,
        mrName: String,
        doctorName: String,
        specialty: String,
        clinicName: String,
        detailedProduct: String,
        samplesGiven: Int,
        inputsDistributed: String,
        doctorReaction: String,
        nextFollowUpDate: String
    ) {
        viewModelScope.launch {
            val dcr = DcrReportEntity(
                date = date,
                mrName = mrName,
                doctorName = doctorName,
                specialty = specialty,
                clinicName = clinicName,
                detailedProduct = detailedProduct,
                samplesGiven = samplesGiven,
                inputsDistributed = inputsDistributed,
                doctorReaction = doctorReaction,
                nextFollowUpDate = nextFollowUpDate,
                status = "Pending"
            )
            repository.insertDcrReport(dcr)
        }
    }

    fun updateDcrStatus(id: Int, newStatus: String, remarks: String) {
        viewModelScope.launch {
            allDcrReports.value.find { it.id == id }?.let { existing ->
                repository.updateDcrReport(existing.copy(status = newStatus, managerRemarks = remarks))
            }
        }
    }

    fun deleteDcrReport(id: Int) {
        viewModelScope.launch {
            repository.deleteDcrReport(id)
        }
    }

    fun addProductScheme(scheme: ProductSchemeEntity) {
        viewModelScope.launch {
            repository.insertProductScheme(scheme)
        }
    }

    fun addDivision(division: DivisionEntity) {
        viewModelScope.launch {
            repository.insertDivision(division)
        }
    }

    fun addTerritory(territory: TerritoryEntity) {
        viewModelScope.launch {
            repository.insertTerritory(territory)
        }
    }

    fun addMolecule(molecule: MoleculeEntity) {
        viewModelScope.launch {
            repository.insertMolecule(molecule)
        }
    }

    fun addTherapeuticCategory(category: TherapeuticCategoryEntity) {
        viewModelScope.launch {
            repository.insertTherapeuticCategory(category)
        }
    }

    fun factoryResetDatabase() {
        viewModelScope.launch {
            repository.clearDivisions()
            repository.clearTerritories()
            repository.clearEmployees()
            repository.clearPartners()
            repository.clearProducts()
            executeSeeding()
        }
    }
}

// Simple Custom ViewModel Factory for passing repository parameter
class SfaViewModelFactory(private val repository: SfaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SfaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SfaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
