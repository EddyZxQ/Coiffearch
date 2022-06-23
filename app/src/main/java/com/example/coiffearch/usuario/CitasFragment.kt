package com.example.coiffearch.usuario

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coiffearch.R
import com.example.coiffearch.citasadptador.Cita
import com.example.coiffearch.citasadptador.RecyclerCitas
import com.example.coiffearch.databinding.FragmentBusquedaBinding
import com.example.coiffearch.databinding.FragmentCitasBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class CitasFragment : Fragment(), RecyclerCitas.OnItemRecyclerClickListener  {

    private var _binding: FragmentCitasBinding? = null
    private val binding get() = _binding!!


    var db = Firebase.firestore
    var idUser = Firebase.auth.currentUser?.uid.toString()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCitasBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db.collection("citas").whereEqualTo("userId", idUser).addSnapshotListener { value, error ->

            var citas = value!!.toObjects(Cita::class.java)


            binding.listaCitas.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(PanelUsuarioActivity.contexto)
                adapter = RecyclerCitas(PanelUsuarioActivity.contexto, citas,this@CitasFragment)
            }

        }

    }


    override fun onItemClick(idCita: String) {


    }

    override fun onDireccionClick(ubicacion: String) {
        lanzarMaps(ubicacion)

    }

    private fun lanzarMaps(direccion: String){
        val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$direccion")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }




    override fun onEliminarClick(idCita: String) {
        alerta_eliminar(idCita)
    }

    override fun onItemClick2(idCita: String, view: View) {
        if (view.findViewById<LinearLayout>(R.id.item_desc_expandible).visibility == View.GONE)
            view.findViewById<LinearLayout>(R.id.item_desc_expandible).visibility = View.VISIBLE
        else
            view.findViewById<LinearLayout>(R.id.item_desc_expandible).visibility = View.GONE
    }

    private fun alerta_eliminar(idCita: String){
        var bindingDialog = layoutInflater.inflate(R.layout.alerta_eliminar, null)
        var alertDialog = Dialog(requireContext())

        alertDialog.setContentView(bindingDialog)
        alertDialog.setCancelable(true)
        alertDialog.show()

        bindingDialog.findViewById<TextView>(R.id.alertDescripcion).text = "¿Estas seguro que deseas cancelar su cita en este local?"
        bindingDialog.findViewById<Button>(R.id.btnAceptarAlerta).setOnClickListener {

            db.collection("citas").document(idCita).delete()
            alertDialog.dismiss()

        }

        bindingDialog.findViewById<Button>(R.id.btnCancelarAlerta).setOnClickListener { alertDialog.dismiss()}

    }




}