package com.sameh.qrbarcodescanner.ui.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.sameh.qrbarcodescanner.databinding.DialogQrScanResultBinding

fun Context.buildQRScanResultDialog(result: String, onCopyClicked: ((String) -> Unit)) {
    val binding = DialogQrScanResultBinding.inflate(LayoutInflater.from(this))
    val alertDialogBuilder = AlertDialog.Builder(this)
        .setView(binding.root)

    binding.tvQrScanResult.text = result

    val alertDialog = alertDialogBuilder.create()
    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.setCancelable(false)
    alertDialog.show()

    binding.icClose.setOnClickListener {
        alertDialog.dismiss()
    }
    binding.btnCopy.setOnClickListener {
        onCopyClicked(result)
        alertDialog.dismiss()
    }
}