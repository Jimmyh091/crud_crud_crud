package com.example.firabasecrud.obras

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.firabasecrud.R
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ObraAdaptador(private val listaObras: MutableList<Obra>) :
    RecyclerView.Adapter<ObraAdaptador.ObraViewHolder>() {

    private lateinit var contexto: Context
    private var lista_filtrada = listaObras

    inner class ObraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val ciudad: TextView = itemView.findViewById(R.id.item_ciudad)
        val fundacion: TextView = itemView.findViewById(R.id.item_fundacion)
        val ratingBar: RatingBar = itemView.findViewById(R.id.ratingBar)
        val editar: ImageView = itemView.findViewById(R.id.item_editar)
        val borrar: ImageView = itemView.findViewById(R.id.item_borrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_obra,parent,false)
        contexto=parent.context
        return ObraViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: ObraViewHolder, position: Int) {
        val obraActual = lista_filtrada[position]
        holder.nombre.text = obraActual.nombre
        holder.ciudad.text = obraActual.autor
        holder.fundacion.text = obraActual.fecha.toString()
        holder.ratingBar.rating = obraActual.estrellas!!

        val URL:String?=when(obraActual.rutaImagen){
            ""->null //Para que active imagen de fallback
            else->obraActual.rutaImagen
        }
        Log.d("Ruta",URL.toString())

        Glide.with(contexto)
            .load(URL)
            .apply(Util.opcionesGlide(contexto))
            .transition(Util.transicion)
            .into(holder.miniatura)

        holder.editar.setOnClickListener{
            val intent= Intent(contexto, EditarObra::class.java)
            intent.putExtra("obra",obraActual)
            contexto.startActivity(intent)
        }

        holder.borrar.setOnClickListener{
            val db_ref= FirebaseDatabase.getInstance().reference
            //val storage_ref= FirebaseStorage.getInstance().reference //?
            val id_projecto = "675c8d190017d6961f5e"
            val id_bucket = "675c8d79001356207764"

            val client = Client()
                .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
                .setProject(id_projecto)

            val storage = Storage(client)
            GlobalScope.launch(Dispatchers.IO) {
                storage.deleteFile(
                    bucketId = id_bucket,
                    fileId = obraActual.idImagen!!
                )
            }

            lista_filtrada.removeAt(position)
            //storage_ref.child("nba").child("clubs").child(obraActual.id!!).delete() //?
            db_ref.child("arte").child("obras").child(obraActual.id!!).removeValue() //?
            Toast.makeText(contexto,"Obra borrada",Toast.LENGTH_SHORT).show()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,lista_filtrada.size)

        }

    }


}