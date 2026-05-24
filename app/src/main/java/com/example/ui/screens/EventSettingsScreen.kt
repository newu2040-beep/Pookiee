package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
            selectedAction?.let { action ->
                viewModel.updateAction(action.copy(soundUri = it.toString()))
                selectedAction = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Meme Trigger Engine", fontWeight = FontWeight.Black)
                        Text("EVENT-BASED DIAGNOSTICS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp, start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    "CORE TRIGGERS", 
                    style = MaterialTheme.typography.labelSmall, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
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
        var tempAction by remember(selectedAction) { mutableStateOf(selectedAction!!) }
        
        AlertDialog(
            onDismissRequest = { selectedAction = null },
            title = { Text("Meme Settings", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = tempAction.customLabel ?: "",
                        onValueChange = { tempAction = tempAction.copy(customLabel = it) },
                        label = { Text("Label (Rename Meme)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { audioPickerLauncher.launch("*/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Pick Audio/Video File")
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = (tempAction.startMs / 1000).toString(),
                            onValueChange = { tempAction = tempAction.copy(startMs = (it.toLongOrNull() ?: 0L) * 1000) },
                            label = { Text("Start (s)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = (tempAction.durationMs?.let { it / 1000 } ?: "").toString(),
                            onValueChange = { tempAction = tempAction.copy(durationMs = it.toLongOrNull()?.let { s -> s * 1000 }) },
                            label = { Text("Duration (s)") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Full") }
                        )
                    }

                    Column {
                        Text("Volume: ${(tempAction.volume * 100).toInt()}%", style = MaterialTheme.typography.labelMedium)
                        Slider(value = tempAction.volume, onValueChange = { tempAction = tempAction.copy(volume = it) })
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.updateAction(tempAction); selectedAction = null }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAction = null }) {
                    Text("Cancel")
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
                    text = action.customLabel ?: action.eventType.name.replace("_", " "),
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
