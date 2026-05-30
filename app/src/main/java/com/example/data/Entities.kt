package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val action: String, // "IN" or "OUT"
    val clinic: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "tour_plans")
data class TourPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val bsDate: String, // Bikram Sambat date e.g. "2083-02-15"
    val doctorOrClinic: String,
    val objectives: String,
    val status: String = "Planned" // "Planned", "Approved", "Visited"
)

@Entity(tableName = "order_bookings")
data class OrderBookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chemistName: String,
    val productName: String,
    val quantity: Int,
    val schemeText: String, // e.g. "10+1 Scheme" or "No Schemes"
    val totalAmount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "competitor_audits")
data class CompetitorAuditEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val chemistName: String,
    val myzithQty: Int,
    val compAQty: Int,
    val compBQty: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "adr_reports")
data class ADRReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val patientInitials: String,
    val patientAge: String,
    val suspectedProduct: String,
    val reaction: String,
    val severity: String, // "Mild", "Moderate", "Severe"
    val reporterName: String,
    val timestamp: Long = System.currentTimeMillis()
)

// ==========================================
// NEW NEPAL PHARMA SFA ENTITIES (ADMIN PANEL)
// ==========================================

@Entity(tableName = "divisions")
data class DivisionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String // e.g., "Rx", "Generic", "OTC", "Critical Care", "Veterinary", "Surgical"
)

@Entity(tableName = "territories")
data class TerritoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "Zone", "Region", "Area", "HQ"
    val name: String, // e.g., "Bagmati", "Koshi", "Kathmandu-A", "Biratnagar Depot"
    val parentId: Int = 0 // self-referencing hierarchy
)

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val code: String,
    val role: String, // e.g. "Chairman", "CEO", "Marketing Director", "Marketing Head", "Division Head", "ZSM", "RSM", "ASM", "MR", "Admin", "Accounts"
    val divisionId: Int = 0,
    val territoryId: Int = 0,
    val reportsToId: Int = 0, // Employee ID self-referencing
    val joiningDate: String,
    val status: String = "Active", // "Active", "On Leave", "Resigned"
    val phone: String,
    val email: String
)

@Entity(tableName = "partners")
data class PartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String, // "super_stockist", "stockist", "wholesaler", "retail_pharmacy", "hospital_pharmacy", "institution", "chain_pharmacy"
    val parentPartnerId: Int = 0, // e.g., retail_pharmacy points to stockist
    val territoryId: Int = 0,
    val ownerName: String,
    val contacts: String,
    val panVat: String,
    val ddaLicenseNo: String,
    val licenseExpiry: String, // Date e.g., "2028-12-15"
    val creditLimitNpr: Double,
    val creditDays: Int,
    val openingBalance: Double,
    val status: String = "Active", // "Active", "Suspended", "Insolvent"
    val assignedMrId: Int = 0 // Assigned Medical Representative Employee ID
)

@Entity(tableName = "therapeutic_categories")
data class TherapeuticCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String // e.g. "Antibiotic", "Gastro", "Neuropathic Pain", "Antigout"
)

@Entity(tableName = "molecules")
data class MoleculeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String, // e.g., "Esomeprazole"
    val strength: String // e.g., "40mg"
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brand: String, // e.g. "Ulshield 20/40"
    val divisionId: Int = 0,
    val categoryId: Int = 0,
    val moleculeId: Int = 0,
    val form: String, // "Tablets", "Capsules", "Syrup", "Inj", "Cream", "Ointment"
    val packSize: String, // e.g. "10x10 ALUALU"
    val hsnCode: String,
    val ddaSchedule: String, // e.g. "A", "B", "C", "G", "Narcotic"
    val tripsStatus: String, // "Patent", "Generics"
    val isActive: Boolean = true,
    val launchedOn: String,
    val imageUrl: String = ""
)

@Entity(tableName = "product_prices")
data class ProductPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val mrpNpr: Double,
    val ptr: Double,             // Price are in NPR to Retailer
    val pts: Double,             // Price are in NPR to Stockist
    val marginRetailerPct: Double,
    val marginStockistPct: Double,
    val effectiveFrom: String,
    val effectiveTo: String = "Active"
)

@Entity(tableName = "product_schemes")
data class ProductSchemeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val schemeText: String, // e.g. "Buy 10 Get 1 Free (10+1)"
    val discountPct: Double = 0.0,
    val validFrom: String,
    val validTo: String,
    val applicablePartnerType: String // "retail_pharmacy", "all", etc.
)

@Entity(tableName = "dcr_reports")
data class DcrReportEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,             // BS Date of visit (e.g., "2083-02-15")
    val mrName: String,           // MR who reported
    val doctorName: String,       // Target Doctor
    val specialty: String,        // e.g., "Cardiologist", "Pediatrician", etc.
    val clinicName: String,       // Clinic/Hospital
    val detailedProduct: String,  // Primary Brand detailed
    val samplesGiven: Int,        // Number of samples gifted
    val inputsDistributed: String,// e.g., "Visual Aid", "Literature (LBL)", "Brand Calendar", "None"
    val doctorReaction: String,   // "Highly Interested", "Interested", "Indifferent", "Demanded Samples"
    val nextFollowUpDate: String, // Future visit BS date
    val status: String = "Pending", // "Pending", "Approved", "Reviewed", "Rejected"
    val managerRemarks: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

