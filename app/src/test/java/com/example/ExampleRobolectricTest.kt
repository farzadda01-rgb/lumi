package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LumiDatabase
import com.example.data.repository.LumiRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: LumiDatabase
    private lateinit var repository: LumiRepositoryImpl

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LumiDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = LumiRepositoryImpl(database)
        runBlocking {
            LumiDatabase.populateInitialData(database)
        }
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read app_name from context matches LUMI`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("LUMI", appName)
    }

    @Test
    fun `repository correctly provides initial seeded services and providers`() = runBlocking {
        val services = repository.allServices.first()
        assertTrue("Services should not be empty", services.isNotEmpty())
        assertEquals(6, services.size)

        val providers = repository.allProviders.first()
        assertEquals(4, providers.size)
    }

    @Test
    fun `favorites persistence and toggle works reactively`() = runBlocking {
        val testServiceId = "srv_glow_facial"
        val initialFavs = repository.favoriteIds.first()
        assertFalse(initialFavs.contains(testServiceId))

        // Toggle on
        repository.toggleFavorite(testServiceId)
        val updatedFavs = repository.favoriteIds.first()
        assertTrue(updatedFavs.contains(testServiceId))

        // Toggle off
        repository.toggleFavorite(testServiceId)
        val revertedFavs = repository.favoriteIds.first()
        assertFalse(revertedFavs.contains(testServiceId))
    }

    @Test
    fun `booking validation rejects past time slot`() = runBlocking {
        val pastTimestamp = System.currentTimeMillis() - 86400000L // 1 day in the past
        val result = repository.bookAppointment(
            serviceId = "srv_haircut",
            providerId = "provider_olivia",
            startTimestamp = pastTimestamp,
            customerNote = "Test past note"
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `booking validation successfully reserves available future slot and prevents duplicate`() = runBlocking {
        val futureTimestamp = System.currentTimeMillis() + 86400000L * 2 // 2 days in future
        val result = repository.bookAppointment(
            serviceId = "srv_haircut",
            providerId = "provider_olivia",
            startTimestamp = futureTimestamp,
            customerNote = "First booking"
        )
        assertTrue(result.isSuccess)
        val appointment = result.getOrNull()
        assertNotNull(appointment)
        assertTrue(appointment!!.referenceCode.startsWith("LUMI-"))
        assertEquals("Confirmed", appointment.status)

        // Attempting to book the SAME provider at the EXACT SAME slot must fail
        val duplicateResult = repository.bookAppointment(
            serviceId = "srv_brows",
            providerId = "provider_olivia",
            startTimestamp = futureTimestamp,
            customerNote = "Duplicate attempt"
        )
        assertTrue("Duplicate slot must be rejected", duplicateResult.isFailure)
    }

    @Test
    fun `cancellation updates appointment status to Cancelled`() = runBlocking {
        val upcoming = repository.upcomingAppointments.first()
        val firstAppointment = upcoming.first()
        assertEquals("Confirmed", firstAppointment.status)

        val cancelResult = repository.cancelAppointment(firstAppointment.id)
        assertTrue(cancelResult.isSuccess)

        val past = repository.pastAppointments.first()
        val cancelled = past.first { it.id == firstAppointment.id }
        assertEquals("Cancelled", cancelled.status)
    }
}
