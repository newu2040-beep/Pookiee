package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.service.ChargingService
import com.example.ui.theme.Theme
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EventSettingsScreen
import com.example.ui.screens.HistoryScreen
import kotlinx.serialization.Serializable

@Serializable object DashboardRoute
@Serializable object EventSettingsRoute
@Serializable object HistoryRoute

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Start the monitoring service
    val serviceIntent = Intent(this, ChargingService::class.java)
    startService(serviceIntent)
    
    enableEdgeToEdge()
    setContent {
      Theme {
        PookieeApp()
      }
    }
  }
}

@Composable
fun PookieeApp() {
  val navController = rememberNavController()
  
  Scaffold(
    modifier = Modifier.fillMaxSize(),
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = DashboardRoute,
      modifier = Modifier.padding(innerPadding)
    ) {
      composable<DashboardRoute> { 
        DashboardScreen(
          onNavigateToEvents = { navController.navigate(EventSettingsRoute) },
          onNavigateToHistory = { navController.navigate(HistoryRoute) }
        ) 
      }
      composable<EventSettingsRoute> { 
        EventSettingsScreen(onBack = { navController.popBackStack() }) 
      }
      composable<HistoryRoute> { 
        HistoryScreen(onBack = { navController.popBackStack() }) 
      }
    }
  }
}
