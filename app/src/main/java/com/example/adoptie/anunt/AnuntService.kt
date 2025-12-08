package com.example.adoptie.anunt

sealed class AnunturiState{
    object Loading: AnunturiState()
    data class Success(val anunturi: List<AnuntDTO>) : AnunturiState()
    data class Error(val message: String) : AnunturiState()
}

// GET /api/anunturi/active
suspend fun fetchAnunturi(): List<AnuntDTO> {
    kotlinx.coroutines.delay(1000)
    // Simulare de date returnate, bazate pe AnuntDTO.kt
    return listOf(
        AnuntDTO(
            id = 1,
            titlu = "Cățeluș jucăuș, caută cămin",
            descriere = "...",
            specie = "Câine",
            rasa = "Labrador",
            gen = Gen.MASCUL,
            varsta = Varsta.UNU_TREI_ANI,
            listaImagini = listOf("http://tavabackend.com/imagini/dog1.jpg"),
            locatieId = 3,
            stare = Stare.ACTIV,
            utilizatorId = 2
        ),
        AnuntDTO(
            id = 2,
            titlu = "Pisică siameză superbă",
            descriere = "...",
            specie = "Pisică",
            rasa = "Siameză",
            gen = Gen.FEMELA,
            varsta = Varsta.TREI_CINCI_ANI,
            listaImagini = listOf("http://tavabackend.com/imagini/cat1.jpg"),
            locatieId = 2,
            stare = Stare.ACTIV,
            utilizatorId = 2
        )
    )
}