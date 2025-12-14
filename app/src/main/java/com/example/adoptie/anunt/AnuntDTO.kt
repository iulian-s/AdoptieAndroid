package com.example.adoptie.anunt
enum class Gen {
    MASCUL, FEMELA
}
enum class Stare {
    NEVERIFICAT, ACTIV, INACTIV
}

data class AnuntDTO(
    val id: Long,
    val titlu: String,
    val descriere: String,
    val specie: String,
    val rasa: String,
    val gen: Gen,
    val varsta: Varsta,
    val varstaMin: Int? = null,
    val varstaMax: Int? = null,
    val listaImagini: List<String>,
    val locatieId: Long,
    var stare: Stare = Stare.NEVERIFICAT,
    val utilizatorId: Long = 0,
    val updatedAt: String = ""
)

enum class Varsta(val display: String, val minLuni: Int?, val maxLuni: Int?) {
    ZERO_TREI_LUNI("0-3 luni", 0, 3),
    TREI_SASE_LUNI("3-6 luni", 3, 6),
    SASE_DOISPREZECE_LUNI("6-12 luni", 6, 12),
    UNU_TREI_ANI("1-3 ani", 12, 36),
    TREI_CINCI_ANI("3-5 ani", 36, 60),
    CINCI_PLUS_ANI("5+ ani", 60, null),
    NECUNOSCUT("Necunoscut", null, null);

    companion object {
        fun getAll() = entries
    }
}