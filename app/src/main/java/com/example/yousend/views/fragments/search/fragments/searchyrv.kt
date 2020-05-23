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
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.yousend.R
import com.example.yousend.adapter.SearchAdapter
import com.google.firebase.firestore.FirebaseFirestore
import net.azarquiel.retrofitcajonbindig.model.persona

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [searchyrecyclerFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [searchyrecyclerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class searchyrv : Fragment(), SearchView.OnQueryTextListener, SearchAdapter.OnClickListenerPersona {

    private lateinit var root: View
    private lateinit var adapter: SearchAdapter
    private lateinit var rv: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var searchView: SearchView
    private var param1: String? = null
    private var param2: String? = null
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var listenerAdapter: SearchAdapter.OnClickListenerPersona

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onQueryTextChange(query: String): Boolean {
        //val original = ArrayList<persona>(personas)
        // adapter.setPersona(original.filter { persona -> persona.username.contains(query) })
        val usuarios = ArrayList<persona>()
        db.collection("users")
            .orderBy("username")
            .startAt(query)
            .endAt(query+"\uf8ff")
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    //Log.d("MisImagenes", "${document.id} => ${document.data.getValue("Description")}")
                    usuarios.add(
                        persona(
                            document.data.getValue("username").toString(),
                            document.data.getValue("imgPerfil").toString(),
                            document.data.getValue("nombre").toString(),
                            document.data.getValue("biografia").toString(),
                            document.data.getValue("uid").toString()
                        )
                    )
                }

                adapter.setPersona(usuarios)

            }.addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }

        return false
    }

    override fun onQueryTextSubmit(text: String): Boolean {
        return false
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_searchyrecycler, container, false)

        initRV()
        cargarSearch()
        return root
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment searchyrecyclerFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            searchyrv().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun cargarSearch() {
        db = FirebaseFirestore.getInstance()
        searchView = root.findViewById(R.id.svusers)
        searchView.setQueryHint("Search...")
        searchView.setOnQueryTextListener(this)
    }

    private fun initRV() {
        rv= root.findViewById(R.id.rvsearch)
        adapter = SearchAdapter(context!!, R.layout.rowsearch,this)

        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(context!!)

    }

    override fun OnClickPerfil(persona:persona):Boolean{
        //cambio el fragmento del LinearLayout del SearchAdapter
        var fragmento = profileSFragment()
        val datos = Bundle()
        datos.putSerializable("key", persona)
        fragmento.setArguments(datos)
        var transaccion = parentFragmentManager.beginTransaction()
        transaccion.replace(R.id.contsearch,fragmento)
        transaccion.commit()
        return true
    }




}
