package com.example.adoptie.localitate

data class LocalitateDTO(
    val id: Long = 0,
    val nume: String = "",
    val judet: String = "",
    val diacritice: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)
