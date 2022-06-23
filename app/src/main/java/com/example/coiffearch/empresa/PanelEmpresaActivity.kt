package com.example.coiffearch.empresa

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.LoginActivity
import com.example.coiffearch.R
import com.example.coiffearch.adaptadorlocales.Local
import com.example.coiffearch.adaptadorlocales.RecyclerLocal
import com.example.coiffearch.databinding.ActivityPanelEmpresaBinding
import com.example.coiffearch.preferencias.CoiffearchApplication
import com.example.coiffearch.usuario.PanelUsuarioActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PanelEmpresaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanelEmpresaBinding

    companion object{
        lateinit var contexto: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanelEmpresaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contexto = applicationContext

        var bottomNavigationView : BottomNavigationView = binding.bottomNavegationEmpresa
        var navController: NavController = Navigation.findNavController(this, R.id.nav_host_empresa)

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

    }


}