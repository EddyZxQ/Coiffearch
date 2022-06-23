package com.example.coiffearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coiffearch.databinding.ActivityEmpleadoLoginBinding
import com.example.coiffearch.empleados.EmpleadoActivity
import com.example.coiffearch.preferencias.CoiffearchApplication.Companion.prefs
import com.example.coiffearch.utiles.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EmpleadoLoginActivity : AppCompatActivity() {

    private lateinit var binding:ActivityEmpleadoLoginBinding
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmpleadoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnIniciarSesion.btnCargando.setOnClickListener {
            if (binding.campoEmail.text.toString().isNotEmpty() && binding.campoPass.text.toString().isNotEmpty()){
                login(binding.campoEmail.text.toString(), binding.campoPass.text.toString())
            }else{
                applicationContext.toast("Debes rellenar todos los campos")
            }
        }

        binding.btnIrLogin.setOnClickListener {
            startActivity(Intent(this@EmpleadoLoginActivity, LoginActivity::class.java))
            finish()
        }



    }

    private fun login(email: String, pass: String) {

        db.collection("usuarios")
            .whereEqualTo("tipoCuenta", 3).get().addOnSuccessListener { resultados ->

                for (doc in resultados){
                    if (doc.data["email"].toString() == email && doc.data["pass"].toString() == pass){
                        prefs.saveRecordarEmpleado(true)
                        prefs.saveUser(email)
                        prefs.savePass(pass)
                        startActivity(Intent(this@EmpleadoLoginActivity, EmpleadoActivity::class.java))
                        finish()

                    }else{
                        applicationContext.toast("Usuario o contraseña incorrectos")
                    }
                }

            }



    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@EmpleadoLoginActivity, LoginActivity::class.java))
        finish()
    }
}