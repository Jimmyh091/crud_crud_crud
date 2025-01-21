package com.example.firabasecrud

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
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


class CrearObra : AppCompatActivity() {


    private lateinit var imagen: ImageView
    private lateinit var nombre: EditText
    private lateinit var autor: EditText
    private lateinit var estrellas: RatingBar
    private lateinit var fecha: Instant
    private lateinit var botonCrear: MaterialButton
    private lateinit var botonVolver: MaterialButton

    //Firebase
    private lateinit var database: DatabaseReference

    //private lateinit var storage: StorageReference
    private var rutaImagen: Uri? = null

    //AppWriteStorage
    private lateinit var id_projecto: String
    private lateinit var id_bucket: String


    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_crear_obra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var activity = this

        nombre = findViewById(R.id.nombre)
        autor = findViewById(R.id.autor)
        imagen = findViewById(R.id.imagen)
        estrellas = findViewById(R.id.estrellas)
        botonCrear = findViewById(R.id.botonCrear)
        botonVolver = findViewById(R.id.botonVolver)

        //firebase
        database = FirebaseDatabase.getInstance().reference
        //storage = FirebaseStorage.getInstance().reference

        //AppWriteStorage
        id_projecto = "675c8d190017d6961f5e"
        id_bucket = "675c8d79001356207764"

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
            .setProject(id_projecto)

        val storage = Storage(client)

        imagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }
        var listaObras = Util.obtenerListaObras(database, this)
        botonCrear.setOnClickListener {

            fecha = Instant.now()
            if (nombre.text.isEmpty() || autor.text.isEmpty() || rutaImagen == null || estrellas.rating <= 0) {
                Toast.makeText(
                    this,
                    "Rellene todos los campos o selecione una imagen",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (Util.existeObra(
                    listaObras,
                    nombre.text.toString()
                )
            ) {
                Toast.makeText(this, "Obra ya existe", Toast.LENGTH_SHORT).show()
            } else {

                val identificadorObra = database.child("arte").child("obras").push().key

                GlobalScope.launch(Dispatchers.IO) {
                    //val inputStream = contentResolver.openInputStream(rutaImagen!!)
                    val identificadorAppWrite = identificadorObra!!.substring(1, 20)
                    //var escudo = UtilCopia.guardarEscudo(storage,identificador_club!!,rutaImagen!!)
                    /* val fileImpostor = inputStream.use { input ->
                         val tempFile = kotlin.io.path.createTempFile(identificadorAppWrite).toFile()
                         if (input != null) {
                             tempFile.outputStream().use { output ->
                                 input.copyTo(output)
                             }
                         }
                         InputFile.fromFile(tempFile) // tenemos un archivo temporal con la imagen
                     }

                     val identificadorFile = ID.unique()
                     val file = storage.createFile(
                         bucketId = id_bucket,
                         fileId = identificadorFile,
                         file = fileImpostor
                     )*/
                    //PRUEBAS
                    var mimeType = ""
                    var nombreArchivo = ""
                    val inputStream = contentResolver.openInputStream(rutaImagen!!)
                    val aux = contentResolver.query(rutaImagen!!, null, null, null, null)
                    aux.use {
                        if (it!!.moveToFirst()) {
                            // Obtener el nombre del archivo
                            val nombreIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nombreIndex != -1) {
                                nombreArchivo = it.getString(nombreIndex)
                            }
                        }
                    }
                    mimeType = contentResolver.getType(rutaImagen!!).toString()

                    val fileInput = InputFile.fromBytes(
                        bytes = inputStream?.readBytes() ?: byteArrayOf(),
                        filename = nombreArchivo,
                        mimeType = mimeType
                    )
                    val identificadorFile = ID.unique()
                    val file = storage.createFile(
                        bucketId = id_bucket,
                        fileId = identificadorFile,
                        file = fileInput
                    )

                    var linkImagen =
                        "https://cloud.appwrite.io/v1/storage/buckets/$id_bucket/files/$identificadorFile/preview?project=$id_projecto"

                    val obra = Obra(
                        identificadorObra,
                        nombre.text.toString(),
                        autor.text.toString(),
                        estrellas.rating,
                        Util.obtenerFecha(),
                        linkImagen,
                        identificadorFile
                    )
                    Util.escribirObra(database, identificadorObra, obra)

                    Util.tostadaCorrutina(
                        activity, applicationContext,
                        "Imagen descargada con éxito"
                    )
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



