package com.example.coiffearch.empresa

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.adaptadorlocalescitas.LocalCitas
import com.example.coiffearch.adaptadorlocalescitas.RecyclerLocalCitas
import com.example.coiffearch.databinding.FragmentSelectorLocalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SelectorLocalFragment : Fragment(), RecyclerLocalCitas.OnBotonesClickListener2 {

    private var _binding: FragmentSelectorLocalBinding? = null
    private val binding  get() = _binding!!


    private lateinit var auth: FirebaseAuth
    lateinit var recycler: RecyclerView
    private var listaLocales = mutableListOf<LocalCitas>()
    var db = Firebase.firestore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSelectorLocalBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        listaLocales.clear()
        obtenerEstablecimiento()

    }

    private fun obtenerEstablecimiento(){
        db.collection("establecimiento").get().addOnSuccessListener { documentos ->

            for (documento in documentos){
                if (documento.data["idPropietario"].toString() == auth.currentUser?.uid.toString()){
                    listaLocales.add(
                        LocalCitas(
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
        recycler= binding.listalocales
        recycler.layoutManager= LinearLayoutManager(PanelEmpresaActivity.contexto)
        recycler.adapter= RecyclerLocalCitas(PanelEmpresaActivity.contexto,listaLocales,this)

    }


    override fun onCartaClick(idEstab: String) {

            val bundle = Bundle()
            bundle.putString("idEstab", idEstab)
            findNavController().navigate(R.id.action_selectorLocalFragment_to_agendaEmpleadosFragment, bundle)

    }




}