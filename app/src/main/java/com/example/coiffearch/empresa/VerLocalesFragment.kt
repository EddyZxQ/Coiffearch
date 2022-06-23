package com.example.coiffearch.empresa

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.adaptadorlocales.Local
import com.example.coiffearch.adaptadorlocales.RecyclerLocal
import com.example.coiffearch.databinding.FragmentVerLocalesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class VerLocalesFragment : Fragment(), RecyclerLocal.OnBotonesClickListener2  {

    private var _binding: FragmentVerLocalesBinding? = null
    private val binding get() = _binding!!


    private lateinit var auth: FirebaseAuth
    lateinit var recycler: RecyclerView
    private var listaLocales = mutableListOf<Local>()
    var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVerLocalesBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.btnAgregarLocal.setOnClickListener {
            findNavController().navigate(R.id.action_verLocalesFragment_to_nav_graph_empresa_agregar_estab2)
        }

        obtenerEstablecimiento()
    }


    private fun obtenerEstablecimiento(){
        listaLocales.clear()
        db.collection("establecimiento").get().addOnSuccessListener { documentos ->

            for (documento in documentos){
                if (documento.data["idPropietario"].toString() == auth.currentUser?.uid.toString()){
                    listaLocales.add(
                        Local(
                            documento.data["establecimientoId"].toString(),
                            documento.data["nombre"].toString(),
                            documento.data["fotoPortada"].toString()
                        )
                    )

                }
            }

            setUp()
        }
    }

    fun setUp(){
        recycler= binding.listaLocales
        recycler.layoutManager= LinearLayoutManager(PanelEmpresaActivity.contexto)
        recycler.adapter= RecyclerLocal(PanelEmpresaActivity.contexto,listaLocales,this)

    }


    private fun alerta_eliminar(idEstab: String){
        var bindingDialog = layoutInflater.inflate(R.layout.alerta_eliminar, null)
        var alertDialog = Dialog(requireContext())

        alertDialog.setContentView(bindingDialog)
        alertDialog.setCancelable(true)
        alertDialog.show()

        bindingDialog.findViewById<TextView>(R.id.alertDescripcion).text = "¿Estas seguro que deseas eliminar el local?"
        bindingDialog.findViewById<Button>(R.id.btnAceptarAlerta).setOnClickListener {
            db.collection("establecimiento").document(idEstab).delete()

            for (i in 0 until  listaLocales.size){
                if (listaLocales[i].id == idEstab){
                    listaLocales.removeAt(i)
                }
            }

            setUp()
            alertDialog.dismiss()
        }

        bindingDialog.findViewById<Button>(R.id.btnCancelarAlerta).setOnClickListener { alertDialog.dismiss()}

    }

    override fun onEliminarClick(idEstab: String) { alerta_eliminar(idEstab) }

    override fun onCartaClick(idEstab: String) {
        val bundle = Bundle()
        bundle.putString("idEstab", idEstab)
        findNavController().navigate(R.id.action_verLocalesFragment_to_empleadosFragment, bundle)

    }

    override fun onEditarClick(idEstab: String) {
        val bundle = Bundle()
        bundle.putString("idEstab", idEstab)
        findNavController().navigate(R.id.action_verLocalesFragment_to_configuracionFragment_empresa, bundle)

    }


}