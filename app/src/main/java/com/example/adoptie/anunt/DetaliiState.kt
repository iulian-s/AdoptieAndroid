package com.example.adoptie.anunt

sealed class DetaliiState {
    object Loading : DetaliiState()
    data class Success(val details: AnuntDetails) : DetaliiState()
    data class Error(val message: String) : DetaliiState()
}