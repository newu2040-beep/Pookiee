package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.PookieeTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePickerScreen(
    currentTheme: PookieeTheme,
    isDarkMode: Boolean,
    isAmoled: Boolean,
    onThemeSelected: (PookieeTheme) -> Unit,
    onToggleDarkMode: (Boolean) -> Unit,
    onToggleAmoled: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Aesthetics", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background)) {
            // Theme Mode Controls
            Surface(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Icon(if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Text("Dark Mode", fontWeight = FontWeight.Bold)
                        }
                        Switch(checked = isDarkMode, onCheckedChange = onToggleDarkMode)
                    }
                    
                    if (isDarkMode) {
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Icon(Icons.Default.NightsStay, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Column {
                                    Text("AMOLED Mode", fontWeight = FontWeight.Bold)
                                    Text("Pure black background", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            Switch(checked = isAmoled, onCheckedChange = onToggleAmoled)
                        }
                    }
                }
            }

            Text(
                "SELECT PALETTE", 
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(com.example.PookieeTheme.entries) { theme ->
                    ThemeCard(
                        theme = theme,
                        isSelected = theme == currentTheme,
                        onClick = { onThemeSelected(theme) }
                    )
                }
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: PookieeTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val color = when (theme) {
        PookieeTheme.LAVENDER -> Color(0xFFD0BCFF)
        PookieeTheme.SAKURA -> Color(0xFFF06292)
        PookieeTheme.MINT -> Color(0xFF4DB6AC)
        PookieeTheme.PEACH -> Color(0xFFFF8A65)
        PookieeTheme.OCEAN -> Color(0xFF42A5F5)
        PookieeTheme.SUNSET -> Color(0xFFFF7043)
        PookieeTheme.ARCTIC -> Color(0xFF26C6DA)
        PookieeTheme.ROSE -> Color(0xFFF48FB1)
        PookieeTheme.SKY -> Color(0xFF81D4FA)
        PookieeTheme.SLATE -> Color(0xFF90A4AE)
        PookieeTheme.CHARCOAL -> Color(0xFF546E7A)
        PookieeTheme.LIME -> Color(0xFFCDDC39)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 3.dp else 1.dp,
            color = if (isSelected) color else MaterialTheme.colorScheme.outline
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(color)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = theme.name.lowercase().replaceFirstChar { it.uppercase() },
                fontWeight = FontWeight.Bold
            )
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = color)
            }
        }
    }
}
