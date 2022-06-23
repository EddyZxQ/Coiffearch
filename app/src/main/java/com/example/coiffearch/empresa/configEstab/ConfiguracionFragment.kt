package com.example.coiffearch.empresa.configEstab

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentConfiguracionBinding
import com.example.coiffearch.empresa.PanelEmpresaActivity
import com.example.coiffearch.utiles.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*


class ConfiguracionFragment : Fragment(){

    private var _binding: FragmentConfiguracionBinding? = null
    private  val binding get() = _binding!!


    var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private  var numFotos = ""

    private  var listaFotosExposicion: MutableList<String> = mutableListOf()


    private  var listaServicios: MutableList<String> = mutableListOf()

    private  var imagenLocal: Uri? = null



    lateinit var storage: FirebaseStorage
    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if (activityResult.resultCode == AppCompatActivity.RESULT_OK) {
            imagenLocal = activityResult.data?.data

            Glide.with(this).load(imagenLocal).into(binding.fotoPrincipalLocal)
            subirFotoPrincipalLocal(imagenLocal!!, UUID.randomUUID().toString())
        }
    }


    companion object{
        var listaUrlFotos: MutableList<String> = mutableListOf()

        var idEstablecimiento:String? = ""
    }

    //Campos

    private var nombreLocal:String = ""
    private  var descripcionLocal:String = ""



    private  var horaApertura =0
    private  var minutoApertura=0

    private  var horaCierre=0
    private  var minutoCierre =0

    private var listaDeDiasCierre = mutableListOf<String>()
    private  var publico = ""

    private var calle = ""
    private var provincia = ""
    private var municipio= ""


    private  var estaAbierto = false
    private  var visibilidad = false

    private var inicioComida = ""
    private var finComida = ""
    private var tiempoEntreCitas = ""

    private var aperturaHora = ""
    private  var cierreHora = ""

    private var contexto = PanelEmpresaActivity.contexto

    private var fotoPortada:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfiguracionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        idEstablecimiento = arguments?.getString("idEstab")
        recibirDatosLocal()

        auth = Firebase.auth
        binding.btnAgregarServicio.setOnClickListener {
            if (binding.cServiciosLocal.text.toString().isNotEmpty()){
                listaServicios.add(binding.cServiciosLocal.text.toString()+" - "+binding.cEstablecerPrecio.text.toString()+"€")
                binding.cServiciosLocal.text.clear()
                binding.cEstablecerPrecio.text.clear()
            }
        }


        binding.btnVerServiciosAgregados.setOnClickListener {
            if (listaServicios.isNotEmpty()) popUpServicios() else Toast.makeText(activity,"No hay ningun servicio en la lista",Toast.LENGTH_SHORT).show()
        }

        binding.btnActualizarInfoLocal.setOnClickListener {actualizarDatosLocal() }
        binding.btnFotoPrincipalLocal.setOnClickListener { fileManager() }



        binding.cHoraAperturaLocal.setOnClickListener { dialogoTiempo(binding.cHoraAperturaLocal)}
        binding.cHoraCierreLocal.setOnClickListener { dialogoTiempo(binding.cHoraCierreLocal)}
        binding.cTiempoInicioComida.setOnClickListener { dialogoTiempo(binding.cTiempoInicioComida)}
        binding.cTiempoFinComida.setOnClickListener { dialogoTiempo(binding.cTiempoFinComida)}
        binding.cIntervaloEntreCitas.setOnClickListener {  dialogoTiempoEntreCitas() }


        binding.btnGalleriaFotos.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("idEstab", idEstablecimiento)
            findNavController().navigate(R.id.action_configuracionFragment2_to_configurationGalleryFragment, bundle)
        }

        camposAutoCompletables()

    }


    private  fun camposAutoCompletables(){

        val adapterProvincia: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.provincias))
        val cProvincia = binding.cProvincia as AutoCompleteTextView
        cProvincia.setAdapter(adapterProvincia)

        val adapterMunicipio: ArrayAdapter<String> = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, resources.getStringArray(R.array.municipio))
        val cMunicipio = binding.cMunicipio as AutoCompleteTextView
        cMunicipio.setAdapter(adapterMunicipio)
    }

    private fun actualizarDatosLocal(){


        var datos = mapOf(
            "idPropietario" to auth.currentUser?.uid.toString(),
            "nombre" to binding.cNombreLocal.text.toString(),
            "calle" to binding.cCalle2.text.toString(),
            "provincia" to binding.cProvincia.text.toString(),
            "municipio" to binding.cMunicipio.text.toString(),
            "servicio" to listaServicios,
            "publico" to binding.cPublicoLocal.text.toString(),
            "aperturaHora" to binding.cHoraAperturaLocal.text.toString(),
            "cierreHora" to binding.cHoraCierreLocal.text.toString(),
            "descripcion" to binding.cDescripcionLocal.text.toString(),
            "diasCierre" to listaDeDiasCierre,
            "estaAbierto" to estaAbierto,
            "estado" to visibilidad,
            "establecimientoId" to idEstablecimiento,
            "finComida" to finComida,
            "inicioComida" to inicioComida,
            "estaAbierto" to estaAbierto,
            "intervaloCitas" to tiempoEntreCitas,
            "fotoPortada" to fotoPortada

        )

        db.collection("establecimiento")
            .document(idEstablecimiento.toString())
            .update(datos).addOnSuccessListener {

                listaUrlFotos.clear()
                val navController: NavController = requireActivity().findNavController(R.id.nav_host_empresa)
                navController.run {
                    popBackStack()
                }
            }



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

    private fun fileManager(){
        storage = Firebase.storage

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }

        intent.type = "image/*"
        responseLauncher.launch(intent)

    }


    fun subirFotoPrincipalLocal(mUri:Uri, idLocal:String){
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("imagesLocales")
        val fileName: StorageReference = folder.child(idLocal)
        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { url ->
               fotoPortada = url.toString()
            }
        }


    }

    private fun popUpDiasCierre(){


        var arrayDias = arrayOf("lunes","martes","miercoles","jueves","viernes","sabado","domingo")

        val diaschecked = booleanArrayOf(
            false, // Lunes
            false, // Martes
            false, // Miercoles
            false, // Jueves
            false, // Viernes
            false,  //Sabado
            false  //Domingo
        )

        var copiaListaDiasCierre = listaDeDiasCierre.toMutableList() //Hay que poner el toMutaBLeList para que sea  una copia

        for (i in arrayDias.indices){ diaschecked[i] = listaDeDiasCierre.contains(arrayDias[i]) }

        val diasList = Arrays.asList(*arrayDias)

        val builder = AlertDialog.Builder(contexto)
        builder.setTitle("Selecciona los dias de cierre del local")

        builder.setMultiChoiceItems(arrayDias,diaschecked){ dialog, which, isChecked ->
            diaschecked[which] = isChecked
            val currentItem = diasList[which]
            if (isChecked) listaDeDiasCierre.add(currentItem) else listaDeDiasCierre.remove(currentItem)

        }

        builder.setPositiveButton("OK") { dialog, which -> }
        builder.setNeutralButton("Cancel") { dialog, which -> listaDeDiasCierre = copiaListaDiasCierre }

        val dialog = builder.create()
        dialog.show()

    }




    private fun  recibirDatosLocal(){

        db.collection("establecimiento")
            .document(idEstablecimiento.toString())
            .get()
            .addOnSuccessListener { documento ->
                horaApertura = documento["aperturaHora"].toString().split(":")[0].toInt()
                minutoApertura = documento["aperturaHora"].toString().split(":")[1].toInt()
                horaCierre = documento["cierreHora"].toString().split(":")[0].toInt()
                minutoCierre =  documento["cierreHora"].toString().split(":")[1].toInt()

                aperturaHora = documento["aperturaHora"].toString()
                cierreHora = documento["cierreHora"].toString()

                nombreLocal = documento["nombre"].toString()
                descripcionLocal = documento["descripcion"].toString()
                listaDeDiasCierre = documento["diasCierre"] as MutableList<String>
                estaAbierto = documento["estaAbierto"] as Boolean
                visibilidad = documento["estado"] as Boolean
                calle = documento["calle"].toString()
                provincia = documento["provincia"].toString()
                municipio = documento["municipio"].toString()
                publico = documento["publico"].toString()
                listaServicios = documento["servicio"] as MutableList<String>
                tiempoEntreCitas = documento["intervaloCitas"].toString()
                inicioComida = documento["inicioComida"].toString()
                finComida = documento["finComida"].toString()

                fotoPortada = documento["fotoPortada"].toString()


            }.addOnSuccessListener {
                establecerCampos()
            }


    }



    private fun establecerCampos(){
        Glide.with(this).load(fotoPortada).into(binding.fotoPrincipalLocal)

        binding.switchREstaAbierto.isChecked = estaAbierto
        binding.switchRVisibilidad.isChecked = visibilidad
        binding.cNombreLocal.setText(nombreLocal)
        binding.cDescripcionLocal.setText(descripcionLocal)
        binding.cCalle2.setText(calle)
        binding.cMunicipio.setText(municipio)
        binding.cProvincia.setText(provincia)
        binding.cHoraAperturaLocal.setText(aperturaHora)
        binding.cHoraCierreLocal.setText(cierreHora)
        binding.cPublicoLocal.setText(publico)
        binding.cTiempoInicioComida.setText(inicioComida)
        binding.cTiempoFinComida.setText(finComida)
        binding.cIntervaloEntreCitas.setText(tiempoEntreCitas)

    }










}