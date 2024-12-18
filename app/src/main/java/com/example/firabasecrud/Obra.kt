package com.example.firabasecrud

import java.io.Serializable
import java.time.Instant

data class Obra(
    var id : String? = "",
    var nombre : String? = "",
    var descripcion : String? = "",
    var estrellas : Int? = 0,
    var fecha : String? = Util.parsearFecha(Instant.now()),
    var rutaImagen : String? = "",
    var idImagen : String? = ""
):Serializable
