package com.example.firabasecrud.genero

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.R
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client
import io.appwrite.services.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GeneroAdaptador(private val listaGenero: MutableList<Genero>) :
    RecyclerView.Adapter<GeneroAdaptador.GeneroViewHolder>() {

    private lateinit var contexto: Context
    private var lista_filtrada = listaGenero

    inner class GeneroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val borrar: ImageView = itemView.findViewById(R.id.item_borrar)
        val editar: ImageView = itemView.findViewById(R.id.item_editar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GeneroViewHolder {
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_genero, parent, false)
        contexto = parent.context
        return GeneroViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: GeneroViewHolder, position: Int) {
        val generoActual = lista_filtrada[position]
        holder.nombre.text = generoActual.nombre

        holder.borrar.setOnClickListener {
            val db_ref= FirebaseDatabase.getInstance().reference

            //val storage_ref= FirebaseStorage.getInstance().reference //?
            val id_projecto = "675c8d190017d6961f5e"

            val client = Client()
                .setEndpoint("https://cloud.appwrite.io/v1")    // Your API Endpoint
                .setProject(id_projecto)

            lista_filtrada.removeAt(position)
            db_ref.child("arte").child("generos").child(generoActual.id!!).removeValue()
            Toast.makeText(contexto, "Genero borrado", Toast.LENGTH_SHORT).show()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, lista_filtrada.size)
        }
    }
}
