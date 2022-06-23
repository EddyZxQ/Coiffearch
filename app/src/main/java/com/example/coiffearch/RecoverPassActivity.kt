package com.example.coiffearch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.coiffearch.databinding.ActivityRecoverPassBinding
import com.example.coiffearch.utiles.ProgressBarBtn
import com.example.coiffearch.utiles.toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RecoverPassActivity : AppCompatActivity() {

    private lateinit var  pb: ProgressBarBtn
    private lateinit var binding: ActivityRecoverPassBinding
    private lateinit var btnRecuperarCuenta: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecoverPassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnRecuperarCuenta = findViewById(R.id.btnRecuperarCuenta)
        pb = ProgressBarBtn(this,btnRecuperarCuenta)
        pb.texto("Enviar")


        binding.btnRecuperarCuenta.btnCargando.setOnClickListener { emailRecuperacion() }

        binding.btnVolverLogin.setOnClickListener { irLogin() }

    }

    private fun emailRecuperacion() {
        pb.cargar()
        val emailAddress = binding.campoEmail.text.toString()

        if (emailAddress.isNotEmpty()){
            Firebase.auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        pb.cancel()
                        applicationContext.toast("Hemos enviado a tu email un link para que puedas modificar los datos de tu cuenta.")
                        startActivity(Intent(this@RecoverPassActivity , LoginActivity::class.java))
                    } else{
                        pb.cancel()
                        applicationContext.toast("Error el correo introducido no se halla autenticado en nuestro sistema")
                    }
                }
        } else{
            pb.cancel()
            applicationContext.toast("Debes rellenar el campo con un correo que hallas este registrado en el sistema")
        }

    }

    private fun irLogin() {
        startActivity(Intent(this@RecoverPassActivity, LoginActivity::class.java))
        finish()
    }
}