package com.example.firabasecrud.obras

import android.annotation.SuppressLint
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

class EditarObra : AppCompatActivity() {

    private lateinit var imagen: ImageView
    private lateinit var nombre: EditText
    private lateinit var autor: EditText
    private lateinit var estrellas: RatingBar
    private lateinit var botonModificar: Button
    private lateinit var botonVolver: Button

    private lateinit var database: DatabaseReference
    //private lateinit var storage: StorageReference
    private var rutaImagen: Uri? = null
    private lateinit var obra : Obra
    private lateinit var listaObras: MutableList<Obra>

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_obra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagen = findViewById(R.id.imagen)
        nombre = findViewById(R.id.nombre)
        autor = findViewById(R.id.autor)
        estrellas = findViewById(R.id.estrellas)
        botonModificar = findViewById(R.id.botonModificar)
        botonVolver = findViewById(R.id.botonVolver)
        database = FirebaseDatabase.getInstance().reference
        //storage = FirebaseStorage.getInstance().reference

        obra = intent.getSerializableExtra("obra") as Obra

        nombre.setText(obra.nombre)
        autor.setText(obra.autor)
        estrellas.rating = obra.estrellas!!.toFloat()

        id_projecto = "674762dd002af7924291"
        id_bucket = "674762fb002a63512c24"

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
            .setProject(id_projecto)

        val storage = Storage(client)

        var activity = this


        Glide.with(applicationContext)
            .load(obra.rutaImagen)
            .apply(Util.opcionesGlide(applicationContext))
            .transition(Util.transicion)
            .into(imagen)



        imagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        listaObras = Util.obtenerListaObras(database, this)
        botonModificar.setOnClickListener {
            if (nombre.text.isEmpty() || autor.text.isEmpty() || estrellas.rating <= 0f ) {
                Toast.makeText(
                    this,
                    "Rellene todos los campos o selecione una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (Util.existeObra(
                    listaObras,
                    nombre.text.toString()
                ) && nombre.text.toString().lowercase() != obra.nombre!!.lowercase()
            ) {
                Toast.makeText(this, "Obra ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificadorObra = obra.id

                GlobalScope.launch(Dispatchers.IO) {

                    if (rutaImagen != null){

                        //var escudo = Util.guardarEscudo(storage,identificador_club!!,url_escudo!!)
                        val identificadorFile = ID.unique()

                        storage.deleteFile(
                            bucketId = id_bucket,
                            fileId = obra.idImagen!!
                        )


                        val inputStream = contentResolver.openInputStream(rutaImagen!!)
                        val fileImpostor = inputStream.use { input ->
                            val tempFile = kotlin.io.path.createTempFile(identificadorFile).toFile()
                            if (input != null) {
                                tempFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            InputFile.fromFile(tempFile) // tenemos un archivo temporal con la imagen
                        }

                        val file = storage.createFile(
                            bucketId = id_bucket,
                            fileId = identificadorFile,
                            file = fileImpostor,
                        )


                        var imagen =
                            "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto"
                        val obra = Obra(
                            identificadorObra,
                            nombre.text.toString(),
                            autor.text.toString(),
                            estrellas.rating,
                            listOf(),
                            Instant.now().toString(),
                            imagen,
                            identificadorFile
                        )
                        Util.escribirObra(database, identificadorObra!!, obra)
                    }else if (nombre.text.toString() != obra.nombre || autor.text.toString() != obra.autor|| estrellas.rating != obra.estrellas){
                        val obra = Obra(
                            identificadorObra,
                            nombre.text.toString(),
                            autor.text.toString(),
                            estrellas.rating,
                            listOf(),
                            Instant.now().toString(),
                            obra.rutaImagen,
                            obra.idImagen
                        )
                        Util.escribirObra(database, identificadorObra!!, obra)

                        Util.tostadaCorrutina(
                            activity, applicationContext,
                            "Obra actualizada con éxito"
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

    private val accesoGaleria = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        if (uri != null) {
            rutaImagen = uri
            imagen.setImageURI(rutaImagen)
        }
    }
}