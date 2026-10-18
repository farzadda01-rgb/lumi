package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppointmentEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProviderEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceDao {
    @Query("SELECT * FROM services ORDER BY isFeatured DESC, rating DESC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id")
    fun getServiceById(id: String): Flow<ServiceEntity?>

    @Query("SELECT * FROM services WHERE id = :id")
    suspend fun getServiceByIdOnce(id: String): ServiceEntity?

    @Query("SELECT * FROM services WHERE isFeatured = 1 ORDER BY rating DESC")
    fun getFeaturedServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE category = :category ORDER BY rating DESC")
    fun getServicesByCategory(category: String): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(services: List<ServiceEntity>)

    @Query("SELECT COUNT(*) FROM services")
    suspend fun getCount(): Int
}

@Dao
interface ProviderDao {
    @Query("SELECT * FROM providers ORDER BY rating DESC")
    fun getAllProviders(): Flow<List<ProviderEntity>>

    @Query("SELECT * FROM providers WHERE id = :id")
    fun getProviderById(id: String): Flow<ProviderEntity?>

    @Query("SELECT * FROM providers WHERE id = :id")
    suspend fun getProviderByIdOnce(id: String): ProviderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(providers: List<ProviderEntity>)

    @Query("SELECT COUNT(*) FROM providers")
    suspend fun getCount(): Int
}

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM appointments ORDER BY startTimestamp ASC")
    fun getAllAppointments(): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE id = :id")
    fun getAppointmentById(id: Long): Flow<AppointmentEntity?>

    @Query("SELECT * FROM appointments WHERE id = :id")
    suspend fun getAppointmentByIdOnce(id: Long): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE referenceCode = :code LIMIT 1")
    suspend fun getAppointmentByReference(code: String): AppointmentEntity?

    @Query("SELECT * FROM appointments WHERE status = 'Confirmed' AND startTimestamp >= :nowTimestamp ORDER BY startTimestamp ASC")
    fun getUpcomingAppointments(nowTimestamp: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE status != 'Confirmed' OR startTimestamp < :nowTimestamp ORDER BY startTimestamp DESC")
    fun getPastAppointments(nowTimestamp: Long): Flow<List<AppointmentEntity>>

    @Query("SELECT * FROM appointments WHERE providerId = :providerId AND status = 'Confirmed' AND startTimestamp = :startTimestamp LIMIT 1")
    suspend fun getConflictingAppointment(providerId: String, startTimestamp: Long): AppointmentEntity?

    @Query("SELECT startTimestamp FROM appointments WHERE providerId = :providerId AND status = 'Confirmed' AND startTimestamp >= :dayStart AND startTimestamp < :dayEnd")
    suspend fun getBookedSlotsForDay(providerId: String, dayStart: Long, dayEnd: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: AppointmentEntity): Long

    @Update
    suspend fun updateAppointment(appointment: AppointmentEntity)

    @Query("UPDATE appointments SET status = :newStatus WHERE id = :id")
    suspend fun updateStatus(id: Long, newStatus: String)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: Long)
}

@Dao
interface FavoriteDao {
    @Query("SELECT serviceId FROM favorites")
    fun getAllFavoriteIds(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE serviceId = :serviceId)")
    fun isFavorite(serviceId: String): Flow<Boolean>

    @Query("SELECT COUNT(*) FROM favorites WHERE serviceId = :serviceId")
    suspend fun countFavorite(serviceId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE serviceId = :serviceId")
    suspend fun removeFavorite(serviceId: String)
}

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<NotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllAsRead()
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileOnce(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfileEntity)
}
