package com.example.coiffearch.usuario

import android.app.*
import android.app.Notification
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController

import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.bumptech.glide.Glide
import com.example.coiffearch.*
import com.example.coiffearch.databinding.ActivityPanelUsuarioBinding
import com.example.coiffearch.preferencias.CoiffearchApplication
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class   PanelUsuarioActivity : AppCompatActivity() {
    private lateinit var  binding: ActivityPanelUsuarioBinding


    companion object{
        lateinit var contexto: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelUsuarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contexto = applicationContext

       var bottomNavigationView : BottomNavigationView = binding.bottomNavegationUser
       var navController: NavController = Navigation.findNavController(this, R.id.nav_host_user)


        if (intent.getStringExtra("noti") != null) {
            if (intent.getStringExtra("noti") == "1"){
                navController.navigate(R.id.panelcitasFragment)
            }
        }


        NavigationUI.setupWithNavController(bottomNavigationView, navController)

    }

}