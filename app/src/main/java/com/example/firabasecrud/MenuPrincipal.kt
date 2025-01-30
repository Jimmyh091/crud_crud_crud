package com.example.firabasecrud

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firabasecrud.chat.VerChat
import com.example.firabasecrud.genero.CrearGenero
import com.example.firabasecrud.genero.EditarGenero
import com.example.firabasecrud.genero.SeleccionarGeneros
import com.example.firabasecrud.genero.VerGeneros
import com.example.firabasecrud.obras.CrearObra
import com.example.firabasecrud.obras.VerObras
import com.google.android.material.textfield.TextInputEditText

class MenuPrincipal : AppCompatActivity() {

    private lateinit var botonCrear: Button
    private lateinit var botonLista: Button
    private lateinit var botonCrearGenero: Button
    private lateinit var botonVerGeneros: Button
    private lateinit var chat : ImageView
    private lateinit var enviarUsuario : ImageView

    private var usuario: String = ""

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
        botonVerGeneros = findViewById(R.id.vergeneros)

        enviarUsuario = findViewById(R.id.enviar)

        chat = findViewById(R.id.chat)

        enviarUsuario.setOnClickListener {
            var nomUs = findViewById<TextInputEditText>(R.id.tiet_nombreUs)
            var text = nomUs.text.toString()
            if (text.isNotBlank() && text.isNotEmpty()) {

                var tituloUs = findViewById<TextView>(R.id.usuario)
                tituloUs.text = "Nombre usuario: $text"
                nomUs.setText("")

                usuario = text
                intent.putExtra("usuario",usuario)
            }else{
                Toast.makeText(applicationContext, "Introduce el nombre de usuario", Toast.LENGTH_SHORT).show()
            }
        }

        chat.setOnClickListener {
            if (usuario.isBlank() || usuario.isEmpty()){
                Toast.makeText(applicationContext, "No hay un usuario introducido", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, VerChat::class.java)
                intent.putExtra("usuario",usuario)
                startActivity(intent)
            }
        }

        botonCrear.setOnClickListener {
            val intent = Intent(this, CrearObra::class.java)
            startActivity(intent)
        }

        botonLista.setOnClickListener {
            val intent = Intent(this, VerObras::class.java)
            startActivity(intent)
        }

        botonCrearGenero.setOnClickListener {
            val intent = Intent(this, CrearGenero::class.java)
            startActivity(intent)
        }

        botonVerGeneros.setOnClickListener {
            val intent = Intent(this, VerGeneros::class.java)
            startActivity(intent)
        }
    }
}