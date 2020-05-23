package com.example.yousend.views.fragments.search.fragments

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.yousend.R
import com.example.yousend.adapter.CustomAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import net.azarquiel.retrofitcajonbindig.model.persona
import net.azarquiel.retrofitcajonbindig.model.post

class profileSFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private var meSigue: Boolean = false
    private lateinit var btnseguir: Button
    private lateinit var arrayPosts: List<post>
    val aux = ArrayList<post>()
    private lateinit var root: View
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: CustomAdapter
    private lateinit var rv: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var mAuth: FirebaseAuth
    private lateinit var persona: persona
    private var posts = 0
    private var follows = 0
    private var following = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_profile_search, container, false)

        val datos = arguments
        persona = datos!!.getSerializable("key") as persona
        cargarRecuersos()
        rv= root.findViewById(R.id.rvPostsS)
        initRV()

        return root
    }

    private fun cargarRecuersos() {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        root.findViewById<TextView>(R.id.tvUserNameS).text = persona.username
        root.findViewById<TextView>(R.id.txtBiografia).text = persona.biografia
        cargarcifras()
        cargarPerfil()
        cargarPosts()
        btnseguir =root.findViewById(R.id.btnSeguirProfile)
        btnseguir.setOnClickListener{
            FollowUnfollow()
        }

        //Compruebo si me sigue
         db.collection("follows")
            .whereEqualTo("following", persona.uid)
            .whereEqualTo("follow",mAuth.currentUser!!.uid).get()
             .addOnSuccessListener { documents ->

                 if(documents.isEmpty){
                     meSigue = false
                     Toast.makeText(context,meSigue.toString(),Toast.LENGTH_SHORT).show()
                     btnseguir.text="Seguir"
                 } else {
                     meSigue= true
                     Toast.makeText(context,meSigue.toString(),Toast.LENGTH_SHORT).show()
                     btnseguir.text="Dejar de seguir"
                 }
             }
    }

    private fun FollowUnfollow() {

        //Si no me sigue esque me hace follow
        if (!meSigue) {
            //guardo los datos de usuario
            val follow: MutableMap<String, Any> = HashMap()
            follow["following"] = persona.uid // a quien sigues
            follow["follow"] = mAuth.currentUser!!.uid //quien sigue

            db.collection("follows")
                .add(follow)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot added with ID: " + documentReference.id
                    )
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }

            meSigue = true
            btnseguir.text = "Dejar de Seguir"
        } else {
            // si me sigue me hace unfollow
            //Borro el docuemnto


            db.collection("follows")
                .whereEqualTo("following", persona.uid)
                .whereEqualTo("follow",mAuth.currentUser!!.uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        db.collection("follows").document(document.id).delete()
                    }
                }

            meSigue = false
            btnseguir.text = "Seguir"
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    private fun cargarPosts() {

        db.collection("publicaciones")
            .whereEqualTo("publisher", persona.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    //Log.d("MisImagenes", "${document.id} => ${document.data.getValue("Description")}")
                    aux.add(
                        post(
                            document.data.getValue("publisher").toString(),
                            document.data.getValue("Description").toString(),
                            document.data.getValue("rutaImagen").toString())
                    )
                }
                arrayPosts = aux
                adapter.setPost(arrayPosts)

            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun initRV() {
        adapter = CustomAdapter(context!!, R.layout.rowmisposts)

        rv.addItemDecoration(
            DividerItemDecoration(
                context!!,
                DividerItemDecoration.HORIZONTAL
            )
        )

        rv.adapter = adapter
        rv.layoutManager = GridLayoutManager(context,3)

    }

    private fun cargarPerfil() {
        //Picasso.get().load(dataItem.imagen).resize(720, 720).centerCrop().into(root.findViewById<ImageView>(R.id.ivFPerfil))
        db.collection("users")
            .whereEqualTo("uid", mAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    root.findViewById<TextView>(R.id.tvUserNameS).text = persona.username
                    Picasso.get().load(persona.imagen).resize(720, 720).centerCrop().into(root.findViewById<ImageView>(R.id.ivPerfilS))
                    root.findViewById<TextView>(R.id.txtBiografia).text = persona.biografia
                }

            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

    }

    private fun cargarcifras() {
        //cargar cuantos posts tengo

        db.collection("publicaciones")
            .whereEqualTo("publisher", persona.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    posts += 1

                }
                root.findViewById<TextView>(R.id.txtPosts).text = posts.toString()


            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


        //cargar cuanta gente me sigue

        db.collection("follows")
            .whereEqualTo("follow", persona.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    follows += 1
                }
                root.findViewById<TextView>(R.id.txtFollowing).text = follows.toString()

            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


        //cargar a cuantos sigo

        db.collection("follows")
            .whereEqualTo("following", persona.uid)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    following += 1
                }
                root.findViewById<TextView>(R.id.txtFollowers).text = following.toString()

            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }


    }


}
