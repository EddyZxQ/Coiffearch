package com.example.coiffearch

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.coiffearch.databinding.ActivityRegisterBinding
import com.example.coiffearch.utiles.ProgressBarBtn
import com.example.coiffearch.utiles.isValidEmail
import com.example.coiffearch.utiles.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var auth: FirebaseAuth

    private val db = Firebase.firestore


    lateinit var campoEmail:String
    lateinit var campoPass:String
    lateinit var campoRepitePass:String
    lateinit var campoNombre:String
    lateinit var campoApellidos:String
    lateinit var campoUsuario:String
    var campotipoCuenta:Int = -1
    lateinit var storage: FirebaseStorage

    private  lateinit var btnRegistrarUsuario: View
    private lateinit var  pb: ProgressBarBtn

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.supportActionBar?.hide()
        binding.btnRegistrarse.btnCargando.isEnabled = false


        binding.checkTerminosCondiciones.setOnCheckedChangeListener { btn, b ->
            comprobarSiAceptarLosTerminos()
        }



        auth = Firebase.auth
        storage = Firebase.storage

        btnRegistrarUsuario = findViewById(R.id.btnRegistrarse)
        pb = ProgressBarBtn(this, btnRegistrarUsuario)
        pb.texto("Registrar")

        binding.btnRegistrarse.btnCargando.setOnClickListener {

            campoEmail =binding.camporREmail.text.toString()
            campoPass = binding.campoRPass.text.toString()
            campoRepitePass = binding.campoRRepitePass.text.toString()
            campoNombre = binding.campoRNombre.text.toString()
            campoUsuario = binding.campoRUsuario.text.toString()
            campoApellidos = binding.campoRApellidos.text.toString()

            if (campoEmail.isNotEmpty() && campoApellidos.isNotEmpty() &&
                campoNombre.isNotEmpty() && campoUsuario.isNotEmpty() &&
                campoPass.isNotEmpty() && campoRepitePass.isNotEmpty() &&
                (binding.radioEmpresa.isChecked || binding.radioUsuario.isChecked)){

                if (campoEmail.isValidEmail()){

                    if (campoPass.length in 6..16){
                        if (campoPass == campoRepitePass){

                            registrar(campoEmail,campoPass)

                        }else{
                            pb.cancel()
                            Toast.makeText(this,"Las contrase??as no coninciden",Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        pb.cancel()
                        Toast.makeText(this,"La contrase??a debe tener entre 6 y 16 caracteres",Toast.LENGTH_SHORT).show()
                    }

                }else{
                    pb.cancel()
                    Toast.makeText(this,"Debes introducir un email valido",Toast.LENGTH_SHORT).show()
                }

            }else{
                pb.cancel()
                Toast.makeText(this,"Debes rellenar todos los campos",Toast.LENGTH_SHORT).show()
            }

        }


        binding.tengoCuenta.setOnClickListener {
            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
            finish()
        }


    }

    private fun comprobarSiAceptarLosTerminos() {
        if (binding.checkTerminosCondiciones.isChecked){
            binding.btnRegistrarse.btnCargando.isEnabled = true
            terminosycondiciones()
        }else{
            binding.btnRegistrarse.btnCargando.isEnabled = false
            applicationContext.toast("Debes aceptar los terminos y condiciones de uso si quieres registrarte en la app")
        }
    }

    private fun terminosycondiciones(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Terminos y condiciones").setMessage("""
            La presente Pol??tica de Privacidad establece los t??rminos en los que 
            CoiffEarch usa y protege la informaci??n que es proporcionada por sus 
            usuarios al momento de utilizar su aplicacion. Esta compa????a est?? 
            comprometida con la seguridad de los datos de sus usuarios. Cuando le 
            pedimos llenar los campos de informaci??n personal con la cual usted 
            pueda ser identificado, lo hacemos asegurando que s??lo se emplear?? de 
            acuerdo con los t??rminos de este documento. Sin embargo esta Pol??tica de 
            Privacidad puede cambiar con el tiempo o ser actualizada por lo que le 
            recomendamos y enfatizamos revisar continuamente esta p??gina para 
            asegurarse que est?? de acuerdo con dichos cambios.

            Informaci??n que es recogida

            Nuestra aplicacion podr?? recoger informaci??n personal por ejemplo: 
            Nombre, apellidos ,  informaci??n de contacto como  su direcci??n de correo 
            electr??nica e informaci??n demogr??fica. As?? mismo cuando sea necesario 
            podr?? ser requerida informaci??n espec??fica para procesar alg??n pedido o 
            realizar una entrega o facturaci??n.

            Uso de la informaci??n recogida

            Nuestra aplicacion emplea la informaci??n con el fin de proporcionar el 
            mejor servicio posible, particularmente para mantener un registro de 
            usuarios, de pedidos en caso que aplique, y mejorar nuestros productos y 
            servicios.  Es posible que sean enviados correos electr??nicos 
            peri??dicamente a trav??s de nuestra app con ofertas especiales, nuevos 
            productos y otra informaci??n publicitaria que consideremos relevante 
            para usted o que pueda brindarle alg??n beneficio, estos correos 
            electr??nicos ser??n enviados a la direcci??n que usted proporcione y 
            podr??n ser cancelados en cualquier momento.

            CoiffEarch est?? altamente comprometido para cumplir con el compromiso de 
            mantener su informaci??n segura. Usamos los sistemas m??s avanzados y los 
            actualizamos constantemente para asegurarnos que no exista ning??n acceso 
            no autorizado.

        """.trimIndent())
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }



    private fun registrar(email:String, pass:String){
        pb.cargar()
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    campoEmail =binding.camporREmail.text.toString()
                    campoPass = binding.campoRPass.text.toString()
                    campoRepitePass = binding.campoRRepitePass.text.toString()
                    campoNombre = binding.campoRNombre.text.toString()
                    campoUsuario = binding.campoRUsuario.text.toString()
                    campoApellidos = binding.campoRApellidos.text.toString()
                    if (binding.radioUsuario.isChecked)
                        campotipoCuenta = 0
                    else if(binding.radioEmpresa.isChecked){
                        campotipoCuenta = 1
                    }

                    val usuario = mapOf(
                        "id" to auth.currentUser?.uid.toString(),
                        "email" to campoEmail,
                        "usuario" to campoUsuario,
                        "nombre" to campoNombre,
                        "apellidos" to campoApellidos,
                        "tipoCuenta" to campotipoCuenta,
                        "estFavoritos" to listOf<String>(),
                        "imgPerfil" to ""

                    )
                    db.collection("usuarios").document(auth.currentUser?.uid.toString()).set(usuario).addOnCompleteListener {
                        val storage = Firebase.storage("gs://coiffearch.appspot.com")
                        storage.reference.child("ppdefault.png").downloadUrl.addOnSuccessListener {
                            db.collection("usuarios").document(auth.currentUser?.uid.toString()).update("imgPerfil",it.toString())
                        }.addOnSuccessListener {
                            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                            Toast.makeText(this,"La cuenta se ha creado correctamente",Toast.LENGTH_SHORT).show()
                        }
                    }

                } else {


                    Toast.makeText(this,"Error al crear la cuenta",Toast.LENGTH_SHORT).show()
                }

            }

    }



    override fun onBackPressed() {
        super.onBackPressed()

        startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
        finish()

    }


}