package com.example.simpleqrscanner.analyzer

import android.graphics.ImageFormat
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class CameraXZxingAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
): ImageAnalysis.Analyzer {

    // reader will specifically look for QR codes
    private val reader = MultiFormatReader().apply {
        val hints = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)
        )

        setHints(hints)
    }

    override fun analyze(imageProxy: ImageProxy) {
        // CameraX default format for ImageAnalysis is YUV_420_888
        if (imageProxy.format == ImageFormat.YUV_420_888) {
            // get the y plane image buffer
            val buffer = imageProxy.planes[0].buffer;
            val data = buffer.toByteArray()

            val source = PlanarYUVLuminanceSource(
                data,
                imageProxy.width,
                imageProxy.height,
                0, 0,
                imageProxy.width,
                imageProxy.height,
                false
            )

            // convert to a binary bitmap that zxing can process
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = reader.decode(binaryBitmap)
                onQrCodeScanned(result.text)
            } catch (e: NotFoundException) {
                // No QR code found in this frame, move along silently
            } finally {
                imageProxy.close()
            }
        }
    }

    // helper function to convert bytebuffer safely to bytearray
    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }
}