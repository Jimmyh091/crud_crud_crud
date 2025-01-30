package com.example.firabasecrud.genero

import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firabasecrud.R
import com.example.firabasecrud.obras.Obra
import com.example.firabasecrud.obras.ObraAdaptador
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// creo que no deberia borrar esta clase
class VerGeneros : AppCompatActivity() {
    private lateinit var volver: Button

    private lateinit var nombre: TextInputEditText
    private lateinit var recycler: RecyclerView
    private lateinit var lista: MutableList<Genero>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: GeneroAdaptador


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_generos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombre = findViewById(R.id.tietnombre)

        recycler = findViewById(R.id.listaGeneros)
        volver = findViewById(R.id.volverInicio)

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
            if (rating.isChecked) {
                listaAux = lista.filter { filtrar(it) }.sortedByDescending { it.nombre }
            } else {
                listaAux = lista.filter { filtrar(it) }
            }

            adaptador = GeneroAdaptador(listaAux.toMutableList())
            recycler.adapter = adaptador
        }

        rating.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                listaAux = lista.filter { filtrar(it) }.sortedBy { it.nombre }
                adaptador = GeneroAdaptador(listaAux.reversed().toMutableList())
                recycler.adapter = adaptador
            } else {
                adaptador = GeneroAdaptador(lista.toMutableList())
                recycler.adapter = adaptador
            }
        }

        adaptador = GeneroAdaptador(lista)
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
}
}