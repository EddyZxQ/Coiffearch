package com.example.coiffearch.usuario

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.PersistableBundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SearchView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentBusquedaBinding
import com.example.coiffearch.establecimientoadaptador.Establecimiento
import com.example.coiffearch.establecimientoadaptador.RecyclerEstablecimientos
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class BusquedaFragment : Fragment(), RecyclerEstablecimientos.OnCajaClickListener {

    private var _binding: FragmentBusquedaBinding? = null
    private val binding get() = _binding!!


    var listaEstablecimientos: MutableList<Establecimiento> = mutableListOf()
    var listafiltroEstablecimientos: MutableList<Establecimiento> = mutableListOf()
    private var db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentBusquedaBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db.collection("establecimiento").addSnapshotListener {  value, error ->

            val establecimientos = value!!.toObjects(Establecimiento::class.java)

            listaEstablecimientos = establecimientos

            establecimientos.forEachIndexed { index, establecimiento ->
                establecimiento.uid =value.documents[index].id
            }


            binding.listaEstablecimientos.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = RecyclerEstablecimientos(PanelUsuarioActivity.contexto, establecimientos,this@BusquedaFragment)
            }

        }


        binding.buscador.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean = true

            override fun onQueryTextChange(nuevotexto: String?): Boolean {

                if (nuevotexto!!.isNotEmpty()){
                    listafiltroEstablecimientos.clear()
                    val busqueda = nuevotexto.lowercase()
                    listaEstablecimientos.forEach {
                        if (it.nombre?.lowercase()?.contains(busqueda) == true){
                            listafiltroEstablecimientos.add(it)
                        }
                    }


                }else{
                    listafiltroEstablecimientos.clear()
                    listafiltroEstablecimientos.addAll(listaEstablecimientos)

                }

                binding.listaEstablecimientos.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(requireContext())
                    adapter = RecyclerEstablecimientos(requireContext(), listafiltroEstablecimientos,this@BusquedaFragment)
                }

                return true
            }

        })




    }


    override fun onItemClick(idEstab: String) {
        val bundle = Bundle()
        bundle.putString("idEstab", idEstab)
        findNavController().navigate(R.id.action_panelbusquedaFragment_to_establecimientoFragment, bundle)
    }




}