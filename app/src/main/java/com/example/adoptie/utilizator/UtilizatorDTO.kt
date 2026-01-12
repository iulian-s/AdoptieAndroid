package com.example.adoptie.utilizator


data class UtilizatorDTO(
    val id: Long,
    val username: String,
    val nume: String,
    val email: String,
    val rol: Rol,
    var localitateId: Long,
    val dataCreare: String,
    val telefon: String,
    val avatar: String? = null,
    var anuntIds: MutableList<Long>
)

enum class Rol {
    USER, ADMIN
}

data class EditareUtilizatorDTO(
    val nume: String,
    var localitateId: Long = 0,
    val telefon: String,
    val avatar: String? = null,
    val parolaVeche: String? = null,
    val parolaNoua: String? = null
)