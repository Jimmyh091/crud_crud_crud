package com.example.firabasecrud.obras

import java.io.Serializable

data class Obra(
    var id : String? = "",
    var nombre : String? = "",
    var autor : String? = "",
    var estrellas : Float? = 0f,
    var fecha : String? = Util.obtenerFecha(),
    var rutaImagen : String? = "",
    var idImagen : String? = ""
):Serializable
