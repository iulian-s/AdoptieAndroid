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