package com.example.randomtext

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createChannel(this)
        requestNotificationPermissionIfNeeded()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val context = LocalContext.current
    val prefs = remember { PrefsManager(context) }
    val quoteRepo = remember { QuoteRepository(context) }

    var enabled by remember { mutableStateOf(prefs.isEnabled) }
    var hour by remember { mutableIntStateOf(prefs.hour) }
    var minute by remember { mutableIntStateOf(prefs.minute) }
    var previewQuote by remember { mutableStateOf(quoteRepo.getRandomQuote()) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(initialHour = hour, initialMinute = minute)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Random Text Notifier",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Quote preview card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Today's Preview",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "\"$previewQuote\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        TextButton(onClick = { previewQuote = quoteRepo.getRandomQuote() }) {
            Text("Shuffle Quote")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Enable / Disable toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Daily Notification", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = enabled,
                onCheckedChange = { newValue ->
                    enabled = newValue
                    prefs.isEnabled = newValue
                    if (newValue) {
                        SchedulerUtil.schedule(context)
                    } else {
                        SchedulerUtil.cancel(context)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time selector
        OutlinedButton(
            onClick = { showTimePicker = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled
        ) {
            Text("Notification Time: %02d:%02d".format(hour, minute))
        }

        // Time picker dialog
        if (showTimePicker) {
            AlertDialog(
                onDismissRequest = { showTimePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        hour = timePickerState.hour
                        minute = timePickerState.minute
                        prefs.hour = hour
                        prefs.minute = minute
                        showTimePicker = false
                        if (enabled) {
                            SchedulerUtil.schedule(context)
                        }
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                },
                title = { Text("Select Time") },
                text = { TimePicker(state = timePickerState) }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Quotes are loaded from the bundled text file.\nNotifications are powered by WorkManager.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
