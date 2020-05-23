package com.example.yousend.adapter

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rowmisposts.view.*
import kotlinx.android.synthetic.main.rowsearch.view.*
import net.azarquiel.retrofitcajonbindig.model.persona
import net.azarquiel.retrofitcajonbindig.model.post


/**
 * Created by pacopulido on 9/10/18.
 */
class SearchAdapter(val context: Context,
                    val layout: Int,
                    val listener: OnClickListenerPersona) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    private var dataList: List<persona> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item,listener)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setPersona(personas: List<persona>) {
        this.dataList = personas
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {

        private lateinit var mAuth: FirebaseAuth
        private lateinit var db: FirebaseFirestore
        private var meSigue: Boolean = false

        fun bind(dataItem: persona, listener: OnClickListenerPersona){

            db = FirebaseFirestore.getInstance()
            mAuth = FirebaseAuth.getInstance()

            //itemView.tvTitloPelicula.text = dataItem.original_title
            if(dataItem.imagen != "") {
                Picasso.get().load(dataItem.imagen).resize(720, 720).centerCrop()
                    .into(itemView.ivFotoPerfil)
            }
                //Picasso.get().load("https://image.tmdb.org/t/p/w500${dataItem.poster_path}").into(itemView.ivMovie)
            //itemView.tvFecha.text = dataItem.release_date

            itemView.tag = dataItem
            itemView.tvUserName.text = dataItem.username
            itemView.tvNombre.text = dataItem.fullname
            mesigue(dataItem)

            itemView.setOnClickListener{
                listener.OnClickPerfil(dataItem)
            }

            itemView.btnFollow.setOnClickListener{
                seguir(dataItem)
            }
        }

        private fun seguir(persona: persona) {

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
                itemView.btnFollow.text = "Unfollow"
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
                itemView.btnFollow.text = "Seguir"
            }
        }

        private fun mesigue(persona: persona) {

            db.collection("follows")
                .whereEqualTo("following", persona.uid)
                .whereEqualTo("follow",mAuth.currentUser!!.uid).get()
                .addOnSuccessListener { documents ->

                    if(documents.isEmpty){
                        meSigue = false
                        itemView.btnFollow.text="Seguir"
                    } else {
                        meSigue= true
                        itemView.btnFollow.text = "Unfollow"
                    }
                }
        }

    }

    interface OnClickListenerPersona{
        fun OnClickPerfil(persona:persona):Boolean{
            return true
        }
    }
}