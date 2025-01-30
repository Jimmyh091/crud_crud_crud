package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firabasecrud.chat.VerChat
import com.example.firabasecrud.obras.CrearObra
import com.example.firabasecrud.obras.VerObras
import com.example.liga.VerMensaje

class MenuPrincipal : AppCompatActivity() {

    private lateinit var botonCrear: Button
    private lateinit var botonLista: Button
    private lateinit var botonCrearGenero: Button
    private lateinit var botonEditarGenero: Button
    private lateinit var chat : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        botonCrear = findViewById(R.id.crearObra)
        botonLista = findViewById(R.id.listaObras)

        botonCrearGenero = findViewById(R.id.creargenero)
        botonEditarGenero = findViewById(R.id.button2)

        chat = findViewById(R.id.chat)

        chat.setOnClickListener {
            val intent = Intent(this, VerChat::class.java)
            startActivity(intent)
        }

        botonCrear.setOnClickListener {
            val intent = Intent(this, CrearObra::class.java)
            startActivity(intent)
        }

        botonLista.setOnClickListener {
            val intent = Intent(this, VerObras::class.java)
            startActivity(intent)
        }

    }
}