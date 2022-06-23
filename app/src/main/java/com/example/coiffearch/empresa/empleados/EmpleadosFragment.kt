package com.example.coiffearch.empresa.empleados

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentEmpleadosBinding
import com.example.coiffearch.empresa.PanelEmpresaActivity
import com.example.coiffearch.establecimientoadaptador.RecyclerEstablecimientos
import com.example.coiffearch.usuario.PanelUsuarioActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class EmpleadosFragment : Fragment(), RecyclerEmpleados.OnItemEmpleadoClickListener {

    private var _binding: FragmentEmpleadosBinding? = null
    private val binding get() = _binding!!

    private var db = Firebase.firestore
    private var idEstablecimiento = ""

    private var empleados:MutableList<Empleado> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEmpleadosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEstablecimiento = arguments?.getString("idEstab").toString()

        binding.btnAgregarEmpleado.setOnClickListener { irAgregarEmpleado() }

        cargarListadoEmpleados()

    }

    private fun irAgregarEmpleado() {
        val bundle = Bundle()
        bundle.putString("idEstab", idEstablecimiento)
        findNavController().navigate(R.id.action_empleadosFragment_to_agregarEmpleadosFragment, bundle)
    }


    private fun cargarListadoEmpleados(){

        db.collection("establecimiento")
            .document(idEstablecimiento)
            .collection("empleados").addSnapshotListener { value, error ->

                empleados = value!!.toObjects(Empleado::class.java)

                binding.listaEmpleados.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = RecyclerEmpleados(PanelEmpresaActivity.contexto, empleados,this@EmpleadosFragment)
                }
            }
    }

    override fun onBtnEliminarClick(idUser: String) {

        db.collection("establecimiento")
            .document(idEstablecimiento)
            .collection("empleados")
            .document(idUser).delete()

        db.collection("usuarios")
            .document(idUser).delete()

    }
}