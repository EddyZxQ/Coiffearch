package com.example.coiffearch.empresa

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.adaptadorListadoHoras.Hora
import com.example.coiffearch.adaptadorListadoHoras.RecyclerHora
import com.example.coiffearch.adaptadorServicios.RecyclerServicios
import com.example.coiffearch.adaptadorServicios.Servicio
import com.example.coiffearch.databinding.FragmentAgregarCitaManualBinding
import com.example.coiffearch.utiles.toast
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*


class AgregarCitaManualFragment : Fragment(),RecyclerServicios.onServicioClickListener, RecyclerHora.OnBotonesClickListener2, DatePickerDialog.OnDateSetListener{

    //, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,

    private var _binding: FragmentAgregarCitaManualBinding? = null
    private val binding get() = _binding!!


    private var db = Firebase.firestore

    //Escoger Servicios
    private  var idEstablecimiento:String? = ""
    var listaServicios:MutableList<String> = mutableListOf()
    private var listaServicio: MutableList<Servicio> = mutableListOf()
    private var servicioSeleccionado = ""


    //Lista Horas
    private var intervaloCitas =0
    private var apertura = ""
    private var cierre = ""
    private var inicioComida = ""
    private var finComida = ""


    private var tiempoEntreAperturaCierreEnMinutos = 0
    private var tiempoParaComerMinutos = 0
    private var listaTurnos = mutableListOf<Hora>() //Aqui se gurda el listado de turnos del local
    private var listaTurnosOcupados = mutableListOf<String>()

    var guardarDia = 0
    var guardarMes = 0
    var guardarAno = 0
    var guardarHora = 0
    var guardarMinuto = 0
    var turno = ""

    private var dia = 0
    private var mes = 0
    private var ano = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgregarCitaManualBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEstablecimiento = arguments?.getString("idEstab")
        listaServicios()
        todosLosTurnos()



        binding.campoServicio.setOnClickListener { dialogoServicio() }
        binding.campoFecha.setOnClickListener { escogerFecha() }
        binding.campoHora.setOnClickListener { alerta_listahoras() }
        binding.btnVolverAgenda.setOnClickListener {  findNavController().popBackStack() }


