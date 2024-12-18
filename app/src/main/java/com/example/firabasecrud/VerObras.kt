package com.example.firabasecrud

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VerObras : AppCompatActivity() {

    private lateinit var volver: Button

    private lateinit var recycler: RecyclerView
    private lateinit var lista:MutableList<Obra>
    private lateinit var db_ref: DatabaseReference
    private lateinit var adaptador: ObraAdaptador


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_clubs)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


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


        adaptador= ObraAdaptador(lista)
        recycler.adapter=adaptador
        recycler.setHasFixedSize(true)
        recycler.layoutManager= LinearLayoutManager(applicationContext)

        volver.setOnClickListener{
            finish()
        }

    }
}