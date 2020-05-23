package com.example.yousend.adapter

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.yousend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rowhome.view.*
import kotlinx.android.synthetic.main.rowmisposts.view.*
import kotlinx.android.synthetic.main.rowsearch.view.*
import net.azarquiel.retrofitcajonbindig.model.feedPost
import net.azarquiel.retrofitcajonbindig.model.persona
import net.azarquiel.retrofitcajonbindig.model.post
import org.jetbrains.anko.image
import org.jetbrains.anko.imageResource
import java.sql.Timestamp


/**
 * Created by pacopulido on 9/10/18.
 */
class HomeAdapter(val context: Context,
                  val layout: Int
                    ) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    private var dataList: List<feedPost> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewlayout = layoutInflater.inflate(layout, parent, false)
        return ViewHolder(viewlayout, context)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    internal fun setPost(posts: List<feedPost>) {
        this.dataList = posts
        notifyDataSetChanged()
    }


    class ViewHolder(viewlayout: View, val context: Context) : RecyclerView.ViewHolder(viewlayout) {

        private lateinit var mAuth: FirebaseAuth
        private lateinit var db: FirebaseFirestore

        fun bind(dataItem: feedPost){

            db = FirebaseFirestore.getInstance()
            mAuth = FirebaseAuth.getInstance()


            if(dataItem.imagenPerfil != "") {
                Picasso.get().load(dataItem.imagenPerfil)
                    .resize(720, 720).centerCrop().into(itemView.ivPerfilH)
            }
            Picasso.get().load(dataItem.imagenPost).resize(720, 720).centerCrop().into(itemView.ivFotoHome)
            itemView.tvUserHome.text = dataItem.username
            itemView.tvLikes.text = dataItem.likes
            itemView.tvDescripcionHome.text = dataItem.descripcion

            itemView.tag = dataItem

            itemView.ivLike.setOnClickListener{
                like(dataItem)
            }

        }

        private fun like(post: feedPost) {

            //mirar si yo le he dado like
            db.collection("likes")
                .whereEqualTo("quiendaLike", mAuth.currentUser!!.uid)
                .whereEqualTo("post", post.postdocument)
                .get()
                .addOnSuccessListener { documents ->
                    Toast.makeText(context,"entra".toString(),Toast.LENGTH_SHORT).show()
                    if(!documents.isEmpty){
                        //ha dado like
                        //si le he dado like antes, esque ahora le quito el like
                        //Actualizo el like en la bbdd
                             post.likes = (post.likes.toInt() -1).toString()
                             db.collection("publicaciones").document(post.postdocument).update("likes", post.likes.toInt())
                            //Cambio imagen y resto like al post
                             itemView.ivLike.setImageResource(R.drawable.like)
                        //boro el documento del like
                            for (document in documents){
                                db.collection("likes").document(document.id)
                                    .delete()
                            }

                    } else {
                        // no ha dado like
                        //si no le he dado like antes, esque le doy like

                        //cambio imagen y sumo like al post
                        post.likes = (post.likes.toInt()+1).toString()
                        db.collection("publicaciones").document(post.postdocument).update("likes", post.likes.toInt())

                        //Actualizo like en la bbd

                        itemView.ivLike.setImageResource(R.drawable.likeon)

                        //Creo un nuevo documento con mi like
                        val like: MutableMap<Any, Any> = HashMap()
                        like["quiendaLike"] = mAuth.currentUser!!.uid
                        like["post"] = post.postdocument

                        db.collection("likes")
                            .add(like)

                    }

                    itemView.tvLikes.text = post.likes

                }.addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }


        }

    }
}