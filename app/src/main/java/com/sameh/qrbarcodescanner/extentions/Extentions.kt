package com.sameh.qrbarcodescanner.extentions

import android.content.Context
import android.util.Log
import android.widget.Toast

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun String.toLog(tag: String = "applicationTAG") {
    Log.d(tag, "Data: $this")
}