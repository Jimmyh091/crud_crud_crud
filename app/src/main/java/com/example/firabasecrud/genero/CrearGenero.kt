package com.example.firabasecrud.genero

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.firabasecrud.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.Instant

class CrearGenero : AppCompatActivity() {

    private lateinit var botonCrear: MaterialButton
    private lateinit var botonVolver: MaterialButton

    private lateinit var nombre: TextInputEditText

    //Firebase
    private lateinit var database: DatabaseReference

    // borrar
    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_genero)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var activity = this

        nombre = findViewById(R.id.tiet_nombreGenero)
        botonCrear = findViewById(R.id.botonCrear)
        botonVolver = findViewById(R.id.botonVolver)

        //firebase
        database = FirebaseDatabase.getInstance().reference
        //storage = FirebaseStorage.getInstance().reference

        var listaGeneros = UtilGenero.obtenerListaGeneros(database, this)
        botonCrear.setOnClickListener {

            val fecha = Instant.now()
            if (nombre.text?.isEmpty() == true) {
                Toast.makeText(
                    this,
                    "Introduzca el nombre del genero",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (UtilGenero.existeGenero(
                    listaGeneros,
                    nombre.text.toString()
                )
            ) {
                Toast.makeText(this, "Género ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificadorGenero = database.child("arte").child("generos").push().key

                GlobalScope.launch(Dispatchers.IO) {

                    val genero = Genero(
                        identificadorGenero,
                        nombre.text.toString()
                    )

                    if (identificadorGenero != null) {
                        UtilGenero.escribirGenero(database, identificadorGenero, genero)
                        UtilGenero.tostadaCorrutina(
                            activity, applicationContext,
                            "Género creado con éxito"
                        )
                    }else{
                        UtilGenero.tostadaCorrutina(
                            activity, applicationContext,
                            "Error con ID del genero"
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
}


