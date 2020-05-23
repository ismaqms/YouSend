package com.example.yousend.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.yousend.R
import com.example.yousend.views.fragments.search.fragments.profileSFragment
import com.example.yousend.views.fragments.search.fragments.searchyrv


class HomeActivity : AppCompatActivity(), searchyrv.OnFragmentInteractionListener, profileSFragment.OnFragmentInteractionListener {

    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        //Esto es para la splashScreen
        Thread.sleep(2000)
        setTheme(R.style.AppTheme)
        //

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menuacciones,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Boton presionado del menu
        val id= item.itemId
        if(id == R.id.addPost){
            Toast.makeText(applicationContext,"añadir post",Toast.LENGTH_SHORT).show()
            val intent = Intent(this, addpost::class.java)
            startActivity(intent)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun selectImage(view: View) {
        val intent = Intent(this, AddfotoPerfil::class.java)
        startActivity(intent)

    }

    fun editarPerfil(view: View) {
        val intent = Intent(this, EditProfile::class.java)
        startActivity(intent)
    }


}
