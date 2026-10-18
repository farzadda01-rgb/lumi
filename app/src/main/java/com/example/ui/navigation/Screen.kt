package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Main : Screen("main?tab={tab}") {
        fun createRoute(tab: String = "home") = "main?tab=$tab"
    }

    object ServiceDetail : Screen("service_detail/{serviceId}") {
        fun createRoute(serviceId: String) = "service_detail/$serviceId"
    }

    object ProviderSelection : Screen("provider_selection/{serviceId}") {
        fun createRoute(serviceId: String) = "provider_selection/$serviceId"
    }

    object Scheduling : Screen("scheduling/{serviceId}/{providerId}") {
        fun createRoute(serviceId: String, providerId: String) = "scheduling/$serviceId/$providerId"
    }

    object BookingSummary : Screen("booking_summary/{serviceId}/{providerId}/{timestamp}") {
        fun createRoute(serviceId: String, providerId: String, timestamp: Long) =
            "booking_summary/$serviceId/$providerId/$timestamp"
    }

    object BookingConfirmation : Screen("booking_confirmation/{referenceCode}") {
        fun createRoute(referenceCode: String) = "booking_confirmation/$referenceCode"
    }

    object EditProfile : Screen("edit_profile")
    object Favorites : Screen("favorites")
    object Notifications : Screen("notifications")
    object AboutLumi : Screen("about_lumi")
}

enum class BottomBarDestination(val route: String, val title: String) {
    HOME("home", "Home"),
    EXPLORE("explore", "Explore"),
    BOOKINGS("bookings", "Bookings"),
    PROFILE("profile", "Profile")
}
