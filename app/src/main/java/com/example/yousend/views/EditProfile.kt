package com.example.yousend.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.yousend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfile : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var mStorageRef: StorageReference? = null
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        cargarRecursos()


    }

    private fun cargarRecursos() {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        //rutafoto = intent.getSerializableExtra("ruta") as String
        mStorageRef = FirebaseStorage.getInstance().getReference()
    }

    fun guardar(view: View) {
        db.collection("users")
            .whereEqualTo("uid", mAuth.currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    db.collection("users").document(document.id).update("nombre", name_input.text.toString())
                    db.collection("users").document(document.id).update("biografia", edBiografia.text.toString())
                    super.onBackPressed()
                }
            }

    }

}
