package com.example.yousend.views.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yousend.R
import com.example.yousend.adapter.HomeAdapter
import com.example.yousend.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.retrofitcajonbindig.model.feedPost

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var rv: RecyclerView
    val follows = ArrayList<String>()
    val posts = ArrayList<feedPost>()
    private lateinit var root: View
    private lateinit var adapter: HomeAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_home, container, false)

        rv= root.findViewById(R.id.rvFeedPost)
        //Toast.makeText(context,"esto",Toast.LENGTH_SHORT).show()
        cargarRecursos()

        return root
    }

    private fun cargarRecursos() {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        initRV()
        usuariosQueSigo()

    }


    private fun initRV() {
        adapter = HomeAdapter(context!!, R.layout.rowhome)

        rv.addItemDecoration(
            DividerItemDecoration(
                context!!,
                DividerItemDecoration.VERTICAL
            )
        )

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context)

    }

    private fun usuariosQueSigo() {
            //Guardo las personas que me siguen
            db.collection("follows")
                .whereEqualTo("follow",mAuth.currentUser!!.uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        //Log.d("jaja", "${document.id} => ${document.data.getValue("following")}")
                        follows.add(document.data.getValue("following").toString())
                        //Toast.makeText(context,document.data.getValue("following").toString(),Toast.LENGTH_SHORT).show()
                    }
                    cargarPosts()
                }
        //Toast.makeText(context,follows.get(0),Toast.LENGTH_SHORT).show()

    }

    private fun cargarPosts() {

        for (i in follows) {
            db.collection("publicaciones")
                .whereEqualTo("publisher", i)
                .limit(10)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        posts.add(
                            feedPost(

                                document.id,
                                i,
                                document.data.getValue("Description").toString(),
                                document.data.getValue("rutaImagen").toString(),
                                "",
                                document.data.getValue("likes").toString(),
                                "",
                                document.data.getValue("date").toString()
                            )
                        )

                    }
                    cargarDatosUser()
                }
        }
    }

    private fun cargarDatosUser() {

        for (i in posts) {
            //Guardo las personas que me siguen
            db.collection("users")
                .whereEqualTo("uid", i.publisher).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        i.username = document.data.getValue("username").toString()
                        i.imagenPerfil = document.data.getValue("imgPerfil").toString()
                    }
                    //Ordeno el array por fecha
                    posts.sortByDescending{it.date}
                    adapter.setPost(posts)
                }
        }

    }



}