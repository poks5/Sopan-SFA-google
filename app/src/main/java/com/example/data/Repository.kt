package com.example.data

import kotlinx.coroutines.flow.Flow

class SfaRepository(private val sfaDao: SfaDao) {
    // Attendance
    val allAttendance: Flow<List<AttendanceEntity>> = sfaDao.getAllAttendance()
    suspend fun insertAttendance(attendance: AttendanceEntity) = sfaDao.insertAttendance(attendance)

    // Tour Plans
    val allTourPlans: Flow<List<TourPlanEntity>> = sfaDao.getAllTourPlans()
    suspend fun insertTourPlan(plan: TourPlanEntity) = sfaDao.insertTourPlan(plan)
    suspend fun updateTourPlan(plan: TourPlanEntity) = sfaDao.updateTourPlan(plan)
    suspend fun deleteTourPlan(id: Int) = sfaDao.deleteTourPlanById(id)

    // Order Bookings
    val allOrderBookings: Flow<List<OrderBookingEntity>> = sfaDao.getAllOrderBookings()
    suspend fun insertOrderBooking(order: OrderBookingEntity) = sfaDao.insertOrderBooking(order)
    suspend fun deleteOrderBooking(id: Int) = sfaDao.deleteOrderBookingById(id)

    // Competitor Audits
    val allCompetitorAudits: Flow<List<CompetitorAuditEntity>> = sfaDao.getAllCompetitorAudits()
    suspend fun insertCompetitorAudit(audit: CompetitorAuditEntity) = sfaDao.insertCompetitorAudit(audit)

    // ADR Reports (Pharmacovigilance)
    val allADRReports: Flow<List<ADRReportEntity>> = sfaDao.getAllADRReports()
    suspend fun insertADRReport(report: ADRReportEntity) = sfaDao.insertADRReport(report)

    // DCR (Daily Call Reports)
    val allDcrReports: Flow<List<DcrReportEntity>> = sfaDao.getAllDcrReports()
    suspend fun insertDcrReport(dcr: DcrReportEntity) = sfaDao.insertDcrReport(dcr)
    suspend fun updateDcrReport(dcr: DcrReportEntity) = sfaDao.updateDcrReport(dcr)
    suspend fun deleteDcrReport(id: Int) = sfaDao.deleteDcrReportById(id)

    // ==========================================
    // NEW SFA WRAPPERS (ADMIN SUITE)
    // ==========================================
    val allDivisions: Flow<List<DivisionEntity>> = sfaDao.getAllDivisions()
    suspend fun insertDivision(division: DivisionEntity) = sfaDao.insertDivision(division)
    suspend fun clearDivisions() = sfaDao.clearDivisions()

    val allTerritories: Flow<List<TerritoryEntity>> = sfaDao.getAllTerritories()
    suspend fun insertTerritory(territory: TerritoryEntity) = sfaDao.insertTerritory(territory)
    suspend fun clearTerritories() = sfaDao.clearTerritories()

    val allEmployees: Flow<List<EmployeeEntity>> = sfaDao.getAllEmployees()
    suspend fun insertEmployee(employee: EmployeeEntity) = sfaDao.insertEmployee(employee)
    suspend fun deleteEmployee(id: Int) = sfaDao.deleteEmployeeById(id)
    suspend fun clearEmployees() = sfaDao.clearEmployees()

    val allPartners: Flow<List<PartnerEntity>> = sfaDao.getAllPartners()
    suspend fun insertPartner(partner: PartnerEntity) = sfaDao.insertPartner(partner)
    suspend fun deletePartner(id: Int) = sfaDao.deletePartnerById(id)
    suspend fun clearPartners() = sfaDao.clearPartners()

    val allTherapeuticCategories: Flow<List<TherapeuticCategoryEntity>> = sfaDao.getAllTherapeuticCategories()
    suspend fun insertTherapeuticCategory(category: TherapeuticCategoryEntity) = sfaDao.insertTherapeuticCategory(category)

    val allMolecules: Flow<List<MoleculeEntity>> = sfaDao.getAllMolecules()
    suspend fun insertMolecule(molecule: MoleculeEntity) = sfaDao.insertMolecule(molecule)

    val allProducts: Flow<List<ProductEntity>> = sfaDao.getAllProducts()
    suspend fun insertProduct(product: ProductEntity) = sfaDao.insertProduct(product)
    suspend fun deleteProduct(id: Int) = sfaDao.deleteProductById(id)
    suspend fun clearProducts() = sfaDao.clearProducts()

    val allProductPrices: Flow<List<ProductPriceEntity>> = sfaDao.getAllProductPrices()
    suspend fun insertProductPrice(price: ProductPriceEntity) = sfaDao.insertProductPrice(price)

    val allProductSchemes: Flow<List<ProductSchemeEntity>> = sfaDao.getAllProductSchemes()
    suspend fun insertProductScheme(scheme: ProductSchemeEntity) = sfaDao.insertProductScheme(scheme)
}
