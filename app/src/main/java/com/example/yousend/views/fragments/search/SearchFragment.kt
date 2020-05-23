package com.example.yousend.views.fragments.search

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.yousend.R
import android.view.LayoutInflater
import com.example.yousend.views.fragments.search.fragments.searchyrv


class SearchFragment : Fragment() {

    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_search, container, false)
        mostrarsearch()

        return root
    }

    private fun mostrarsearch() {

        var fragmento = searchyrv.newInstance("r1","r2")
        var transaccion =childFragmentManager.beginTransaction()
        transaccion.replace(R.id.contsearch,fragmento)
        transaccion.commit()
    }



}

