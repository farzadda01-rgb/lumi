package com.example.data.repository

import com.example.data.local.LumiDatabase
import com.example.data.local.InitialDemoData
import com.example.data.model.AppointmentEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProviderEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

interface LumiRepository {
    val allServices: Flow<List<ServiceEntity>>
    val featuredServices: Flow<List<ServiceEntity>>
    fun getService(id: String): Flow<ServiceEntity?>
    suspend fun getServiceOnce(id: String): ServiceEntity?
    val allProviders: Flow<List<ProviderEntity>>
    fun getProvider(id: String): Flow<ProviderEntity?>
    suspend fun getProviderOnce(id: String): ProviderEntity?

    val upcomingAppointments: Flow<List<AppointmentEntity>>
    val pastAppointments: Flow<List<AppointmentEntity>>
    fun getAppointment(id: Long): Flow<AppointmentEntity?>

    val favoriteIds: Flow<Set<String>>
    fun isFavorite(serviceId: String): Flow<Boolean>
    suspend fun toggleFavorite(serviceId: String)

    val notifications: Flow<List<NotificationEntity>>
    val unreadNotificationsCount: Flow<Int>
    suspend fun markNotificationAsRead(id: Long)
    suspend fun markAllNotificationsAsRead()

    val userProfile: Flow<UserProfileEntity?>
    suspend fun updateUserProfile(firstName: String, lastName: String, email: String, phone: String)

    suspend fun getBookedSlots(providerId: String, dayStart: Long, dayEnd: Long): List<Long>

    suspend fun bookAppointment(
        serviceId: String,
        providerId: String,
        startTimestamp: Long,
        customerNote: String
    ): Result<AppointmentEntity>

    suspend fun cancelAppointment(appointmentId: Long): Result<Unit>
    suspend fun ensureSeeded()
}

