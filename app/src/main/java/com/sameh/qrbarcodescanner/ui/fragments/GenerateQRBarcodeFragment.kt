package com.sameh.qrbarcodescanner.ui.fragments

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sameh.qrbarcodescanner.R
import com.sameh.qrbarcodescanner.databinding.FragmentGenerateQRBarcodeBinding
import com.sameh.qrbarcodescanner.extentions.showToast
import com.sameh.qrbarcodescanner.extentions.toLog
import com.sameh.qrbarcodescanner.zxing.generateQRBarcode
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class GenerateQRBarcodeFragment : Fragment() {

    private var _binding: FragmentGenerateQRBarcodeBinding? = null
    private val binding get() = _binding!!

    private var currentResultBitmap: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGenerateQRBarcodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setActions() {
        binding.apply {
            btnGenerate.setOnClickListener {
                handleGenerateQRCodeValidation()
            }
            icSavePhoto.setOnClickListener {
                if (currentResultBitmap != null) {
                    val path = saveQRImageBitmapToGallery()
                    "image saved at: $path".toLog()
                    "image saved at: $path".showToast(requireContext())
                }
            }
            icBack.setOnClickListener {
                navigateBackToHomeFragment()
            }
        }
    }

    private fun handleGenerateQRCodeValidation() {
        val qrCodeValue = binding.etQrValue.text.toString()
        if (qrCodeValue.isNotEmpty()) {
            generateQRCode(qrCodeValue)
        } else
            "You Should Enter QR Value".showToast(requireContext())
    }

    private fun generateQRCode(value: String) {
        val qrBitmap = generateQRBarcode(value)
        if (qrBitmap != null) {
            currentResultBitmap = qrBitmap
            binding.ivQrBarcode.setImageBitmap(qrBitmap)
            binding.icSavePhoto.visibility = View.VISIBLE
        }
    }

    private fun saveQRImageBitmapToGallery(): String {
        val picturesDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val directory = File(picturesDirectory, "QRBarcode")

        if (!directory.exists()) {
            directory.mkdirs() // Create the directory if it doesn't exist
        }

        val timestamp = System.currentTimeMillis()
        val fileName = "qr_$timestamp.jpg"
        val myPath = File(directory, fileName)

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(myPath)
            // Use the compress method on the Bitmap object to write the image to the OutputStream
            currentResultBitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            // Notify the media scanner to add the saved image to the gallery
            MediaScannerConnection.scanFile(
                requireContext(),
                arrayOf(myPath.toString()),
                null
            ) { path, uri ->
                // You can handle the scan completed callback here if needed
                "saveQRImageBitmapToGallery -> path: $path <-> uri: $uri".toLog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        return directory.absolutePath
    }


    private fun navigateBackToHomeFragment() {
        val id = findNavController().currentDestination?.id

        if (id == R.id.generateQRBarcodeFragment) {
            findNavController().popBackStack()
        }
    }
}