package com.example.coiffearch.empresa.empleados

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.coiffearch.databinding.FragmentAgregarEmpleadosBinding
import com.example.coiffearch.utiles.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class AgregarEmpleadosFragment : Fragment() {

    private var _binding:FragmentAgregarEmpleadosBinding? = null
    private val binding get() = _binding!!


    private var db =Firebase.firestore


    private var listaServicios: MutableList<String> = mutableListOf()
    private var idEstablecimiento = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAgregarEmpleadosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEstablecimiento = arguments?.getString("idEstab").toString()

        binding.cHoraAperturaLocal.setOnClickListener { dialogoTiempo(binding.cHoraAperturaLocal)}
        binding.cHoraCierreLocal.setOnClickListener { dialogoTiempo(binding.cHoraCierreLocal)}
        binding.cTiempoInicioComida.setOnClickListener { dialogoTiempo(binding.cTiempoInicioComida)}
        binding.cTiempoFinComida.setOnClickListener { dialogoTiempo(binding.cTiempoFinComida)}
        binding.cIntervaloEntreCitas.setOnClickListener {  dialogoTiempoEntreCitas() }

        binding.btnAgregarServicio.setOnClickListener {agregarServicio() }
        binding.btnVerServiciosAgregados.setOnClickListener {  if (listaServicios.isNotEmpty()) popUpServicios() else Toast.makeText(activity,"No hay ningun servicio en la lista",Toast.LENGTH_SHORT).show() }

        binding.btnAgregarEmpleado.setOnClickListener { crearEmpleado() }

    }


    private fun dialogoTiempo(campo: EditText) {

        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(activity, object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                if (minute in 0..9){
                    campo.setText(String.format("%d:0%d", hourOfDay, minute))
                }else {
                    campo.setText(String.format("%d:%d", hourOfDay, minute))
                }
            }
        }, hour, minute, true)

        mTimePicker.show()

    }

    private fun dialogoTiempoEntreCitas() {

        val mTimePicker: TimePickerDialog
        val mcurrentTime = Calendar.getInstance()
        val hour = mcurrentTime.get(Calendar.HOUR_OF_DAY)
        val minute = mcurrentTime.get(Calendar.MINUTE)

        mTimePicker = TimePickerDialog(activity, 3,object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                if (hourOfDay >  0){
                    binding.cIntervaloEntreCitas.setText(((hourOfDay*60)+minute).toString())
                }else{
                    binding.cIntervaloEntreCitas.setText(minute.toString())
                }
            }
        }, 0, minute, true)


        mTimePicker.show()

    }


    private fun agregarServicio(){
        if (binding.cServiciosLocal.text.toString().isNotEmpty() &&  binding.cServiciosLocal.text.toString().isNotEmpty()){
            listaServicios.add(binding.cServiciosLocal.text.toString()+" - "+binding.cEstablecerPrecio.text.toString()+"€")
            binding.cServiciosLocal.text.clear()
            binding.cEstablecerPrecio.text.clear()
        }else{
            requireContext().toast("Debes rellenar el campo del servicio y el precio")
        }
    }


    private fun popUpServicios(){

        var listaServiciox : ListView = ListView(activity)
        val adaptador = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,listaServicios)
        listaServiciox.adapter = adaptador



        val builder = AlertDialog.Builder(requireContext())

        builder.setView(listaServiciox)
        builder.setTitle("Lista De Servicios").setMessage("Pulsa para eliminar")
        builder.setNeutralButton("Cerrar",{_,_ -> null})
        val dialog: AlertDialog = builder.create()
        dialog.show()


        listaServiciox.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, pos:Int,
                                                id: Long ->
            val servicio = adaptador.getItem(pos) as String
            listaServicios.remove(servicio)
            dialog.hide()

            popUpServicios()
        }

    }



    private fun crearEmpleado(){

        var nombre = binding.campoNombre.text.toString()
        var email = binding.campoEmail.text.toString()
        var pass = binding.campoPass.text.toString()
        var horaApertura = binding.cHoraAperturaLocal.text.toString()
        var horaCierre = binding.cHoraCierreLocal.text.toString()
        var intervaloCitas = binding.cIntervaloEntreCitas.text.toString()
        var inicioComida = binding.cTiempoInicioComida.text.toString()
        var finComida = binding.cTiempoFinComida.text.toString()

        var idUserEmpleado = UUID.randomUUID().toString()

        if (nombre.isNotEmpty() && horaApertura.isNotEmpty() && horaCierre.isNotEmpty()&& intervaloCitas.isNotEmpty() && inicioComida.isNotEmpty() && finComida.isNotEmpty()){
            if (listaServicios.isNotEmpty()){


                val usuario = mapOf(
                    "id" to idUserEmpleado,
                    "email" to email,
                    "nombre" to nombre,
                    "pass" to pass,
                    "tipoCuenta" to "3",
                    "idLocal" to idEstablecimiento

                )
                db.collection("usuarios").document(idUserEmpleado).set(usuario).addOnCompleteListener {

                    db.collection("establecimiento")
                        .document(idEstablecimiento)
                        .collection("empleados")
                        .document(idUserEmpleado)
                        .set(
                            mapOf(
                                "nombre" to nombre,
                                "idempleado" to idUserEmpleado,
                                "servicio" to listaServicios,
                                "aperturaHora" to horaApertura,
                                "cierreHora" to horaCierre,
                                "diasCierre" to FieldValue.arrayUnion(),
                                "intervaloCitas" to intervaloCitas,
                                "inicioComida" to inicioComida,
                                "finComida" to finComida
                            )
                        ).addOnSuccessListener {
                            findNavController().popBackStack()
                            requireContext().toast("Empleado agregado correctamente")
                        }


                }




            }else{
                requireContext().toast("Debes agregar almenos un servicio")

            }
        }else{
            requireContext().toast("Debes rellenar todos los campos")
        }
    }

}