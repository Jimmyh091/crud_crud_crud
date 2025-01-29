package com.example.firabasecrud.chat

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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

class Utilidades {
    companion object {
        fun escribirClub(db_ref:DatabaseReference,id:String,nombre:String,ciudad:String,fundacion:Int,url_firebase:String,estado:Int,notificador:String)=
            db_ref.child("liga").child("clubs").child(id).setValue(Club(
                id,
                nombre,
                ciudad,
                fundacion,
                url_firebase,
                estado,
                notificador
            ))

        fun animacion_carga(contexto: Context): CircularProgressDrawable {
            val animacion=CircularProgressDrawable(contexto)
            animacion.strokeWidth=5f
            animacion.centerRadius=30f
            animacion.start()

            return animacion
        }
        val transicion = DrawableTransitionOptions.withCrossFade(500)

        fun opcionesGlide(context: Context):RequestOptions {
            val options = RequestOptions()
                .placeholder(animacion_carga(context))
                .fallback(R.drawable.escudo_generico)
                .error(R.drawable.error_404)
            return options
        }

        suspend fun guardarEscudo(sto_ref:StorageReference,id:String,imagen:Uri):String{
            lateinit var url_escudo_firebase:Uri

            url_escudo_firebase=sto_ref.child("liga").child("escudos").child(id)
                .putFile(imagen).await().storage.downloadUrl.await()

            return url_escudo_firebase.toString()
        }

        fun existeClub(clubs:List<Club>,nombre:String):Boolean{
            return clubs.any { it.nombre!!.lowercase()==nombre.lowercase() }
        }

        fun tostadaCorrutina(activity: AppCompatActivity,contexto: Context,texto:String){
            activity.runOnUiThread{
                Toast.makeText(
                    contexto,
                    texto,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        //Obtener lista datos pero no para usar de manera sincrona o sea que algo dependa directamente de el
        fun obtenerListaClubs(db_ref: DatabaseReference):MutableList<Club>{
            var lista= mutableListOf<Club>()
            //Consulta a la bd
            db_ref.child("liga")
                .child("clubs")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        lista.clear()
                        snapshot.children.forEach{ hijo: DataSnapshot?->
                            val pojo_club=hijo?.getValue(Club::class.java)
                            lista.add(pojo_club!!)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })
            return lista
        }

        //despues hacer otra version con el adapter

    }
}