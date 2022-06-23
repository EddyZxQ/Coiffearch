package com.example.coiffearch.usuario

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.coiffearch.LoginActivity
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentPerfilUsuarioBinding
import com.example.coiffearch.preferencias.CoiffearchApplication
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class PerfilUsuarioFragment : Fragment() {

    private var _binding: FragmentPerfilUsuarioBinding? = null
    private val binding get() = _binding!!


    private  var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPerfilUsuarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        auth = Firebase.auth
        getInfoUsuario()



        binding.btnActualizar.setOnClickListener {

            db.collection("usuarios")
                .document(auth.currentUser?.uid.toString())
                .update(
                    "email", binding.cEmail.text.toString(),
                    "nombre", binding.cNombre.text.toString(),
                    "apellidos", binding.cApellido.text.toString(),
                    "usuario", binding.cUsuario.text.toString())

            auth.currentUser!!.updateEmail(binding.cEmail.text.toString()).addOnCompleteListener {

            }

        }

        binding.btnEliminarCuenta.setOnClickListener {
            db.collection("usuarios").document(auth.currentUser?.uid.toString())
                .delete()
                .addOnSuccessListener { Toast.makeText(activity, "Cuenta eliminada con exito", Toast.LENGTH_SHORT).show()

                    Firebase.auth.signOut()
                    auth.currentUser?.delete()
                    CoiffearchApplication.prefs.saveRecordar(false)
                    startActivity(Intent(activity, LoginActivity::class.java))
                    activity?.finish()

                }
                .addOnFailureListener { Toast.makeText(activity, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show() }

        }

        binding.btnEditarPerfil.setOnClickListener {


            if (binding.panelEditarPerfil.visibility == View.GONE){
                binding.panelEditarPerfil.visibility = View.VISIBLE
                binding.iconoOpcionesEditarPerfil.setImageResource(R.drawable.arrow_desplegada)

            }else{
                binding.panelEditarPerfil.visibility = View.GONE
                binding.iconoOpcionesEditarPerfil.setImageResource(R.drawable.arrow)
            }

        }

        binding.btnAjustesCuenta.setOnClickListener {


            if (binding.panelAjustesCuenta.visibility == View.GONE){
                binding.panelAjustesCuenta.visibility = View.VISIBLE
                binding.iconoAjustesCuenta.setImageResource(R.drawable.arrow_desplegada)

            }else{
                binding.panelAjustesCuenta.visibility = View.GONE
                binding.iconoAjustesCuenta.setImageResource(R.drawable.arrow)
            }

        }



        binding.btnCerrarSesionPerfilUser.setOnClickListener {
            CoiffearchApplication.prefs.saveRecordar(false)
            Firebase.auth.signOut()
            startActivity(Intent(activity, LoginActivity::class.java))
            activity?.finish()
        }



    }

    private fun getInfoUsuario(){
        db.collection("usuarios")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener {
                if (it.exists()) {

                    binding.userName.text = it.get("nombre").toString().replaceFirstChar(Char::titlecase) +" "+it.get("apellidos").toString().replaceFirstChar(Char::titlecase)
                    binding.cNombre.setText(it.get("nombre").toString())
                    binding.cApellido.setText(it.get("apellidos").toString())
                    binding.cEmail.setText(it.get("email").toString())
                    binding.cUsuario.setText(it.get("usuario").toString())


                    Glide.with(this).load(it.get("imgPerfil")).into(binding.fotoPerfil)
                }
            }
    }



}