package com.example.ui.screens

import android.os.Build
import android.os.BatteryManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BatteryMascot

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToEvents: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    var batteryLevel by remember { mutableIntStateOf(0) }
    var isCharging by remember { mutableStateOf(false) }
    var batteryTemp by remember { mutableStateOf(0f) }
    var batteryVoltage by remember { mutableIntStateOf(0) }
    var batteryHealth by remember { mutableStateOf("Unknown") }
    var batteryTech by remember { mutableStateOf("Li-ion") }
    var chargingTimeRemaining by remember { mutableStateOf<Long?>(null) } // In ms

    // Permission handling
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.READ_MEDIA_AUDIO)
    } else {
        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    LaunchedEffect(Unit) {
        launcher.launch(permissionsToRequest)
        
        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, intentFilter)
        
        batteryLevel = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 0
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        
        batteryTemp = (batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0) / 10f
        batteryVoltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        
        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        batteryHealth = when (health) {
            BatteryManager.BATTERY_HEALTH_GOOD -> "GOOD"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "OVERHEAT"
            BatteryManager.BATTERY_HEALTH_DEAD -> "DEAD"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "OVER VOLTAGE"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "FAILURE"
            BatteryManager.BATTERY_HEALTH_COLD -> "COLD"
            else -> "UNKNOWN"
        }
        
        batteryTech = batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Li-ion"
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            chargingTimeRemaining = batteryManager.computeChargeTimeRemaining()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                },
                title = { 
                    Column {
                        Text("Pookiee", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                        Text("CHARGING UTILITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                },
                actions = {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(24.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(androidx.compose.foundation.shape.CircleShape).background(MaterialTheme.colorScheme.onSecondaryContainer))
                            Text("ACTIVE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Main Battery Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), MaterialTheme.colorScheme.secondaryContainer)
                            )
                        )
                ) {
                    // Battery Mascot or wave effect
                    Box(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(60.dp).alpha(0.3f)) {
                        BatteryMascot(level = batteryLevel, isCharging = isCharging, modifier = Modifier.fillMaxSize())
                    }

                    Row(
                        modifier = Modifier.padding(24.dp).fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text("Current Charge", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("$batteryLevel", fontSize = 64.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text("%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.padding(bottom = 12.dp))
                            }
                        }
                        
                        Surface(
                            color = Color.White.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                if (isCharging) Icons.Default.Bolt else Icons.Default.BatteryStd,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(12.dp).size(32.dp)
                            )
                        }
                    }
                    
                    val timeText = if (isCharging && chargingTimeRemaining != null && chargingTimeRemaining!! > 0) {
                        val hours = (chargingTimeRemaining!! / (1000 * 60 * 60)).toInt()
                        val minutes = ((chargingTimeRemaining!! / (1000 * 60)) % 60).toInt()
                        "FULL IN: ${hours}H ${minutes}M"
                    } else if (isCharging) {
                        "CALCULATING..."
                    } else {
                        "UNPLUGGED"
                    }

                    Text(
                        timeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
                    )
                }
            }

            // Bento Grid Row 1: Health & Voltage
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BentoMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "HEALTH",
                        value = batteryHealth,
                        icon = Icons.Default.Favorite,
                        iconBg = Color(0xFFFFD8E4)
                    )
                    BentoMiniCard(
                        modifier = Modifier.weight(1f),
                        title = "VOLTAGE",
                        value = "${batteryVoltage / 1000f}V",
                        icon = Icons.Default.FlashOn,
                        iconBg = Color(0xFFE8DEF8)
                    )
                }
            }

            // Bento Grid Row 2: Tech & Temp
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    BentoMiniCard(
                        modifier = Modifier.weight(0.8f),
                        title = "TYPE",
                        value = batteryTech,
                        icon = Icons.Default.Memory,
                        iconBg = Color(0xFFC2E7FF)
                    )
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(28.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.Center) {
                            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("$batteryTemp°C", fontSize = 24.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                val status = if (batteryTemp > 40) "HOT" else if (batteryTemp < 15) "COLD" else "CHILL"
                                val statusColor = if (batteryTemp > 40) Color.Red else if (batteryTemp < 15) Color.Blue else Color(0xFF4CAF50)
                                Text(status, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = statusColor, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text("CORE TEMPERATURE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        }
                    }
                }
            }

            // Roast Mode Section
            item {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("AI ROAST ENGINE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Switch(checked = true, onCheckedChange = {}, modifier = Modifier.scale(0.7f))
                        }
                        Text("\"Bro seriously? Charging at $batteryLevel%? Go touch grass, your phone isn't your life.\"", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Big Full Width Action: Meme Customizer
            item {
                Surface(
                    onClick = onNavigateToEvents,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(28.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Surface(color = Color(0xFFD0BCFF), shape = androidx.compose.foundation.shape.CircleShape, modifier = Modifier.size(44.dp)) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF21005D), modifier = Modifier.padding(8.dp))
                            }
                            Column {
                                Text("Add Custom Effect", fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                Text("WAV, MP3, or TTS Voice", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DashboardActionCard(
                    title = "Persistence Engine Logs",
                    description = "Historical battery performance analytics",
                    icon = Icons.Default.History,
                    onClick = onNavigateToHistory
                )
            }
        }
    }
}

@Composable
fun BentoMiniCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    onClick: (() -> Unit)? = null
) {
    Surface(
        onClick = { onClick?.invoke() },
        modifier = modifier,
        enabled = onClick != null,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(
                color = iconBg,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.padding(10.dp), tint = Color.Black.copy(alpha = 0.7f))
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                Text(description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun MiniStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(24.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
        }
    }
}
