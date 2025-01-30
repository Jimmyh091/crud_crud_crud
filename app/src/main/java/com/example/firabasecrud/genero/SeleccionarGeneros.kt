package com.example.firabasecrud.genero

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.R
import com.example.firabasecrud.databinding.ActivitySeleccionarGenerosBinding
import com.example.firabasecrud.databinding.ItemGeneroCheckboxBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

/*class SeleccionarGeneros : AppCompatActivity(), OnGeneroClickedListener, OnGeneroChecked {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_seleccionar_generos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onGeneroClicked(genero: Genero) {
        TODO("Not yet implemented")
    }

    override fun onGeneroChecked(genero: Genero) {
        TODO("Not yet implemented")
    }
}*/

class SeleccionarGeneros : AppCompatActivity(){
    private lateinit var volver: Button

    private lateinit var nombre: TextInputEditText
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Genero>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: SeleccionarGeneroAdaptador

    private lateinit var binding: ActivitySeleccionarGenerosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySeleccionarGenerosBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombre = binding.tietnombre

        recycler = binding.listaGeneros
        volver = binding.volverInicio

        db_ref = FirebaseDatabase.getInstance().reference
        lista = mutableListOf()

        db_ref.child("arte")
            .child("generos")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach { hijo: DataSnapshot? ->
                        val pojoGenero = hijo?.getValue(Genero::class.java)
                        lista.add(pojoGenero!!)
                    }
                    // Notificar que los datos han cambiado
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        var listaAux = listOf<Genero>()

        nombre.doOnTextChanged { text, start, before, count ->

            listaAux = lista.filter { filtrar(it) }

            adaptador = SeleccionarGeneroAdaptador(listaAux.toMutableList())
            recycler.adapter = adaptador
        }

        adaptador = SeleccionarGeneroAdaptador(lista)
        recycler.adapter = adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(applicationContext)

        volver.setOnClickListener {
            finish()
        }
    }

    fun filtrar(genero: Genero): Boolean {
        return genero.nombre!!.contains(nombre.text.toString())
    }

    fun onGeneroClicked(genero: Genero) {
        TODO("Not yet implemented")
    }

    fun onGeneroChecked(genero: Genero) {
        TODO("Not yet implemented")
    }
}