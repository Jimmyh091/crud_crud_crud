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

class GeneroAdaptador(private val listaGenero: MutableList<Genero>) :
    RecyclerView.Adapter<GeneroAdaptador.GeneroViewHolder>() {

    private lateinit var contexto: Context
    private var lista_filtrada = listaGenero

    inner class GeneroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val miniatura: ImageView = itemView.findViewById(R.id.item_miniatura)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val borrar: ImageView = itemView.findViewById(R.id.item_borrar)
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

            lista_filtrada.removeAt(position)
            Toast.makeText(contexto, "Genero borrado", Toast.LENGTH_SHORT).show()
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, lista_filtrada.size)
        }
    }
}
