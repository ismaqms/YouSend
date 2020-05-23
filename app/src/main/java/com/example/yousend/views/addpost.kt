package com.example.yousend.views

import android.net.Uri
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.yousend.R

import kotlinx.android.synthetic.main.activity_addpost.*
import org.jetbrains.anko.image

class addpost : AppCompatActivity(),Camara.OnFragmentInteractionListener{
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    val admin = supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addpost)
        mostrarcamara()

    }

    private fun mostrarcamara() {
        var fragmento = Camara.newInstance("r1","r2")
        var transaccion =admin.beginTransaction()
        transaccion.replace(R.id.frame_cam_o_galery,fragmento)
        transaccion.addToBackStack(null) //para el boton de retroceso
        transaccion.commit()
    }


}
