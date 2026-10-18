package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.core.common.DataStoreManager
import com.example.data.local.LumiDatabase
import com.example.data.repository.LumiRepositoryImpl
import com.example.ui.navigation.LumiNavGraph
import com.example.ui.theme.LumiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = LumiDatabase.getInstance(applicationContext)
        val repository = LumiRepositoryImpl(database)
        val dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            val isDarkThemePref by dataStoreManager.isDarkMode.collectAsState(initial = null)
            val isDark = isDarkThemePref ?: isSystemInDarkTheme()

            LumiTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LumiNavGraph(
                        repository = repository,
                        dataStoreManager = dataStoreManager
                    )
                }
            }
        }
    }
}
