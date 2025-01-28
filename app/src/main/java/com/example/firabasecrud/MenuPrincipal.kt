package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipal : AppCompatActivity() {

    private lateinit var botonCrear: Button
    private lateinit var botonLista: Button

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