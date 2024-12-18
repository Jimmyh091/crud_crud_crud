package com.example.firabasecrud

import java.io.Serializable

data class Obra(
    var id : String? = "",
    var nombre : String? = "",
    var descripcion : String? = "",
    var estrellas : Int? = 0,
    var fecha : String? = "",
    var rutaImagen : String? = "",
    var idImagen : String? = ""
):Serializable
