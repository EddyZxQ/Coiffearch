package com.example.coiffearch

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.coiffearch.databinding.ActivityLoginBinding
import com.example.coiffearch.empleados.EmpleadoActivity
import com.example.coiffearch.empresa.PanelEmpresaActivity
import com.example.coiffearch.preferencias.CoiffearchApplication.Companion.prefs
import com.example.coiffearch.usuario.PanelUsuarioActivity
import com.example.coiffearch.utiles.ProgressBarBtn
import com.example.coiffearch.utiles.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var  sharedPreferences: SharedPreferences
    var recuerdame = false

    private lateinit var btnIniciarSesion: View
    private lateinit var  pb: ProgressBarBtn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        btnIniciarSesion = findViewById(R.id.btnIniciarSesion)
        pb = ProgressBarBtn(this, btnIniciarSesion)
        pb.texto("Iniciar Sesion")

        auth = Firebase.auth

        comprobarSiEsEmpleado()
        comprobarRecordar()

        binding.btnIniciarSesion.btnCargando.setOnClickListener {
            iniciarSesion(binding.campoEmail.text.toString().trim(), binding.campoPass.text.toString())
        }


        binding.noTengoCuenta.setOnClickListener {
            startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
            finish()
        }

        binding.btnOlvidadoPass.setOnClickListener { irPanelRecuperarCuenta() }

        binding.btnIrLoginEmpleado.setOnClickListener {  irLoginEmpleado()}
    }

    private fun comprobarRecordar(){
        if (prefs.getRecordar()) lastUsuarioAutoAuth()
    }


    private fun comprobarSiEsEmpleado(){

        if (prefs.getRecordarEmpleado()){
            startActivity(Intent(this@LoginActivity, EmpleadoActivity::class.java))
            finish()
        }

    }

    private fun lastUsuarioAutoAuth() {
        val currentUser = auth.currentUser

        if(currentUser != null) {
            pb.cargar()

            binding.campoEmail.setText(prefs.getUser())
            binding.campoPass.setText(prefs.getPass())
            binding.checkRecordarme.isChecked = true

            db.collection("usuarios").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                if (it["tipoCuenta"].toString() == "0"){
                    irPanelUsuario()
                }else if(it["tipoCuenta"].toString() == "1"){
                    irPanelEmpresa()
                }
            }


        }







    }

    private fun irLoginEmpleado() {
        startActivity(Intent(this@LoginActivity, EmpleadoLoginActivity::class.java))
        finish()
    }

    private fun irPanelRecuperarCuenta() {
        startActivity(Intent(this@LoginActivity, RecoverPassActivity::class.java))
        finish()
    }

    private fun irPanelUsuario(){
        startActivity(Intent(this@LoginActivity, PanelUsuarioActivity::class.java))
        finish()
    }

    private fun irPanelEmpresa(){
        startActivity(Intent(this@LoginActivity, PanelEmpresaActivity::class.java))
        finish()
    }


    private fun iniciarSesion(email:String, password:String){
        pb.cargar()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //SHARED PREFERENCE
                        prefs.saveRecordar(binding.checkRecordarme.isChecked)
                        prefs.saveUser(email)
                        prefs.savePass(password)

                        //LOGICA BBDD
                        db.collection("usuarios").get().addOnSuccessListener { resultado ->
                            for (documento in resultado){
                                if (email == documento.data["email"].toString()){
                                    if (documento.data["tipoCuenta"].toString() == "0"){
                                        irPanelUsuario()
                                    }else if (documento.data["tipoCuenta"].toString() == "1"){
                                        irPanelEmpresa()
                                    }else{
                                        pb.cancel()
                                    }
                                }

                            }
                        }

                        //Fin
                    } else {
                        pb.cancel()
                        this.toast("Error email o contraseña incorrectos")
                    }
                }
        }else{
            pb.cancel()
            this.toast("Debes rellenar todos los campos")
        }



    }








}