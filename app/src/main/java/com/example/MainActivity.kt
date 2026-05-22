package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.WorldCupDatabase
import com.example.data.repository.WorldCupRepository
import com.example.ui.screens.MainScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WorldCupViewModel
import com.example.ui.viewmodel.WorldCupViewModelFactory
import com.example.util.NotificationHelper

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup Android system notification channel
        NotificationHelper.createNotificationChannel(this)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Request dynamic runtime permissions on Android 13+
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    // Permissions granted handler
                }

                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                // Initialize Room Database, DAO and Repository
                val database = WorldCupDatabase.getDatabase(applicationContext)
                val repository = WorldCupRepository(database.worldCupDao())

                // Instantiate ViewModel with Custom Factory
                val worldCupViewModel: WorldCupViewModel = viewModel(
                    factory = WorldCupViewModelFactory(repository, applicationContext)
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    MainScreen(viewModel = worldCupViewModel)
                }
            }
        }
    }
}