class LumiRepositoryImpl(
    private val database: LumiDatabase
) : LumiRepository {

    private val serviceDao = database.serviceDao()
    private val providerDao = database.providerDao()
    private val appointmentDao = database.appointmentDao()
    private val favoriteDao = database.favoriteDao()
    private val notificationDao = database.notificationDao()
    private val userProfileDao = database.userProfileDao()

    override val allServices: Flow<List<ServiceEntity>> = serviceDao.getAllServices()
    override val featuredServices: Flow<List<ServiceEntity>> = serviceDao.getFeaturedServices()

    override fun getService(id: String): Flow<ServiceEntity?> = serviceDao.getServiceById(id)
    override suspend fun getServiceOnce(id: String): ServiceEntity? = withContext(Dispatchers.IO) {
        serviceDao.getServiceByIdOnce(id)
    }

    override val allProviders: Flow<List<ProviderEntity>> = providerDao.getAllProviders()
    override fun getProvider(id: String): Flow<ProviderEntity?> = providerDao.getProviderById(id)
    override suspend fun getProviderOnce(id: String): ProviderEntity? = withContext(Dispatchers.IO) {
        providerDao.getProviderByIdOnce(id)
    }

    override val upcomingAppointments: Flow<List<AppointmentEntity>> =
        appointmentDao.getUpcomingAppointments(System.currentTimeMillis() - 60_000L) // 1 min grace

    override val pastAppointments: Flow<List<AppointmentEntity>> =
        appointmentDao.getPastAppointments(System.currentTimeMillis() - 60_000L)

    override fun getAppointment(id: Long): Flow<AppointmentEntity?> = appointmentDao.getAppointmentById(id)

    override val favoriteIds: Flow<Set<String>> = favoriteDao.getAllFavoriteIds().map { it.toSet() }

    override fun isFavorite(serviceId: String): Flow<Boolean> = favoriteDao.isFavorite(serviceId)

    override suspend fun toggleFavorite(serviceId: String) = withContext(Dispatchers.IO) {
        val count = favoriteDao.countFavorite(serviceId)
        if (count > 0) {
            favoriteDao.removeFavorite(serviceId)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(serviceId = serviceId))
        }
    }

    suspend fun setFavorite(serviceId: String, isFav: Boolean) = withContext(Dispatchers.IO) {
        if (isFav) {
            favoriteDao.addFavorite(FavoriteEntity(serviceId = serviceId))
        } else {
            favoriteDao.removeFavorite(serviceId)
        }
    }

    override val notifications: Flow<List<NotificationEntity>> = notificationDao.getAllNotifications()
    override val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()

    override suspend fun markNotificationAsRead(id: Long) = withContext(Dispatchers.IO) {
        notificationDao.markAsRead(id)
    }

    override suspend fun markAllNotificationsAsRead() = withContext(Dispatchers.IO) {
        notificationDao.markAllAsRead()
    }

    override val userProfile: Flow<UserProfileEntity?> = userProfileDao.getUserProfile()

    override suspend fun updateUserProfile(
        firstName: String,
        lastName: String,
        email: String,
        phone: String
    ) = withContext(Dispatchers.IO) {
        userProfileDao.saveUserProfile(
            UserProfileEntity(
                id = 1,
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                phoneNumber = phone.trim()
            )
        )
    }

    override suspend fun getBookedSlots(
        providerId: String,
        dayStart: Long,
        dayEnd: Long
    ): List<Long> = withContext(Dispatchers.IO) {
        appointmentDao.getBookedSlotsForDay(providerId, dayStart, dayEnd)
    }

    override suspend fun bookAppointment(
        serviceId: String,
        providerId: String,
        startTimestamp: Long,
        customerNote: String
    ): Result<AppointmentEntity> = withContext(Dispatchers.IO) {
        try {
            // Validation 1: Past time slots must not be selectable
            if (startTimestamp < System.currentTimeMillis() - 120_000L) {
                return@withContext Result.failure(IllegalArgumentException("Selected time slot has already passed."))
            }

            // Validation 2: Service and Provider existence
            val service = serviceDao.getServiceByIdOnce(serviceId)
                ?: return@withContext Result.failure(IllegalStateException("Selected service could not be found."))
            val provider = providerDao.getProviderByIdOnce(providerId)
                ?: return@withContext Result.failure(IllegalStateException("Selected provider could not be found."))

            // Validation 3: Duplicate booking prevention for same provider/time slot
            val conflict = appointmentDao.getConflictingAppointment(providerId, startTimestamp)
            if (conflict != null) {
                return@withContext Result.failure(IllegalStateException("This time slot is already reserved for ${provider.name}. Please choose another time."))
            }

            // Validation 4: Note length limit
            val safeNote = if (customerNote.length > 500) customerNote.take(500) else customerNote

            val endTimestamp = startTimestamp + (service.durationMinutes * 60 * 1000L)
            val randomSuffix = (1000..9999).random()
            val referenceCode = "LUMI-$randomSuffix"

            val appointment = AppointmentEntity(
                referenceCode = referenceCode,
                serviceId = service.id,
                providerId = provider.id,
                startTimestamp = startTimestamp,
                endTimestamp = endTimestamp,
                priceCents = service.priceCents,
                currency = service.currency,
                status = "Confirmed",
                customerNote = safeNote
            )

            val newId = appointmentDao.insertAppointment(appointment)
            val savedAppointment = appointment.copy(id = newId)

            // Add confirmation notification
            notificationDao.insertNotification(
                NotificationEntity(
                    title = "Booking confirmed ($referenceCode)",
                    body = "${service.name} with ${provider.name} has been saved to your local demonstration appointments.",
                    createdAt = System.currentTimeMillis(),
                    isRead = false
                )
            )

            Result.success(savedAppointment)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelAppointment(appointmentId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val appointment = appointmentDao.getAppointmentByIdOnce(appointmentId)
                ?: return@withContext Result.failure(IllegalStateException("Appointment not found."))

            appointmentDao.updateStatus(appointmentId, "Cancelled")

            val service = serviceDao.getServiceByIdOnce(appointment.serviceId)
            val serviceName = service?.name ?: "Appointment"

            notificationDao.insertNotification(
                NotificationEntity(
                    title = "Appointment cancelled",
                    body = "$serviceName (${appointment.referenceCode}) was cancelled in your local demonstration records.",
                    createdAt = System.currentTimeMillis(),
                    isRead = false
                )
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun ensureSeeded() = withContext(Dispatchers.IO) {
        val count = serviceDao.getCount()
        if (count == 0) {
            LumiDatabase.populateInitialData(database)
        }
    }
}
