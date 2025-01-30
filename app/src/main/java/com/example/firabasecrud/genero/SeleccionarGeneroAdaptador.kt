package com.example.firabasecrud.genero

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.R
import com.example.firabasecrud.databinding.ItemGeneroBinding
import com.example.firabasecrud.databinding.ItemGeneroCheckboxBinding
import com.google.firebase.database.FirebaseDatabase
import io.appwrite.Client

class SeleccionarGeneroAdaptador(private val listaGenero: MutableList<Genero>) :
    RecyclerView.Adapter<SeleccionarGeneroAdaptador.SeleccionarGeneroViewHolder>() {

    private lateinit var contexto: Context
    private var lista_filtrada = listaGenero
    private lateinit var binding : ItemGeneroCheckboxBinding

    inner class SeleccionarGeneroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox = binding.itemGeneroCb
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeleccionarGeneroViewHolder {
        binding = ItemGeneroCheckboxBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val vista_item = LayoutInflater.from(parent.context).inflate(R.layout.item_genero_checkbox, parent, false)
        contexto = parent.context
        return SeleccionarGeneroViewHolder(vista_item)
    }

    override fun getItemCount() = lista_filtrada.size

    override fun onBindViewHolder(holder: SeleccionarGeneroViewHolder, position: Int) {
        val generoActual = lista_filtrada[position]
        holder.checkbox.text = generoActual.nombre

        holder.checkbox.isChecked = false

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            holder.checkbox.isChecked = isChecked
        }
    }

    fun clicarGenero(genero: Genero, position: Int) {
        listaGenero[position].checked = !listaGenero[position].checked
        // ???onGeneroChecked.onGeneroChecked(genero)
        notifyItemChanged(position)
    }


}
