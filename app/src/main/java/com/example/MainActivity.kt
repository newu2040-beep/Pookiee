package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.service.ChargingService
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.EventSettingsScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.ThemePickerScreen
import com.example.ui.theme.Theme
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable object DashboardRoute
@Serializable object EventSettingsRoute
@Serializable object HistoryRoute
@Serializable object ThemePickerRoute

enum class PookieeTheme {
    LAVENDER, SAKURA, MINT, PEACH, OCEAN, SUNSET, ARCTIC
}

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Start the monitoring service
    val serviceIntent = Intent(this, ChargingService::class.java)
    startService(serviceIntent)
    
    enableEdgeToEdge()
    setContent {
      var currentTheme by remember { mutableStateOf(PookieeTheme.LAVENDER) }
      var isDarkMode by remember { mutableStateOf(false) }

      Theme(pookieeTheme = currentTheme, darkTheme = isDarkMode) {
        PookieeApp(
            onThemeChange = { currentTheme = it }, 
            currentTheme = currentTheme,
            isDarkMode = isDarkMode,
            onToggleDarkMode = { isDarkMode = it }
        )
      }
    }
  }
}

@Composable
fun PookieeApp(
    onThemeChange: (PookieeTheme) -> Unit, 
    currentTheme: PookieeTheme,
    isDarkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
  val navController = rememberNavController()
  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val scope = rememberCoroutineScope()
  
  ModalNavigationDrawer(
    drawerState = drawerState,
    drawerContent = {
      ModalDrawerSheet(
        modifier = Modifier.width(300.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerTonalElevation = 2.dp
      ) {
        Spacer(Modifier.height(48.dp))
        Text(
            "POOKIEE", 
            modifier = Modifier.padding(horizontal = 28.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            "Meme Utility", 
            modifier = Modifier.padding(horizontal = 28.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(32.dp))
        
        NavigationDrawerItem(
          label = { Text("Bento Diagnostics", fontWeight = FontWeight.Bold) },
          selected = false,
          onClick = { 
            navController.navigate(DashboardRoute) { popUpTo(DashboardRoute) { inclusive = true } }
            scope.launch { drawerState.close() }
          },
          icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
          modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
          label = { Text("Meme Trigger Engine", fontWeight = FontWeight.Bold) },
          selected = false,
          onClick = { 
            navController.navigate(EventSettingsRoute)
            scope.launch { drawerState.close() }
          },
          icon = { Icon(Icons.Default.Bolt, contentDescription = null) },
          modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
          label = { Text("Power Persistence Logs", fontWeight = FontWeight.Bold) },
          selected = false,
          onClick = { 
            navController.navigate(HistoryRoute)
            scope.launch { drawerState.close() }
          },
          icon = { Icon(Icons.Default.History, contentDescription = null) },
          modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
          label = { Text("Visual Aesthetics", fontWeight = FontWeight.Bold) },
          selected = false,
          onClick = { 
            navController.navigate(ThemePickerRoute)
            scope.launch { drawerState.close() }
          },
          icon = { Icon(Icons.Default.Palette, contentDescription = null) },
          modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
      }
    }
  ) {
    Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = {
          // Optional: Could add a BottomAppBar here too if desired, 
          // but drawer + bento grid is cleaner as requested.
      }
    ) { innerPadding ->
      NavHost(
        navController = navController,
        startDestination = DashboardRoute,
        modifier = Modifier.padding(innerPadding)
      ) {
        composable<DashboardRoute> { 
          DashboardScreen(
            onNavigateToEvents = { navController.navigate(EventSettingsRoute) },
            onNavigateToHistory = { navController.navigate(HistoryRoute) },
            onOpenDrawer = { scope.launch { drawerState.open() } }
          ) 
        }
        composable<EventSettingsRoute> { 
          EventSettingsScreen(onBack = { navController.popBackStack() }) 
        }
        composable<HistoryRoute> { 
          HistoryScreen(onBack = { navController.popBackStack() }) 
        }
        composable<ThemePickerRoute> {
            ThemePickerScreen(
                currentTheme = currentTheme,
                isDarkMode = isDarkMode,
                onThemeSelected = onThemeChange,
                onToggleDarkMode = onToggleDarkMode,
                onBack = { navController.popBackStack() }
            )
        }
      }
    }
  }
}
