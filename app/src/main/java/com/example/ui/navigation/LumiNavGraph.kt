package com.example.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.core.common.DataStoreManager
import com.example.data.repository.LumiRepository
import com.example.ui.screens.about.AboutLumiScreen
import com.example.ui.screens.confirmation.BookingConfirmationScreen
import com.example.ui.screens.favorites.FavoritesScreen
import com.example.ui.screens.main.MainScaffold
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.EditProfileScreen
import com.example.ui.screens.providerselection.ProviderSelectionScreen
import com.example.ui.screens.scheduling.SchedulingScreen
import com.example.ui.screens.servicedetails.ServiceDetailScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.screens.summary.BookingSummaryScreen

@Composable
fun LumiNavGraph(
    repository: LumiRepository,
    dataStoreManager: DataStoreManager,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(280)) },
        exitTransition = { fadeOut(animationSpec = tween(280)) },
        popEnterTransition = { fadeIn(animationSpec = tween(280)) },
        popExitTransition = { fadeOut(animationSpec = tween(280)) }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                repository = repository,
                dataStoreManager = dataStoreManager,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Main.createRoute("home")) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                dataStoreManager = dataStoreManager,
                onFinishOnboarding = {
                    navController.navigate(Screen.Main.createRoute("home")) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Main.route,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.StringType
                    defaultValue = "home"
                }
            )
        ) { backStackEntry ->
            val tab = backStackEntry.arguments?.getString("tab") ?: "home"
            MainScaffold(
                initialTab = tab,
                repository = repository,
                dataStoreManager = dataStoreManager,
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToEditProfile = {
                    navController.navigate(Screen.EditProfile.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToNotifications = {
                    navController.navigate(Screen.Notifications.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.AboutLumi.route)
                }
            )
        }

        composable(
            route = Screen.ServiceDetail.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            ServiceDetailScreen(
                serviceId = serviceId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onBookAppointment = { sId, pId ->
                    // Navigate to Provider Selection to allow choosing a specialist
                    navController.navigate(Screen.ProviderSelection.createRoute(sId))
                }
            )
        }

        composable(
            route = Screen.ProviderSelection.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            ProviderSelectionScreen(
                serviceId = serviceId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onProviderSelected = { sId, pId ->
                    navController.navigate(Screen.Scheduling.createRoute(sId, pId))
                }
            )
        }

        composable(
            route = Screen.Scheduling.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
            SchedulingScreen(
                serviceId = serviceId,
                providerId = providerId,
                repository = repository,
                onBack = { navController.popBackStack() },
                onProceedToSummary = { sId, pId, timestamp ->
                    navController.navigate(Screen.BookingSummary.createRoute(sId, pId, timestamp))
                }
            )
        }

        composable(
            route = Screen.BookingSummary.route,
            arguments = listOf(
                navArgument("serviceId") { type = NavType.StringType },
                navArgument("providerId") { type = NavType.StringType },
                navArgument("timestamp") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
            val providerId = backStackEntry.arguments?.getString("providerId") ?: ""
            val timestamp = backStackEntry.arguments?.getLong("timestamp") ?: 0L
            BookingSummaryScreen(
                serviceId = serviceId,
                providerId = providerId,
                timestamp = timestamp,
                repository = repository,
                onBack = { navController.popBackStack() },
                onBookingConfirmed = { referenceCode ->
                    navController.navigate(Screen.BookingConfirmation.createRoute(referenceCode)) {
                        popUpTo(Screen.Main.route) { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = Screen.BookingConfirmation.route,
            arguments = listOf(
                navArgument("referenceCode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val referenceCode = backStackEntry.arguments?.getString("referenceCode") ?: ""
            BookingConfirmationScreen(
                referenceCode = referenceCode,
                repository = repository,
                onViewBookings = {
                    navController.navigate(Screen.Main.createRoute("bookings")) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                onBackToHome = {
                    navController.navigate(Screen.Main.createRoute("home")) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                repository = repository,
                onBack = { navController.popBackStack() },
                onNavigateToServiceDetail = { serviceId ->
                    navController.navigate(Screen.ServiceDetail.createRoute(serviceId))
                },
                onNavigateToExplore = {
                    navController.navigate(Screen.Main.createRoute("explore")) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationsScreen(
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AboutLumi.route) {
            AboutLumiScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
