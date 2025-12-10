package com.example.adoptie.anunt

object AnunturiRoutes {
    const val LIST = "anunturi/list"
    const val DETAILS = "anunturi/details/{anuntId}"

    const val USER_PROFILE = "anunturi/profile/{userId}"

    fun detailsRoute(anuntId: Long) = "anunturi/details/$anuntId"
    fun profileRoute(userId: Long) = "anunturi/profile/$userId"
}