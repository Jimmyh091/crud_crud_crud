package com.example.firabasecrud.genero

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.firabasecrud.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class UtilGenero {
    companion object {

        fun existeGenero(generos: List<Genero>, nombre: String): Boolean {
            return generos.any { it.nombre!!.lowercase() == nombre.lowercase() }
        }

        fun obtenerListaGeneros(db_ref: DatabaseReference, contexto: Context): MutableList<Genero> {
            val listaGeneros = mutableListOf<Genero>()

            db_ref.child("arte").child("generos")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { genero ->
                            val generoActual = genero.getValue(Genero::class.java)
                            listaGeneros.add(generoActual!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(contexto, "Error al obtener los generos", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            return listaGeneros
        }

        fun escribirGenero(db_ref: DatabaseReference, id: String, genero: Genero) {
            db_ref.child("arte").child("generos").child(id).setValue(genero)
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
