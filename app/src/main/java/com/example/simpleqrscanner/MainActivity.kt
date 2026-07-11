package com.example.simpleqrscanner

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.simpleqrscanner.ui.components.ScannerOverlay
import com.example.simpleqrscanner.ui.screens.CameraXScannerPreview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val context = LocalContext.current
                    var scannedText by remember { mutableStateOf("Point camera at a QR code") }

                    // 1. Local state to track if permission is granted
                    var hasCameraPermission by remember {
                        mutableStateOf(checkCameraPermission(context))
                    }

                    // 2. The Native Permission Launcher
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            hasCameraPermission = isGranted
                        }
                    )

                    Column(modifier = Modifier.fillMaxSize()) {
                        if (hasCameraPermission) {
                            // Camera Screen Area (Granted State)
                            Box(modifier = Modifier.weight(0.85f)) {
                                CameraXScannerPreview(onQrCodeScanned = { result ->
                                    scannedText = result
                                })
                                ScannerOverlay()
                            }

                            Card(
                                modifier = Modifier.weight(0.15f).fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Scanned Text (Takes up most of the space)
                                    Text(
                                        text = scannedText,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Start
                                    )

                                    // 2. Only show the copy button if a valid QR code has actually been scanned
                                    if (scannedText != "Point camera at a QR code") {
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText("Scanned QR Code", scannedText)
                                                clipboard.setPrimaryClip(clip)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ContentCopy,
                                                contentDescription = "Copy to clipboard",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        } else {
                            // Request Permission UI (Denied State)
                            Column(
                                modifier = Modifier.fillMaxSize().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "We need camera access to scan QR codes using ZXing.",
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = {
                                    // 3. Launch the native request system dialog
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }) {
                                    Text("Grant Permission")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Helper function to check initial status when the app opens
    private fun checkCameraPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }
}