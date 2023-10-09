package com.sameh.qrbarcodescanner.zxing

import android.app.Activity
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.sameh.qrbarcodescanner.extentions.toLog

fun initialQRBarcodeScanner(activity: Activity) {
    val integrator = IntentIntegrator(activity)
    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
    integrator.setPrompt("Scan a QR Code")
    integrator.setCameraId(0)  // Use the device's rear camera by default
    integrator.setBeepEnabled(true)  // Play a beep sound when a barcode is detected
    integrator.setBarcodeImageEnabled(true)  // Save the captured image
    integrator.setOrientationLocked(false)
    integrator.initiateScan()
}

fun generateQRBarcode(value: String): Bitmap? {
    return try {
        val barcodeEncoder = BarcodeEncoder()
        barcodeEncoder.encodeBitmap(value, BarcodeFormat.QR_CODE, 400, 400)
    } catch (e: Exception) {
        e.message?.toLog()
        null
    }
}

fun decodeQRCode(bitmap: Bitmap): String? {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val source = RGBLuminanceSource(width, height, pixels)
    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

    try {
        val result = MultiFormatReader().decode(binaryBitmap)
        return result.text
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}