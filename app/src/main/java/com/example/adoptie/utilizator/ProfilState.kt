package com.example.adoptie.utilizator

sealed class ProfilState {
    object Loading : ProfilState()
    data class Success(val details: ProfilDetails) : ProfilState()
    data class Error(val message: String) : ProfilState()
}