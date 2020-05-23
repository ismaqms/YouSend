package com.example.yousend.views

import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.example.yousend.R
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import android.widget.EditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.FirebaseStorage
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_compartir_foto.*
import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class compartirFoto : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    var bitmap: Bitmap? = null
    private var mStorageRef: StorageReference? = null
    private var rutafoto: String? = null
    var urlDeLaFoto: String? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compartir_foto)

        cargarRecursos()


        //Toast.makeText(this, "llega: {$rutafoto}", Toast.LENGTH_SHORT).show()

        btnCompartir.setOnClickListener { subirImagen() }
    }

    private fun cargarRecursos() {
        mAuth = FirebaseAuth.getInstance()
        rutafoto = intent.getSerializableExtra("ruta") as String
        mStorageRef = FirebaseStorage.getInstance().getReference()
        val imageview = findViewById<ImageView>(R.id.post_image)
        bitmap = BitmapFactory.decodeFile(rutafoto)
        imageview.setImageBitmap(bitmap)
    }

    private fun subirImagen() {
        val file = Uri.fromFile(File(rutafoto))
        val sdf = SimpleDateFormat("dd-M-yyyy-hh:mm:ss")
        val currentDate = sdf.format(Date())
        val riversRef = mStorageRef!!.child("images/${file.lastPathSegment}")

        val uploadTask = riversRef.putFile(file)
        uploadTask.continueWith {
            if (!it.isSuccessful) {
                it.exception?.let { t ->
                    throw t
                }
            }
            riversRef.downloadUrl
        }.addOnCompleteListener {
            if (it.isSuccessful) {
                it.result!!.addOnSuccessListener { task ->
                    publicarPost(task.toString())
                }
            }
        }
    }

    fun publicarPost(rutaImagen: String) {

            // postejecute
            //Guardo datos de l post en firebase
            db = FirebaseFirestore.getInstance()
            //guardo los datos de usuario
            val post: MutableMap<Any, Any> = HashMap()
            post["Description"] = findViewById<EditText>(R.id.descripcion).text.toString()
            // post["postid"] = txtNombre.text.toString()
            post["rutaImagen"] = rutaImagen
            post["publisher"] = mAuth.currentUser!!.uid
            post["date"] = Timestamp(System.currentTimeMillis())
            post["likes"] = 0


            db.collection("publicaciones")
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot added with ID: " + documentReference.id


                    )
                    Toast.makeText(this, "Post subido", Toast.LENGTH_SHORT).show()
                    volverHome()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e) }
        }

    private fun volverHome() {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}