        binding.btnAgregarCita.setOnClickListener {
            if (binding.campoFecha.text.toString().isNotEmpty() && binding.campoHora.text.toString().isNotEmpty() && binding.campoPersona.text.toString().isNotEmpty() && binding.campoServicio.text.toString().isNotEmpty()){
                agregarCita()
            }else{
                requireActivity().toast("Debes rellenar todos los campos para agregar una cita")
            }
        }



    }




    //Servicios
    private fun listaServicios(){

        db.collection("establecimiento")
            .document(idEstablecimiento!!).get()
            .addOnSuccessListener { documento ->
                listaServicios.addAll(documento["servicio"] as ArrayList<String>)

            }.addOnCompleteListener {
                for (servicio in  listaServicios){
                    listaServicio.add(Servicio(servicio))
                }
            }

    }


    private fun dialogoServicio(){
        var alertDialog = Dialog(requireContext(), R.style.DialogTheme)

        var bindingDialog = layoutInflater.inflate(R.layout.lista_servicios, null)
        val adaptador = RecyclerServicios(requireContext(),listaServicio,this, alertDialog)
        bindingDialog.findViewById<RecyclerView>(R.id.liztaServicos).adapter = adaptador
        bindingDialog.findViewById<RecyclerView>(R.id.liztaServicos).layoutManager= LinearLayoutManager(requireContext())


        alertDialog.setContentView(bindingDialog)
        alertDialog.setCancelable(true)
        alertDialog.show()
    }

    override fun onServicioClick(servicio: String, dialog: Dialog) {
        servicioSeleccionado = servicio
        binding.campoServicio.setText(servicio)
        dialog.dismiss()
    }


    //Lista Horas Disponibles para reservar

    private fun todosLosTurnos(){

        db.collection("establecimiento")
            .document(idEstablecimiento!!)
            .get()
            .addOnSuccessListener { documento ->
                intervaloCitas=documento["intervaloCitas"].toString().toInt()
                apertura = documento["aperturaHora"].toString()
                cierre = documento["cierreHora"].toString()
                inicioComida = documento["inicioComida"].toString()
                finComida = documento["finComida"].toString()


            }.addOnSuccessListener {

                //Horario
                var hCierre =cierre.split(":")[0].toInt()*60
                var mCierre =cierre.split(":")[1].toInt()
                var hApertura =apertura.split(":")[0].toInt()*60
                var mApertrua = apertura.split(":")[1].toInt()

                var minsCierre = mCierre+hCierre
                var minApertura = mApertrua+hApertura
                tiempoEntreAperturaCierreEnMinutos = minsCierre-minApertura


                var elementosPorDia = tiempoEntreAperturaCierreEnMinutos/intervaloCitas

                var auxH = hApertura/60 //HORAS
                var auxM = mApertrua //MINUTOS

                for (i in 0..elementosPorDia){

                    if(auxM in 0..9) listaTurnos.add(Hora("$auxH:0$auxM")) else listaTurnos.add(Hora("$auxH:$auxM"))

                    auxM+=intervaloCitas

                    if (auxM >= 60){
                        auxM -= 60
                        auxH+=1
                    }

                }


                //Comida

                var hInicioComidaMinutos = inicioComida.split(":")[0].toInt()*60
                var mInicicioComidaMinutos = inicioComida.split(":")[1].toInt()

                var hFinComidaMinutos = finComida.split(":")[0].toInt()*60
                var mFinComidaMinutos = finComida.split(":")[1].toInt()

                tiempoParaComerMinutos =  (hFinComidaMinutos+mFinComidaMinutos) -(hInicioComidaMinutos+mInicicioComidaMinutos)

                var auxHC = hInicioComidaMinutos / 60
                var auxMC = mInicicioComidaMinutos


                for (i in 0..tiempoParaComerMinutos){

                    if (listaTurnos.contains(Hora("$auxHC:$auxMC"))) listaTurnos.remove(Hora("$auxHC:$auxMC"))
                    if (listaTurnos.contains(Hora("$auxHC:0$auxMC"))) listaTurnos.remove(Hora("$auxHC:0$auxMC"))


                    if (i >=60){
                        auxMC = 0
                        auxHC += 1
                    }else{
                        auxMC+=1
                    }

                }


            }
            .addOnFailureListener {
                Toast.makeText(activity, "HA FALLADO COMO UNA CASA", Toast.LENGTH_LONG).show()
            }
    }


    private fun alerta_listahoras(){

        //Falta comprobar la fecha
        db.collection("citas")
            .whereEqualTo("establecimientoId", idEstablecimiento!!)
            .whereEqualTo("fecha","$guardarDia/$guardarMes/$guardarAno")
            .get()
            .addOnSuccessListener { resultado->

                var auxihora:String = ""
                var auxiHoraCambio:String = ""
                var auxiMinutos = ""

                for (documento in resultado){
                    var list = listOf<String>("00","01","02","03","04","05","06","07","08","09")

                    auxihora = documento["hora"].toString().split(":")[0]
                    auxiMinutos = documento["hora"].toString().split(":")[1]


                    if (auxihora in list) auxiHoraCambio = "${auxihora[1]}:${auxiMinutos}"

                    if (auxihora in list) listaTurnosOcupados.add(auxiHoraCambio) else listaTurnosOcupados.add(documento["hora"].toString())
                }
            }.addOnSuccessListener {
                var alertDialog = Dialog(requireContext())

                var bindingDialog = layoutInflater.inflate(R.layout.lista_horas, null)
                val adaptador = RecyclerHora(requireContext(),listaTurnos,this, alertDialog,listaTurnosOcupados)
                bindingDialog.findViewById<RecyclerView>(R.id.liztahoras).adapter = adaptador
                bindingDialog.findViewById<RecyclerView>(R.id.liztahoras).layoutManager= LinearLayoutManager(requireContext())


                alertDialog.setContentView(bindingDialog)
                alertDialog.setCancelable(true)
                alertDialog.show()
            }



    }

    override fun onCartaClick(idEstab: String, dialog: Dialog) {
        turno = idEstab

        binding.campoHora.setText(turno)

        guardarHora = turno.split(":")[0].toInt()
        guardarMinuto = turno.split(":")[1].toInt()

        dialog.dismiss()
    }


    private fun escogerFecha(){
        val cal = Calendar.getInstance()

        dia = cal.get(Calendar.DAY_OF_MONTH)
        mes = cal.get(Calendar.MONTH)
        ano = cal.get(Calendar.YEAR)

        DatePickerDialog(requireContext(),R.style.DialogTheme,this,ano,mes,dia).show()
    }

    override fun onDateSet(view: DatePicker?, ano: Int, mes: Int, diames: Int) {
        guardarDia = diames
        guardarMes = mes+1
        guardarAno = ano

        val cal = Calendar.getInstance()

        dia = cal.get(Calendar.DAY_OF_MONTH)
        this.mes = cal.get(Calendar.MONTH)
        this.ano = cal.get(Calendar.YEAR)

        binding.campoFecha.setText("$guardarDia/$guardarMes/$guardarAno")

    }


    //Agregar Cita

    private fun agregarCita(){
        var turnox = ""

        var id = UUID.randomUUID()

        turnox =if (turno.split(":")[0].toInt() in 0..9)"0$turno" else turno

        db.collection("citas").document(id.toString())
            .set(mapOf(
                "accion" to servicioSeleccionado,
                "citaId" to id.toString(),
                "establecimientoId" to idEstablecimiento!!,
                "fecha" to "$guardarDia/$guardarMes/$guardarAno",
                "hora" to turnox,
                "nombre" to binding.campoPersona.text.toString(),
            )).addOnSuccessListener {
                binding.apply {
                    campoPersona.text.clear()
                    campoHora.text.clear()
                    campoServicio.text.clear()
                }
            }

    }





}