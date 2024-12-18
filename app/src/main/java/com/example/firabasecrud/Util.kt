package com.example.firabasecrud

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class Util {
    companion object {

        fun obtenerFecha(): String {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            return formatter.format(Date())
        }

        fun existeObra(obras: List<Obra>, nombre: String): Boolean {
            return obras.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }


        fun obtenerListaObras(db_ref: DatabaseReference, contexto: Context): MutableList<Obra> {
            val listaObras = mutableListOf<Obra>()

            db_ref.child("arte").child("obras")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { obra ->
                            val obraActual = obra.getValue(Obra::class.java)
                            listaObras.add(obraActual!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(contexto, "Error al obtener las obras", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            return listaObras
        }

        fun escribirObra(db_ref: DatabaseReference,id: String, obra : Obra) {
            db_ref.child("arte").child("obras").child(id).setValue(obra)
        }

        //LO CAMBIAREMOS
        suspend fun guardarImagen(almacen: StorageReference, id: String, imagen: Uri): String {
            var urlAlmacen: Uri
            urlAlmacen =
                almacen.child("imagenes").child(id).putFile(imagen).await()
                    .storage.downloadUrl.await()

            return urlAlmacen.toString()
        }

        fun tostadaCorrutina(activity: AppCompatActivity, contexto: Context, texto: String){
            activity.runOnUiThread{
                Toast.makeText(contexto,texto,Toast.LENGTH_SHORT).show()
            }
        }

        fun animacionCarga(contexto: Context): CircularProgressDrawable {
            val animacion = CircularProgressDrawable(contexto)
            animacion.strokeWidth = 5f
            animacion.centerRadius = 30f
            animacion.start()

            return animacion
        }

        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context): RequestOptions {
            val options = RequestOptions()
                .placeholder(animacionCarga(context))
                .fallback(R.drawable.fotogaleria)
                .error(R.drawable.error_404)
            return options
        }


    }
}