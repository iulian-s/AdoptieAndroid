package com.example.adoptie.utilizator

import com.example.adoptie.anunt.AnuntDTO

data class ProfilDetails(
    val user: UtilizatorDTO,
    val userAnunturi: List<AnuntDTO>
)
