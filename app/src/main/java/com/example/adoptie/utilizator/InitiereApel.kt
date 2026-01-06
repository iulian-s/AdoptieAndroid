package com.example.adoptie.utilizator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

/**
 * Deschide aplicația de apelare cu numărul specificat.
 */
fun initiatePhoneCall(context: Context, phoneNumber: String) {
    val uri = "tel:$phoneNumber".toUri()

    val intent = Intent(Intent.ACTION_DIAL, uri)

    context.startActivity(intent)
}