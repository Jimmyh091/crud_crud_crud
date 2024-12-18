package com.example.firabasecrud

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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.appwrite.Client
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class EditarObra : AppCompatActivity() {

    private lateinit var imagen: ImageView
    private lateinit var nombre: EditText
    private lateinit var descripcion: EditText
    private lateinit var estrellas: RatingBar
    private lateinit var fecha: String
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
        setContentView(R.layout.activity_editar_club)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        imagen = findViewById(R.id.imagen)
        nombre = findViewById(R.id.nombre)
        descripcion = findViewById(R.id.descripcion)
        estrellas = findViewById(R.id.estrellas)
        fecha = ""
        botonModificar = findViewById(R.id.botonModificar)
        botonVolver = findViewById(R.id.botonVolver)
        database = FirebaseDatabase.getInstance().reference
        //storage = FirebaseStorage.getInstance().reference

        obra = intent.getSerializableExtra("club") as Obra

        nombre.setText(obra.nombre)
        descripcion.setText(obra.descripcion)


        id_projecto = "674762dd002af7924291"
        id_bucket = "674762fb002a63512c24"

        val client = Client()
            .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
            .setProject(id_projecto)

        val storage = Storage(client)

        var activity = this


        Glide.with(applicationContext)
            .load(obra.rutaImagen)
            .apply(UtilCopia.opcionesGlide(applicationContext))
            .transition(UtilCopia.transicion)
            .into(imagen)



        imagen.setOnClickListener {
            accesoGaleria.launch("image/*")
        }

        listaObras = UtilCopia.obtenerListaObras(database, this)
        botonModificar.setOnClickListener {
            if (nombre.text.isEmpty() || descripcion.text.isEmpty() ) {
                Toast.makeText(
                    this,
                    "Rellene todos los campos o selecione una imagen",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (UtilCopia.existeObra(
                    listaObras,
                    nombre.text.toString()
                ) && nombre.text.toString().lowercase() != obra.nombre!!.lowercase()
            ) {
                Toast.makeText(this, "Club ya existe", Toast.LENGTH_SHORT).show()
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
                        val club = Obra(
                            identificadorObra,
                            nombre.text.toString(),
                            descripcion.text.toString(),
                            estrellas.rating.toInt(),
                            fecha,
                            imagen,
                            identificadorFile
                        )
                        UtilCopia.escribirObra(database, identificadorObra!!, club)
                    }else if (nombre.text.toString()!=obra.nombre || descripcion.text.toString()!=obra.descripcion
                        || fecha!=obra.fecha!!){
                        val club = Obra(
                            identificadorObra,
                            nombre.text.toString(),
                            descripcion.text.toString(),
                            estrellas.rating.toInt(),
                            fecha,
                            obra.rutaImagen,
                            obra.idImagen
                        )
                        UtilCopia.escribirObra(database, identificadorObra!!, obra)

                        UtilCopia.tostadaCorrutina(activity,applicationContext,
                            "Obra actualizada con éxito")
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