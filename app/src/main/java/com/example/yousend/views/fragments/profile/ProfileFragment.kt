package com.example.yousend.views.fragments.profile


import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.azarquiel.retrofitcajonbindig.model.post
import com.example.yousend.R
import com.example.yousend.adapter.CustomAdapter
import com.example.yousend.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class ProfileFragment : Fragment() {

    private lateinit var root: View
    private lateinit var arrayPosts: List<post>

    val aux = ArrayList<post>()
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var adapter: CustomAdapter
    private lateinit var rv: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private var posts = 0
    private var follows = 0
    private var following = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {

        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        root = inflater.inflate(R.layout.profile_fragment, container, false)

        //en los fragmentos el recycylerview hay que buscarlo con el finviewbyid sino da nullpointer
        cargarRecursos()
        rv= root.findViewById(R.id.rvPosts)
        initRV()
        cargarcifras()
        cargarPerfil()
        cargarMisPosts()

        return root
    }

    private fun cargarcifras() {
        //cargar cuantos posts tengo

        db.collection("publicaciones")
            .whereEqualTo("publisher", mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    posts += 1
                }
                root.findViewById<TextView>(R.id.txtPosts).text = posts.toString()

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        //cargar cuanta gente me sigue

        db.collection("follows")
            .whereEqualTo("follow", mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    follows += 1

                }
                root.findViewById<TextView>(R.id.txtFollowing).text = follows.toString()

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


        //cargar a cuantos sigo

        db.collection("follows")
            .whereEqualTo("following", mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    following += 1

                }
                root.findViewById<TextView>(R.id.txtFollowers).text = following.toString()

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }


    }


    private fun cargarRecursos() {

        rv= root.findViewById(R.id.rvPosts)

        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()

    }

    private fun cargarPerfil() {
        //Picasso.get().load(dataItem.imagen).resize(720, 720).centerCrop().into(root.findViewById<ImageView>(R.id.ivFPerfil))
        db.collection("users")
            .whereEqualTo("uid", mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    root.findViewById<TextView>(R.id.tvUserName).text = document.data.getValue("username").toString()
                    root.findViewById<TextView>(R.id.tvUserName2).text = document.data.getValue("nombre").toString()
                    Picasso.get().load(document.data.getValue("imgPerfil").toString()).resize(720, 720).centerCrop().into(root.findViewById<ImageView>(R.id.ivFPerfil))
                    root.findViewById<TextView>(R.id.txtBiografia).text = document.data.getValue("biografia").toString()
                }

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }

    private fun cargarMisPosts() {

        //hago query del user actual

        db.collection("publicaciones")
            .whereEqualTo("publisher", mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    //Log.d("MisImagenes", "${document.id} => ${document.data.getValue("Description")}")
                    aux.add(post(
                        document.data.getValue("publisher").toString(),
                        document.data.getValue("Description").toString(),
                        document.data.getValue("rutaImagen").toString()))
                }
                arrayPosts = aux
                adapter.setPost(arrayPosts)

            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun initRV() {
        adapter = CustomAdapter(context!!, R.layout.rowmisposts)
        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(context,3)

    }


}
