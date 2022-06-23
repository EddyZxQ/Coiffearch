package com.example.coiffearch.empresa.agenda

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentAgendaEmpleadosBinding
import com.example.coiffearch.databinding.FragmentConfigurationGalleryBinding
import com.example.coiffearch.empresa.PanelEmpresaActivity
import com.example.coiffearch.empresa.empleados.Empleado
import com.example.coiffearch.empresa.empleados.RecyclerEmpleados
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class AgendaEmpleadosFragment : Fragment(),RecyclerAgendaEmpleados.OnItemEmpleadoClickListener {

    private var _binding :FragmentAgendaEmpleadosBinding? = null
    private val binding get() = _binding!!


    private var db = Firebase.firestore
    private var idEstablecimiento = ""
    private var empleados = mutableListOf<Empleado>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgendaEmpleadosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEstablecimiento = arguments?.getString("idEstab").toString()
        cargarListaEmpleados()

    }

    private fun cargarListaEmpleados() {
        db.collection("establecimiento")
            .document(idEstablecimiento)
            .collection("empleados").addSnapshotListener { value, error ->

                empleados = value!!.toObjects(Empleado::class.java)

                binding.listaEmpleados.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = RecyclerAgendaEmpleados(PanelEmpresaActivity.contexto, empleados,this@AgendaEmpleadosFragment)
                }
            }
    }

    override fun onCajaClick(idUser: String) {
        val bundle = Bundle()
        bundle.putString("idEstab", idEstablecimiento)
        bundle.putString("idEmpleado", idUser)
        findNavController().navigate(R.id.action_agendaEmpleadosFragment_to_agendaFragment, bundle)
    }

}