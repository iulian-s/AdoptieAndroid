package com.example.adoptie.anunt

sealed class AnunturiState{
    object Loading: AnunturiState()
    data class Success(val anunturi: List<AnuntDTO>) : AnunturiState()
    data class Error(val message: String) : AnunturiState()
}
