package com.example.firabasecrud.genero

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.example.firabasecrud.obras.Obra
import com.example.firabasecrud.obras.UtilGenero
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant
class EditarGenero : AppCompatActivity() {

    private lateinit var botonVolver: Button
    private lateinit var botonEditar: Button

    private lateinit var nombre: TextInputEditText

    private lateinit var database: DatabaseReference
    private lateinit var genero: Genero
    private lateinit var listaGeneros: MutableList<Genero>

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_genero)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        genero = intent.getStringExtra("genero") as Genero

        nombre = findViewById(R.id.nombreGenero)
        nombre.setText(genero.nombre)

        id_projecto = "674762dd002af7924291"
        id_bucket = "674762fb002a63512c24"

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")  // Your API Endpoint
            .setProject(id_projecto)

        val storage = Storage(client)

        var activity = this

        // Cargar lista de géneros desde la base de datos
        listaGeneros = UtilGenero.obtenerListaGeneros(database, this)

        botonEditar.setOnClickListener {
            if (nombre.text!!.isEmpty()) {
                Toast.makeText(
                    this,
                    "Rellene todos los campos o seleccione una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (UtilGenero.existeGenero(
                    listaGeneros,
                    nombre.text.toString()
                ) && nombre.text.toString().lowercase() != genero.nombre!!.lowercase()
            ) {
                Toast.makeText(this, "Género ya existe", Toast.LENGTH_SHORT).show()
            } else {
                val identificadorGenero = genero.id

                GlobalScope.launch(Dispatchers.IO) {
                    if (rutaImagen != null) {

                        val identificadorFile = ID.unique()

                        // Actualizar los datos del género
                        val generoActualizado = Genero(
                            identificadorGenero,
                            nombre.text.toString()
                        )

                        UtilGenero.escribirGenero(database, identificadorGenero!!, generoActualizado)
                    } else if (nombre.text.toString() != genero.nombre) {
                        val generoActualizado = Genero(
                            identificadorGenero,
                            nombre.text.toString()
                        )

                        UtilGenero.escribirGenero(database, identificadorGenero!!, generoActualizado)

                        UtilGenero.tostadaCorrutina(
                            activity, applicationContext,
                            "Género actualizado con éxito"
                        )
                    }
                }
                finish()
            }
        }

        botonVolver.setOnClickListener {
            finish()
        }
    }

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            rutaImagen = uri
            imagen.setImageURI(rutaImagen)
        }
    }
}
