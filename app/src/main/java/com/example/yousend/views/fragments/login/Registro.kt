package com.example.yousend.views.fragments.login

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.yousend.views.HomeActivity
import com.example.yousend.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_registro.*



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Registro : Fragment(){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

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
        var view = inflater.inflate(R.layout.fragment_registro, container, false)

        val btnRegistrar:Button = view.findViewById(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener{
            comprobarDatos()
        }
        return view
    }

    private fun comprobarDatos() {

        mAuth = FirebaseAuth.getInstance()

        if(txtEmail.text.isNotEmpty() && txtPassword.text.isNotEmpty()){
            //añado el jaja.com porque yo quiero que se loge con username y no con email, conque se registre con username
            // pero yo lo gardo como email
            mAuth.createUserWithEmailAndPassword(tvUserName.text.toString()+"@jaja.com",txtPassword.text.toString())
                .addOnCompleteListener {
                    if(it.isSuccessful){

                        //Guardo datos de usuario en firestore
                        db = FirebaseFirestore.getInstance()
                        //guardo los datos de usuario
                        val user: MutableMap<String, Any> = HashMap()
                        user["email"] = txtEmail.text.toString()
                        user["uid"] = mAuth.currentUser!!.uid
                        user["nombre"] = txtNombre.text.toString()
                        user["apellidos"] = textApellido.text.toString()
                        user["username"] = tvUserName.text.toString()
                        user["biografia"] = ""
                        user["imgPerfil"] = ""

                        db.collection("users")
                            .add(user)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    TAG,
                                    "DocumentSnapshot added with ID: " + documentReference.id
                                )
                            }
                            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e) }


                        val intent = Intent(getActivity(), HomeActivity::class.java)
                        //intent.putExtra("variable", valor)
                        startActivity(intent)
                        activity?.finish() //Para que no pueda volver atras una vez logeado
                    }else {
                        //mostrar alerta, en el video de autenticacion se ve mejor
                        alerta("Introduce un correo y una contraseña con minimo 6 caracteres")
                    }
                }
        } else {
            alerta("Introduce un usuario y contraseña")
        }
    }

    private fun alerta(mensaje: String) {
        val builder = AlertDialog.Builder(getActivity())
        builder.setTitle("Error")
        builder.setMessage(mensaje)
        builder.setPositiveButton("Aceptar",null)
        builder.create().show()
    }


    fun onButtonPressed(pulsado: Boolean) {
        listener?.onFragmentInteractionRegistro(pulsado)
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
        fun onFragmentInteractionRegistro(pulsado: Boolean)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Registro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Registro().apply {

            }
    }
}
