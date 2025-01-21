package com.example.firabasecrud

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Switch
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerObras : AppCompatActivity() {

    private lateinit var volver: Button

    private lateinit var nombre : TextInputEditText
    private lateinit var rating : Switch
    private lateinit var recycler: RecyclerView
    private lateinit var lista:MutableList<Obra>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: ObraAdaptador


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_obras)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        nombre=findViewById(R.id.tietnombre)
        rating=findViewById(R.id.rating)

        recycler=findViewById(R.id.listaObras)
        volver=findViewById(R.id.volverInicio)

        db_ref= FirebaseDatabase.getInstance().reference
        lista= mutableListOf()
        db_ref.child("arte")
            .child("obras")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach{ hijo:DataSnapshot?->
                        val pojoObra=hijo?.getValue(Obra::class.java)
                        lista.add(pojoObra!!)
                    }
                    //Jugar con esto para demostrar que no es un codigo sincrono
                    recycler.adapter?.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        var listaAux = listOf<Obra>()
        nombre.doOnTextChanged{
            text, start, before, count ->
            listaAux = lista.filter { filtrar(it) }
            adaptador= ObraAdaptador(listaAux.toMutableList())
            recycler.adapter=adaptador
        }

        rating.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                listaAux = lista.sortedBy { it.estrellas }
                adaptador = ObraAdaptador(listaAux.reversed().toMutableList())
                recycler.adapter = adaptador
            }else{
                adaptador = ObraAdaptador(lista.toMutableList())
                recycler.adapter = adaptador
            }

        }

        adaptador= ObraAdaptador(lista)
        recycler.adapter=adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager= LinearLayoutManager(applicationContext)

        volver.setOnClickListener{
            finish()
        }

    }

    fun filtrar(obra : Obra) : Boolean{

        return obra.nombre!!.contains(nombre.text.toString())

    }
}