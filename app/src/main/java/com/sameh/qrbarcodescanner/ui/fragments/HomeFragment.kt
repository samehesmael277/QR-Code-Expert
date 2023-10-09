package com.sameh.qrbarcodescanner.ui.fragments

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.sameh.qrbarcodescanner.R
import com.sameh.qrbarcodescanner.databinding.FragmentHomeBinding
import com.sameh.qrbarcodescanner.extentions.showToast
import com.sameh.qrbarcodescanner.extentions.toLog
import com.sameh.qrbarcodescanner.ui.activity.MainActivity
import com.sameh.qrbarcodescanner.ui.dialogs.buildQRScanResultDialog
import com.sameh.qrbarcodescanner.zxing.decodeQRCode
import com.sameh.qrbarcodescanner.zxing.initialQRBarcodeScanner


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setActions()
        handleQRScannerResult()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setActions() {
        binding.apply {
            btnScan.setOnClickListener {
                initialQRBarcodeScanner(requireActivity())
            }
            btnChooseLocalImage.setOnClickListener {
                chooseImageFromGallery()
            }
            btnGenerate.setOnClickListener {
                navigateToGenerateQRBarcodeFragment()
            }
        }
    }

    private fun handleQRScannerResult() {
        (activity as MainActivity).onGetQRScanResult {
            "data from home fragment: $it".toLog()
            showResultDialog(it)
        }
    }

    private fun showResultDialog(value: String) {
        requireContext().buildQRScanResultDialog(value) { scanResult ->
            // on copy clicked
            copyTextToClipboard(scanResult)
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        resultLauncherOfChooseImage.launch(intent)
    }

    private var resultLauncherOfChooseImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                val uri = intent!!.data

                if (uri != null) {
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        val qrResult = decodeQRCode(bitmap)
                        if (qrResult != null)
                            showResultDialog(qrResult)
                        else
                            "failed to decode qr code".toLog()
                    } catch (e: Exception) {
                        e.message?.toLog()
                        e.message?.showToast(requireContext())
                    }
                }

            } else {
                "No Image Selected".toLog()
            }
        }

    private fun copyTextToClipboard(text: String) {
        try {
            val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
            "Text copied to clipboard".toLog()
            "Text copied to clipboard".showToast(requireContext())
        } catch (e: Exception) {
            e.message?.toLog()
        }
    }

    private fun navigateToGenerateQRBarcodeFragment() {
        val id = findNavController().currentDestination?.id

        if (id == R.id.homeFragment) {
            val action = HomeFragmentDirections.actionHomeFragmentToGenerateQRBarcodeFragment()
            findNavController().navigate(action)
        }
    }
}