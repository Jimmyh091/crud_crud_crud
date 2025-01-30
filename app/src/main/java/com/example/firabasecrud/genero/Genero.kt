package com.example.firabasecrud.genero

import java.io.Serializable

data class Genero(
    var id : String? = "",
    var nombre : String? = "",
    var checked : Boolean = false
):Serializable
