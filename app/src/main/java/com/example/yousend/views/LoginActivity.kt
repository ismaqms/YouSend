package com.example.yousend.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.yousend.R
import com.example.yousend.views.fragments.login.Login
import com.example.yousend.views.fragments.login.Registro

class LoginActivity : AppCompatActivity(), Registro.OnFragmentInteractionListener, Login.OnFragmentInteractionListener {
    override fun onFragmentInteraction(pulsado: Boolean) {

        //Lo que devuelve el fragmento al pulsar el boton
        if (pulsado) crearCuenta()
    }

    override fun onFragmentInteractionRegistro(pulsado: Boolean) {

    }

    val admin = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        //Esto es para la splashScreen
        Thread.sleep(2000)
        setTheme(R.style.AppTheme)
        //
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //btnCrearCuenta.setOnClickListener { onClickCrearCuenta() }
        mostrarlogin()

    }


    private fun mostrarlogin() {
        var fragmento = Login.newInstance("r1","r2")
        var transaccion =admin.beginTransaction()
        transaccion.replace(R.id.LinearLogin,fragmento)
        transaccion.addToBackStack(null) //para el boton de retroceso
        transaccion.commit()
    }

    private fun crearCuenta(){
        var fragmento = Registro.newInstance("r1","r2")
        var transaccion =admin.beginTransaction()
        transaccion.replace(R.id.LinearLogin,fragmento)
        transaccion.addToBackStack(null) //para el boton de retroceso
        transaccion.commit()
    }


}
