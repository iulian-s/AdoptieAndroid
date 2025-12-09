package com.example.adoptie.anunt

object AnunturiRoutes {
    const val LIST = "anunturi/list"
    const val DETAILS = "anunturi/details/{anuntId}"

    fun detailsRoute(anuntId: Long) = "anunturi/details/$anuntId"
}