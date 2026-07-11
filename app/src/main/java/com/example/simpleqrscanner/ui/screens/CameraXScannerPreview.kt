package com.example.simpleqrscanner.ui.screens

import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.simpleqrscanner.analyzer.CameraXZxingAnalyzer
import java.util.concurrent.Executors

@Composable
fun CameraXScannerPreview(
    modifier: Modifier = Modifier,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifeCycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // 1. Live Preview Use case
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                // 2. Image Analysis use case where ZXing lives
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            Executors.newSingleThreadExecutor(),
                            CameraXZxingAnalyzer { qrText ->
                                onQrCodeScanned(qrText)
                            }
                        )
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifeCycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (exc: Exception) {
                    Log.e("CameraXScannerUI", "Use case binding failed", exc)
                }

            }, ContextCompat.getMainExecutor(context))

            previewView
        },

        modifier = modifier.fillMaxSize()
    )
}