package com.example.data.local

import com.example.data.model.AppointmentEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProviderEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserProfileEntity
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object InitialDemoData {

    val providers = listOf(
        ProviderEntity(
            id = "provider_olivia",
            name = "Olivia Bennett",
            specialty = "Master Hair Stylist & Brow Artisan",
            bio = "With over 8 years in bespoke hair architecture and facial aesthetics, Olivia focuses on natural silhouettes and effortless daily elegance.",
            rating = 4.9f,
            reviewCount = 142,
            avatarDrawableName = "lumi_provider_olivia",
            experienceYears = 8
        ),
        ProviderEntity(
            id = "provider_marcus",
            name = "Marcus Thorne",
            specialty = "Holistic Bodywork & Wellness Specialist",
            bio = "Certified therapist specializing in restorative deep-tissue release, hot stone relaxation, and tension-relief rituals.",
            rating = 4.9f,
            reviewCount = 230,
            avatarDrawableName = "lumi_provider_marcus",
            experienceYears = 10
        ),
        ProviderEntity(
            id = "provider_elena",
            name = "Elena Rostova",
            specialty = "Licensed Aesthetician & Skin Specialist",
            bio = "Pioneering botanical skin treatments and lymphatic drainage protocols designed to restore natural barrier radiance and calm.",
            rating = 4.8f,
            reviewCount = 118,
            avatarDrawableName = "lumi_provider_elena",
            experienceYears = 7
        ),
        ProviderEntity(
            id = "provider_chloe",
            name = "Chloe Vance",
            specialty = "Certified Nail Artist & Hand Care Specialist",
            bio = "Specializing in gentle cuticle restoration, clean Japanese gel techniques, and nourishing botanical hand rituals.",
            rating = 4.8f,
            reviewCount = 95,
            avatarDrawableName = "lumi_provider_chloe",
            experienceYears = 6
        )
    )

    val services = listOf(
        ServiceEntity(
            id = "srv_haircut",
            name = "Signature Haircut",
            description = "A personalized haircut experience designed around your style, preferences, and daily routine. Includes botanical wash, scalp massage, precision sculpting, and customized styling.",
            category = "Hair",
            providerId = "provider_olivia",
            durationMinutes = 45,
            priceCents = 4500L,
            rating = 4.9f,
            reviewCount = 128,
            imageDrawableName = "lumi_service_hair",
            isFeatured = true,
            whatIncluded = "Tailored consultation|Herbal scalp massage & wash|Precision cut & texture styling|Custom home aftercare guide",
            location = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
            cancellationPolicy = "Free cancellation up to 24 hours before your scheduled appointment."
        ),
        ServiceEntity(
            id = "srv_manicure",
            name = "Classic Manicure",
            description = "Gentle nail shaping, warm cuticle treatment, botanical essential oil hand massage, and long-lasting non-toxic polish application.",
            category = "Nails",
            providerId = "provider_chloe",
            durationMinutes = 40,
            priceCents = 3000L,
            rating = 4.8f,
            reviewCount = 94,
            imageDrawableName = "lumi_service_nails",
            isFeatured = true,
            whatIncluded = "Warm soak & cuticle therapy|Nail shaping & buffing|Organic jojoba hand massage|Breathable non-toxic polish",
            location = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
            cancellationPolicy = "Free cancellation up to 24 hours before your scheduled appointment."
        ),
        ServiceEntity(
            id = "srv_massage",
            name = "Relaxing Massage",
            description = "A deeply restorative full-body massage using warm organic oils, gentle heat therapy, and mindful rhythmic techniques to melt chronic stress.",
            category = "Massage",
            providerId = "provider_marcus",
            durationMinutes = 60,
            priceCents = 7500L,
            rating = 4.9f,
            reviewCount = 215,
            imageDrawableName = "lumi_service_spa",
            isFeatured = true,
            whatIncluded = "Botanical foot soak|Aromatherapy oil selection|60-min restorative bodywork|Warm herbal tea service",
            location = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
            cancellationPolicy = "Free cancellation up to 24 hours before your scheduled appointment."
        ),
        ServiceEntity(
            id = "srv_glow_facial",
            name = "Glow Facial",
            description = "Intensive hydration, gentle botanical exfoliation, and a restorative vitamin-infused facial mask to restore cellular radiance and calm sensitivity.",
            category = "Skin",
            providerId = "provider_elena",
            durationMinutes = 50,
            priceCents = 6000L,
            rating = 4.7f,
            reviewCount = 88,
            imageDrawableName = "lumi_service_skin",
            isFeatured = true,
            whatIncluded = "Double botanical cleanse|Gentle AHA enzymatic peel|Sculpting lymphatic facial massage|Glow peptide serum infusion",
            location = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
            cancellationPolicy = "Free cancellation up to 24 hours before your scheduled appointment."
        ),
        ServiceEntity(
            id = "srv_brows",
            name = "Brow Sculpt & Lamination",
            description = "Precision brow shaping, nourishing keratin lamination, and subtle custom tinting for naturally full, feather-soft arches.",
            category = "Brows",
            providerId = "provider_olivia",
            durationMinutes = 35,
            priceCents = 3800L,
            rating = 4.8f,
            reviewCount = 72,
            imageDrawableName = "lumi_service_hair",
            isFeatured = false,
            whatIncluded = "Golden ratio brow mapping|Gentle vegan wax & tweeze|Keratin lamination treatment|Custom hybrid tint",
            location = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
            cancellationPolicy = "Free cancellation up to 24 hours before your scheduled appointment."
        ),
        ServiceEntity(
            id = "srv_body_polish",
            name = "Botanical Body Polish",
            description = "Gentle bamboo salt buffing followed by a rich nourishing shea butter and rosehip veil to leave skin soft, radiant, and hydrated.",
            category = "Wellness",
            providerId = "provider_marcus",
            durationMinutes = 55,
            priceCents = 7000L,
            rating = 4.9f,
            reviewCount = 64,
            imageDrawableName = "lumi_service_spa",
            isFeatured = false,
            whatIncluded = "Dry body brush prep|Fine bamboo & mineral salt scrub|Warm rain mist rinse|Whipped shea butter veil",
            location = "LUMI Sanctuary, 452 Beverly Blvd, Suite 200",
            cancellationPolicy = "Free cancellation up to 24 hours before your scheduled appointment."
        )
    )

    fun createInitialAppointment(): AppointmentEntity {
        val zone = ZoneId.systemDefault()
        val tomorrow = LocalDate.now(zone).plusDays(1)
        val startTime = LocalDateTime.of(tomorrow, LocalTime.of(10, 0))
        val endTime = startTime.plusMinutes(45)

        return AppointmentEntity(
            id = 1,
            referenceCode = "LUMI-7489",
            serviceId = "srv_haircut",
            providerId = "provider_olivia",
            startTimestamp = startTime.atZone(zone).toInstant().toEpochMilli(),
            endTimestamp = endTime.atZone(zone).toInstant().toEpochMilli(),
            priceCents = 4500L,
            currency = "USD",
            status = "Confirmed",
            customerNote = "Looking forward to subtle soft layering.",
            createdAt = System.currentTimeMillis() - 86400000L
        )
    }

    val initialNotifications = listOf(
        NotificationEntity(
            id = 1,
            title = "Welcome to LUMI",
            body = "Discover curated wellness rituals and reserve your time for self-care.",
            createdAt = System.currentTimeMillis() - 3600000L * 24,
            isRead = false
        ),
        NotificationEntity(
            id = 2,
            title = "Upcoming ritual reminder",
            body = "Your Signature Haircut with Olivia Bennett is scheduled for tomorrow at 10:00 AM.",
            createdAt = System.currentTimeMillis() - 3600000L * 2,
            isRead = false
        )
    )

    val initialProfile = UserProfileEntity(
        id = 1,
        firstName = "Alex",
        lastName = "Morgan",
        email = "alex@example.com",
        phoneNumber = "+1 (555) 234-8901"
    )
}
