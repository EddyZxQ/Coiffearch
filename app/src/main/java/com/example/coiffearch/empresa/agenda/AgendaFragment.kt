package com.example.coiffearch.empresa.agenda

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.adaptadorCalendario.CitaCalendario
import com.example.coiffearch.adaptadorCalendario.RecyclerCitasCalendario
import com.example.coiffearch.adaptadorDiasMes.RecyclerCalendario
import com.example.coiffearch.databinding.FragmentAgendaBinding
import com.example.coiffearch.adaptadorDiasMes.Calendario
import com.example.coiffearch.empresa.PanelEmpresaActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*


class AgendaFragment : Fragment(), RecyclerCalendario.CalendarioClickListener, RecyclerCitasCalendario.OnBotonesClickListener2  {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    var db = Firebase.firestore

    lateinit var recycler: RecyclerView
    private var citas = mutableListOf<CitaCalendario>()

    companion object{
        var diaHoy = LocalDate.now().toString().split("-")[2].toInt() -1
    }


    private var hoy = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    //DIAS X MES
    private lateinit var recyclerDiasMes: RecyclerView
    private var diaMes:MutableList<Calendario> = mutableListOf()
    private var currentFecha: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    private  var idEstablecimiento:String? = ""
    private  var idEmpleado:String? = ""


    private val sdf  = SimpleDateFormat("YYYY/mm/DD hh:MM:ss")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        idEstablecimiento = arguments?.getString("idEstab")
        idEmpleado = arguments?.getString("idEmpleado").toString()


        //BTN ANTERIOR
        binding.btnAnterior.setOnClickListener {
            currentFecha.add(Calendar.MONTH, -1)

            if (hoy == currentFecha){
                diaHoy = LocalDate.now().toString().split("-")[2].toInt() -1
            }else{
                diaHoy = 0
            }


            diaMes.clear()
            cargarDias()
            binding.fieldFecha.text = ""

        }

        //BTN SIGUIENTE
        binding.btnSiguiente.setOnClickListener {
            diaHoy =0
            currentFecha.add(Calendar.MONTH, 1)

            if (hoy == currentFecha){
                diaHoy = LocalDate.now().toString().split("-")[2].toInt() -1
            }else{
                diaHoy = 0
            }

            diaMes.clear()
            cargarDias()
            binding.fieldFecha.text = ""
        }

        cargarDias()


        binding.btnAgregarCitaManual.setOnClickListener { irCitaManual() }

    }

    private fun irCitaManual() {
        val bundle = Bundle()
        bundle.putString("idEstab", idEstablecimiento)
        findNavController().navigate(R.id.action_agendaFragment_to_agregarCitaManualFragment,bundle)
    }


    fun setUp(){
        citas.sortBy { it.tiempoCita }
        recycler= binding.listaTurnos
        recycler.layoutManager= LinearLayoutManager(activity?.baseContext!!)
        recycler.adapter= RecyclerCitasCalendario(activity?.baseContext!!,citas,this)
    }


    private fun setUpCalendario(){

        var linear = LinearLayoutManager(PanelEmpresaActivity.contexto)
        linear.orientation = LinearLayoutManager.HORIZONTAL

        recyclerDiasMes= binding.listaDiasMes
        recyclerDiasMes.layoutManager= linear

        recyclerDiasMes.adapter= RecyclerCalendario(PanelEmpresaActivity.contexto,diaMes,this)


        recyclerDiasMes.postDelayed(Runnable {

            recyclerDiasMes.smoothScrollToPosition(diaHoy)

        }, 50)



    }

    private  fun cargarDias(){
        try {

            var fecha: LocalDate

            for (i in 1..currentFecha.getActualMaximum(Calendar.DAY_OF_MONTH)){

                fecha = LocalDate.of(currentFecha[1], currentFecha[2]+1, i)




                diaMes.add(
                    Calendario(
                        i.toString(),
                        (currentFecha[2]+1).toString(),
                        currentFecha[1].toString(),
                        when(fecha.dayOfWeek.value){
                            1 -> "LUN"
                            2 -> "MAR"
                            3 -> "MIE"
                            4 -> "JUE"
                            5 -> "VIE"
                            6 -> "SAB"
                            7 -> "DOM"
                            else ->"JAJAXD"
                        }
                    )
                )

            }
            setUpCalendario()
        } catch (e: Exception) {
        }
    }

    fun diaCompleto(dia:String):String{
        return when(dia){
            "LUN" -> "Lunes"
            "MAR" -> "Martes"
            "MIE" -> "Miercoles"
            "JUE" -> "Jueves"
            "VIE" -> "Viernes"
            "SAB" -> "Sabado"
            "DOM" -> "Domingo"
            else ->"JAJAXD"
        }
    }

    fun mesCompleto(mes:Int):String{
        return when(mes){
            1 -> "Enero"
            2 -> "Febrero"
            3 -> "Marzo"
            4 -> "Abril"
            5 -> "Mayo"
            6 -> "Junio"
            7 -> "Julio"
            8 -> "Agosto"
            9 -> "Septiembre"
            10 -> "Octubre"
            11 -> "Noviembre"
            12 -> "Diciembre"
            else ->"JAJAXD"
        }
    }



    override fun onCartaClick(dia: String, diaTexto: String, mes: String, ano: String) {

        if (!recyclerDiasMes.isComputingLayout){
            recyclerDiasMes.adapter?.notifyDataSetChanged()
        }else{
            citas.clear()
            binding.fieldFecha.text = "${diaCompleto(diaTexto)} $dia, ${mesCompleto(mes.toInt())} $ano"

            db.collection("citas")
                .whereEqualTo("establecimientoId", idEstablecimiento)
                .whereEqualTo("idEmpleado",idEmpleado)
                .whereEqualTo("fecha", "$dia/$mes/$ano")
                .get()
                .addOnSuccessListener { resultado ->

                    for (documento in resultado){


                        citas.add(
                            CitaCalendario(
                                documento["establecimientoId"].toString(),
                                documento["citaId"].toString(),
                                documento["userId"].toString(),
                                documento["nombre"].toString(),
                                documento["hora"].toString(),
                                dia,
                                mes,
                                ano,
                                documento["accion"].toString()
                            )
                        )


                    }//For
                    setUp()

                }
        }

    }

    override fun onCartaClick(idEstab: String) {}


}