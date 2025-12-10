package com.example.adoptie.utilizator

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

/**
 * Deschide aplicația de apelare cu numărul specificat.
 */
fun initiatePhoneCall(context: Context, phoneNumber: String) {
    // 1. Asigură-te că numărul este prefixat cu 'tel:' pentru Intent
    val uri = "tel:$phoneNumber".toUri()

    // 2. Creează Intent-ul
    val intent = Intent(Intent.ACTION_DIAL, uri)

    // 3. Lansează aplicația
    context.startActivity(intent)
}