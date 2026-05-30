package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SfaDao {
    // Attendance
    @Query("SELECT * FROM attendance ORDER BY timestamp DESC")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity)

    // Tour Plans
    @Query("SELECT * FROM tour_plans ORDER BY id DESC")
    fun getAllTourPlans(): Flow<List<TourPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTourPlan(plan: TourPlanEntity)

    @Update
    suspend fun updateTourPlan(plan: TourPlanEntity)

    @Query("DELETE FROM tour_plans WHERE id = :id")
    suspend fun deleteTourPlanById(id: Int)

    // Order Bookings
    @Query("SELECT * FROM order_bookings ORDER BY timestamp DESC")
    fun getAllOrderBookings(): Flow<List<OrderBookingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderBooking(order: OrderBookingEntity)

    @Query("DELETE FROM order_bookings WHERE id = :id")
    suspend fun deleteOrderBookingById(id: Int)

    // Competitor Audits (RCPA)
    @Query("SELECT * FROM competitor_audits ORDER BY timestamp DESC")
    fun getAllCompetitorAudits(): Flow<List<CompetitorAuditEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompetitorAudit(audit: CompetitorAuditEntity)

    // ADR Reports (Pharmacovigilance)
    @Query("SELECT * FROM adr_reports ORDER BY timestamp DESC")
    fun getAllADRReports(): Flow<List<ADRReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertADRReport(report: ADRReportEntity)

    // DCR (Daily Call Reports)
    @Query("SELECT * FROM dcr_reports ORDER BY timestamp DESC")
    fun getAllDcrReports(): Flow<List<DcrReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDcrReport(dcr: DcrReportEntity)

    @Update
    suspend fun updateDcrReport(dcr: DcrReportEntity)

    @Query("DELETE FROM dcr_reports WHERE id = :id")
    suspend fun deleteDcrReportById(id: Int)

    // ==========================================
    // NEW NEPAL SFA DAO METHODS (ADMIN SUITE)
    // ==========================================

    @Query("SELECT * FROM divisions ORDER BY id ASC")
    fun getAllDivisions(): Flow<List<DivisionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDivision(division: DivisionEntity)

    @Query("DELETE FROM divisions")
    suspend fun clearDivisions()

    @Query("SELECT * FROM territories ORDER BY id ASC")
    fun getAllTerritories(): Flow<List<TerritoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerritory(territory: TerritoryEntity)

    @Query("DELETE FROM territories")
    suspend fun clearTerritories()

    @Query("SELECT * FROM employees ORDER BY id DESC")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: EmployeeEntity)

    @Query("DELETE FROM employees WHERE id = :id")
    suspend fun deleteEmployeeById(id: Int)

    @Query("DELETE FROM employees")
    suspend fun clearEmployees()

    @Query("SELECT * FROM partners ORDER BY id DESC")
    fun getAllPartners(): Flow<List<PartnerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPartner(partner: PartnerEntity)

    @Query("DELETE FROM partners WHERE id = :id")
    suspend fun deletePartnerById(id: Int)

    @Query("DELETE FROM partners")
    suspend fun clearPartners()

    @Query("SELECT * FROM therapeutic_categories ORDER BY id ASC")
    fun getAllTherapeuticCategories(): Flow<List<TherapeuticCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTherapeuticCategory(category: TherapeuticCategoryEntity)

    @Query("SELECT * FROM molecules ORDER BY id ASC")
    fun getAllMolecules(): Flow<List<MoleculeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMolecule(molecule: MoleculeEntity)

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Int)

    @Query("DELETE FROM products")
    suspend fun clearProducts()

    @Query("SELECT * FROM product_prices ORDER BY id DESC")
    fun getAllProductPrices(): Flow<List<ProductPriceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductPrice(price: ProductPriceEntity)

    @Query("SELECT * FROM product_schemes ORDER BY id DESC")
    fun getAllProductSchemes(): Flow<List<ProductSchemeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProductScheme(scheme: ProductSchemeEntity)
}

@Database(
    entities = [
        AttendanceEntity::class,
        TourPlanEntity::class,
        OrderBookingEntity::class,
        CompetitorAuditEntity::class,
        ADRReportEntity::class,
        DivisionEntity::class,
        TerritoryEntity::class,
        EmployeeEntity::class,
        PartnerEntity::class,
        TherapeuticCategoryEntity::class,
        MoleculeEntity::class,
        ProductEntity::class,
        ProductPriceEntity::class,
        ProductSchemeEntity::class,
        DcrReportEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sfaDao(): SfaDao
}
