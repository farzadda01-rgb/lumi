package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val category: String, // "Hair", "Nails", "Skin", "Massage", "Brows", "Wellness"
    val providerId: String,
    val durationMinutes: Int,
    val priceCents: Long,
    val currency: String = "USD",
    val rating: Float,
    val reviewCount: Int,
    val imageDrawableName: String,
    val isFeatured: Boolean = false,
    val whatIncluded: String, // Pipe-separated list of inclusions
    val location: String = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
    val cancellationPolicy: String = "Free cancellation up to 24 hours before your appointment."
) {
    fun getInclusionsList(): List<String> {
        return whatIncluded.split("|").map { it.trim() }.filter { it.isNotEmpty() }
    }
}

@Entity(tableName = "providers")
data class ProviderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val specialty: String,
    val bio: String,
    val rating: Float,
    val reviewCount: Int,
    val avatarDrawableName: String,
    val experienceYears: Int = 8
)

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val referenceCode: String,
    val serviceId: String,
    val providerId: String,
    val startTimestamp: Long,
    val endTimestamp: Long,
    val priceCents: Long,
    val currency: String = "USD",
    val status: String, // "Confirmed", "Completed", "Cancelled"
    val customerNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val serviceId: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String = ""
)
