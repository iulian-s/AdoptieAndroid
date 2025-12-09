package com.example.adoptie.anunt

import com.example.adoptie.localitate.LocalitateDTO
import com.example.adoptie.utilizator.UtilizatorDTO

data class AnuntDetails(
    val anunt: AnuntDTO,
    val user: UtilizatorDTO,
    val localitate: LocalitateDTO
)
