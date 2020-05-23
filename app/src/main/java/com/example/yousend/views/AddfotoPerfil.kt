package com.example.yousend.views

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.yousend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_addfoto_perfil.*
import java.io.ByteArrayOutputStream
import java.io.IOException

class AddfotoPerfil : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var mStorageRef: StorageReference? = null
    private lateinit var mAuth: FirebaseAuth

    companion object {
        const val REQUEST_PERMISSION = 200
        const val REQUEST_GALLERY = 1
    }

    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addfoto_perfil)

        checkPermiss()
        cargarRecursos()

        btnPerfil.setOnClickListener { photoFromGallary() }
    }


    private fun photoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_GALLERY)
    }


    @Suppress("DEPRECATION")
    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val contentURI = data!!.data
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                    subirImagen(bitmap)
                    iv.setImageBitmap(bitmap)

                } catch (e: IOException) {
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Camera permiss
    private fun checkPermiss() {
        if (    ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                REQUEST_PERMISSION
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No tienes los permisos suficientes...", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun subirImagen(bitmap: Bitmap)  {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)

        val data = baos.toByteArray()

        val riversRef = mStorageRef!!.child("fotosPerfil/${mAuth.currentUser!!.uid}")

        val uploadTask = riversRef.putBytes(data)
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

                    //Actualizo el campo de imgPerfil con la ruta de la foto de perfil
                    db.collection("users")
                        .whereEqualTo("uid", mAuth.currentUser!!.uid).get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                Toast.makeText(this@AddfotoPerfil, document.id, Toast.LENGTH_SHORT).show()
                                db.collection("users").document(document.id).update("imgPerfil", task.toString())
                            }
                        }

                    volverHome()
                }
            }
        }

    }

    private fun cargarRecursos() {
        db = FirebaseFirestore.getInstance()
        mAuth = FirebaseAuth.getInstance()
        //rutafoto = intent.getSerializableExtra("ruta") as String
        mStorageRef = FirebaseStorage.getInstance().getReference()
    }


    private fun volverHome() {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }
}
