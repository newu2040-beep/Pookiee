package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.PookieeApplication
import com.example.data.model.ChargingAction
import com.example.data.model.ChargingEventType
import com.example.ui.viewmodel.PookieeViewModel
import com.example.ui.viewmodel.PookieeViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSettingsScreen(
    onBack: () -> Unit,
    viewModel: PookieeViewModel = viewModel(factory = PookieeViewModelFactory((LocalContext.current.applicationContext as PookieeApplication).repository))
) {
    val context = LocalContext.current
    val actions by viewModel.allActions.collectAsState(initial = emptyList())
    var selectedAction by remember { mutableStateOf<ChargingAction?>(null) }

    val audioPickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            // Persist permission for internal storage URIs if needed, but GetContent is usually fine for one-off
            selectedAction?.let { action ->
                viewModel.updateAction(action.copy(soundUri = it.toString()))
                selectedAction = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meme Alerts") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val allTypes = ChargingEventType.entries
            items(allTypes) { type ->
                val action = actions.find { it.eventType == type } ?: ChargingAction(type)
                EventItem(
                    action = action,
                    onClick = { selectedAction = action },
                    onToggle = { enabled ->
                        viewModel.updateAction(action.copy(isEnabled = enabled))
                    }
                )
            }
        }
    }

    if (selectedAction != null) {
        // Simple dialog to mock sound selection for now
        AlertDialog(
            onDismissRequest = { selectedAction = null },
            title = { Text("Configure ${selectedAction?.eventType?.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a meme sound or effect from storage.")
                    OutlinedButton(onClick = { audioPickerLauncher.launch("audio/*") }) {
                        Icon(Icons.Default.Upload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedAction?.soundUri != null) "Change Audio" else "Pick Audio File")
                    }
                    if (selectedAction?.soundUri != null) {
                        Text("Current: ${selectedAction?.soundUri}", style = MaterialTheme.typography.labelSmall)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedAction = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
fun EventItem(
    action: ChargingAction,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = if (action.isEnabled) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                shape = androidx.compose.foundation.shape.CircleShape,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    if (action.isEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp),
                    tint = if (action.isEnabled) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.eventType.name.replace("_", " "),
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = if (action.soundUri != null) "Meme Active" else "No meme set",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Switch(
                checked = action.isEnabled,
                onCheckedChange = onToggle,
                modifier = Modifier.scale(0.8f)
            )
        }
    }
}
