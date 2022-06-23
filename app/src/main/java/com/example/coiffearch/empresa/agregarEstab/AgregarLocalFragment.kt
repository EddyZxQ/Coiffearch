package com.example.coiffearch.empresa.agregarEstab

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentAgregarLocalBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import java.util.*


class AgregarLocalFragment : Fragment() {

    private var _binding: FragmentAgregarLocalBinding? = null
    private val binding get() = _binding!!



    private  var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth
    private  var listaServicios: MutableList<String> = mutableListOf()

    private  var imagenLocal: Uri? = null


    lateinit var storage: FirebaseStorage
    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->
        if (activityResult.resultCode == AppCompatActivity.RESULT_OK) {
            imagenLocal = activityResult.data?.data
            subirFotoPrincipalLocal(imagenLocal!!, UUID.randomUUID().toString())
        }
    }
    var idEstab = ""


    private var fotoPortada:String = ""



override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAgregarLocalBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEstab = UUID.randomUUID().toString()
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


        binding.btnCrearLocal.setOnClickListener { crearLocal() }
        binding.btnFotoPrincipalLocal.setOnClickListener { fileManager() }



        binding.cHoraAperturaLocal.setOnClickListener { dialogoTiempo(binding.cHoraAperturaLocal)}
        binding.cHoraCierreLocal.setOnClickListener { dialogoTiempo(binding.cHoraCierreLocal)}
        binding.cTiempoInicioComida.setOnClickListener { dialogoTiempo(binding.cTiempoInicioComida)}
        binding.cTiempoFinComida.setOnClickListener { dialogoTiempo(binding.cTiempoFinComida)}
        binding.cIntervaloEntreCitas.setOnClickListener {  dialogoTiempoEntreCitas() }


        //AutoComplete
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


    private fun crearLocal(){

        var idPropietario = auth.currentUser?.uid.toString()
        var nombre = binding.cNombreLocal.text.toString()

        var publico = binding.cPublicoLocal.text.toString()
        var horaApertura = binding.cHoraAperturaLocal.text.toString()
        var horaCierre = binding.cHoraCierreLocal.text.toString()
        var intervaloCitas = binding.cIntervaloEntreCitas.text.toString()
        var inicioComida = binding.cTiempoInicioComida.text.toString()
        var finComida = binding.cTiempoFinComida.text.toString()
        var estaAbierto = binding.switchREstaAbierto.isChecked
        var visibilidad = binding.switchREstaAbierto.isChecked

        var calle = binding.cCalle.text.toString()
        var provincia = binding.cProvincia.text.toString()
        var municipio = binding.cMunicipio.text.toString()


        var imgsLocalId = ""
        var imgRutaId = ""



        if (nombre != ""){
            if (calle != ""){
                if (horaApertura !="" && horaCierre != ""){
                    if (listaServicios.isNotEmpty()){
                        if (intervaloCitas != ""){


                            var datos = mapOf(
                                "idPropietario" to idPropietario,
                                "nombre" to nombre,
                                "calle" to calle,
                                "provincia" to provincia,
                                "municipio" to municipio,
                                "servicio" to listaServicios,
                                "imagenesExposicion" to mutableListOf<String>(),
                                "publico" to publico,
                                "aperturaHora" to horaApertura,
                                "cierreHora" to horaCierre,
                                "descripcion" to "",
                                "diasCierre" to FieldValue.arrayUnion(),
                                "establecimientoId" to idEstab,
                                "intervaloCitas" to intervaloCitas,
                                "inicioComida" to inicioComida,
                                "finComida" to finComida,
                                "estaAbierto" to estaAbierto,
                                "estado" to visibilidad,
                                "storageRef" to imgRutaId,
                                "fotoPortada" to fotoPortada
                            )

                            db.collection("establecimiento").document(idEstab).set(datos).addOnCompleteListener {
                                Toast.makeText(activity,"LOCAL AGREGADO EXITOSAMENTE", Toast.LENGTH_SHORT).show()

                               findNavController().popBackStack()

                            }


                        }else{
                            Toast.makeText(activity, "Establece el tiempo entre citas", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(activity, "Debes establecer un servicio como minimo", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(activity, "Debes especificar la hora de apertura y de cierre", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(activity, "Debes especificar la ubicacion del local", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(activity, "Debes especificar un nombre para el local", Toast.LENGTH_SHORT).show()
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
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("imagesLocales").child(idEstab)
        val fileName: StorageReference = folder.child(idLocal)
        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { url ->

                fotoPortada=url.toString()
                Glide.with(this).load(fotoPortada).into(binding.imagenPrincipal)


            }
        }


    }


}