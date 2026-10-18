package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AppointmentEntity
import com.example.data.model.FavoriteEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.ProviderEntity
import com.example.data.model.ServiceEntity
import com.example.data.model.UserProfileEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ServiceEntity::class,
        ProviderEntity::class,
        AppointmentEntity::class,
        FavoriteEntity::class,
        NotificationEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LumiDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun providerDao(): ProviderDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: LumiDatabase? = null

        fun getInstance(context: Context): LumiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LumiDatabase::class.java,
                    "lumi_database.db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                populateInitialData(getInstance(context))
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(database: LumiDatabase) {
            database.serviceDao().insertAll(InitialDemoData.services)
            database.providerDao().insertAll(InitialDemoData.providers)
            database.appointmentDao().insertAppointment(InitialDemoData.createInitialAppointment())
            database.notificationDao().insertAll(InitialDemoData.initialNotifications)
            database.userProfileDao().saveUserProfile(InitialDemoData.initialProfile)
            database.favoriteDao().addFavorite(FavoriteEntity("srv_haircut"))
            database.favoriteDao().addFavorite(FavoriteEntity("srv_massage"))
        }
    }
}
