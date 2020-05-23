package com.example.yousend.views.fragments.login

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.yousend.views.HomeActivity
import com.example.yousend.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_login.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Login.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Login.newInstance] factory method to
 * create an instance of this fragment.
 */
class Login : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view =  inflater.inflate(R.layout.fragment_login, container, false)
        val btncrearcuenta:Button = view.findViewById(R.id.btnCrearCuenta)
        btncrearcuenta.setOnClickListener{
            onButtonPressed(true)
        }

        val btnIniciar:Button = view.findViewById(R.id.btnIniciar)
        btnIniciar.setOnClickListener{
            comprobarDatos()
        }

        return view
    }

    private fun comprobarDatos() {

        if(txtEmailLogin.text.isNotEmpty() && txtContraseñaLogin.text.isNotEmpty()){
            //añado el @jaja.com porque quiero que se registre conusername y en firebase no hay opcion aun de username
            //conque me invento un dominio y solo cojo el username
            FirebaseAuth.getInstance().signInWithEmailAndPassword(txtEmailLogin.text.toString()+"@jaja.com",txtContraseñaLogin.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(getActivity(), HomeActivity::class.java)
                        //intent.putExtra("variable", valor)
                        startActivity(intent)

                        activity?.finish() //Para que no pueda volver atras una vez logeado

                    }else {
                        alerta("Usuario y contraseña erroneos")
                    }
                }
        } else {
            alerta("Introduce usuario y contraseña")
        }
    }

    private fun alerta(mensaje: String) {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar",null)
        builder.create().show()
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(pulsado: Boolean) {
        listener?.onFragmentInteraction(pulsado)
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
        fun onFragmentInteraction(pulsado: Boolean)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
