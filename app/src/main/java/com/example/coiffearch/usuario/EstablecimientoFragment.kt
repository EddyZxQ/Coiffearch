package com.example.coiffearch.usuario

import android.app.*
import android.app.Notification
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.provider.ContactsContract
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.*
import com.example.coiffearch.adaptadorListadoHoras.Hora
import com.example.coiffearch.adaptadorListadoHoras.RecyclerHora
import com.example.coiffearch.adaptadorServicios.RecyclerServicios
import com.example.coiffearch.adaptadorServicios.Servicio
import com.example.coiffearch.databinding.FragmentEstablecimientoBinding
import com.example.coiffearch.empresa.PanelEmpresaActivity
import com.example.coiffearch.empresa.empleados.Empleado
import com.example.coiffearch.empresa.empleados.RecyclerEmpleados
import com.example.coiffearch.usuario.empleados.RecyclerSeleccionarEmpleado
import com.example.coiffearch.utiles.toast
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import org.imaginativeworld.whynotimagecarousel.ImageCarousel
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.time.DayOfWeek
import java.util.*


class EstablecimientoFragment : Fragment(),
    DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener,
    RecyclerHora.OnBotonesClickListener2,
    RecyclerServicios.onServicioClickListener,
    RecyclerSeleccionarEmpleado.OnItemEmpleadoClickListener{

    private var _binding:FragmentEstablecimientoBinding? = null
    private val binding get() = _binding!!


    private var db = Firebase.firestore
    private lateinit var auth: FirebaseAuth

    var dia = 0
    var mes = 0
    var ano = 0

    var turno = "" //8:45

    var guardarDia = 0
    var guardarMes = 0
    var guardarAno = 0
    var guardarHora = 0
    var guardarMinuto = 0

    var listaServicios:MutableList<String> = mutableListOf()
    private var intervaloCitas =0
    private var apertura = ""
    private var cierre = ""
    private var inicioComida = ""
    private var finComida = ""


    private var tiempoEntreAperturaCierreEnMinutos = 0
    private var tiempoParaComerMinutos = 0
    private var listaTurnos = mutableListOf<Hora>() //Aqui se gurda el listado de turnos del local
    private var listaTurnosOcupados = mutableListOf<String>()
    private var servicioSeleccionado =""
    private var usuarioRealizaCita = ""

    var ubicacion = ""
    var nombrelocal = ""

    private  var idEstablecimiento:String? = ""

    private var listaServicio: MutableList<Servicio> = mutableListOf()
    private var defaultTimeAlarm = 600 // <- * 1000 /60 MINUTOS/ 1H


    private var empleados = mutableListOf<Empleado>()

    private var idEmpleadoSeleccionado = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       _binding = FragmentEstablecimientoBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "all_notifications" // You should create a String resource for this instead of storing in a variable
            val mChannel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "This is default channel used for all other notifications"

            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        createNotificationChannel()
        idEstablecimiento = arguments?.getString("idEstab")

        comprobarSiHayEmpleados()
        cargarListadoEmpleados()

        comprobarFav() //Comprobar si este local esta en los fav del usuario
        cargarImagen()

        auth = Firebase.auth

        sacarUsuarioActual()

        binding.campoUbicacionE.setOnClickListener { lanzarMaps(binding.campoUbicacionE.text.toString()) }

        db.collection("establecimiento").document(idEstablecimiento!!)
            .get()
            .addOnSuccessListener { documento ->

                binding.campoTituloE.text = documento["nombre"].toString()
                binding.campoDescripcionE.text = documento["descripcion"].toString()
                binding.campoEstadoE.text = if (documento["estaAbierto"] as Boolean)"Abierto" else "Cerrado"
                binding.campoHorarioE.text = documento["aperturaHora"].toString()+"/"+documento["cierreHora"].toString()
                binding.campoPublicoE.text = documento["publico"].toString()
                binding.campoUbicacionE.text =  "${documento["provincia"].toString()}, ${documento["municipio"].toString()}, ${documento["calle"].toString()}"

                nombrelocal =  documento["nombre"].toString()
                ubicacion =  "${documento["provincia"].toString()}, ${documento["municipio"].toString()}, ${documento["calle"].toString()}"

            }



        escogerFecha()

        binding.btnPedirCita.setOnClickListener {

            if (servicioSeleccionado != ""){
                if (servicioSeleccionado != "Selecciona servicio" && servicioSeleccionado != "") {
                    if (guardarDia>0 && guardarAno>0 && guardarMes > 0 && turno != ""){
                        establecerCita()
                    }else{
                        Toast.makeText(activity, "Selecciona una fecha y una hora  para tu cita", Toast.LENGTH_SHORT).show()
                    }

                }else{
                    Toast.makeText(activity, "Debes seleccionar un servicio", Toast.LENGTH_SHORT).show()
                }
            }else{
                requireContext().toast("Debes seleccionar un empleado primero")
            }

        }

        binding.btnAgregarFav.setOnClickListener {

            if (binding.btnAgregarFav.isChecked){
                db.collection("usuarios").document(auth.currentUser?.uid.toString())
                    .update("estFavoritos", FieldValue.arrayUnion(idEstablecimiento!!))
                snackbarAgregadoFavoritos()
            }else{
                db.collection("usuarios").document(auth.currentUser?.uid.toString())
                    .update("estFavoritos", FieldValue.arrayRemove(idEstablecimiento!!))
                snackbarQuitarFavoritos()
            }
        }


        binding.btnEscogerServicio.setOnClickListener {

            if (idEmpleadoSeleccionado != ""){
                var alertDialog = Dialog(requireContext(), R.style.DialogTheme)

                var bindingDialog = layoutInflater.inflate(R.layout.lista_servicios, null)
                val adaptador = RecyclerServicios(requireContext(),listaServicio,this, alertDialog)
                bindingDialog.findViewById<RecyclerView>(R.id.liztaServicos).adapter = adaptador
                bindingDialog.findViewById<RecyclerView>(R.id.liztaServicos).layoutManager= LinearLayoutManager(requireContext())


                alertDialog.setContentView(bindingDialog)
                alertDialog.setCancelable(true)
                alertDialog.show()
            }else{
              requireContext().toast("Debes seleccionar un empleado primero.")
            }

        }

    }


    private fun comprobarSiHayEmpleados() {
        db.collection("establecimiento")
            .document(idEstablecimiento.toString())
            .collection("empleados")
            .get()
            .addOnSuccessListener {

                if (!it.isEmpty){
                    binding.ContenedorAcciones.visibility = View.VISIBLE
                }else{
                    binding.ContenedorAcciones.visibility = View.GONE
                }

            }

    }

    private fun cargarListadoEmpleados() {
        db.collection("establecimiento")
            .document(idEstablecimiento.toString())
            .collection("empleados").addSnapshotListener { value, error ->

                empleados = value!!.toObjects(Empleado::class.java)

                binding.listaSeleccionEmpleados.apply {
                    setHasFixedSize(true)

                    var linear = LinearLayoutManager(activity)
                    linear.orientation = LinearLayoutManager.HORIZONTAL
                    layoutManager = linear
                    adapter = RecyclerSeleccionarEmpleado(PanelUsuarioActivity.contexto, empleados,this@EstablecimientoFragment)
                }
            }
    }

    override fun onCajaClick(idUser: String) {
        idEmpleadoSeleccionado = idUser
        listaServicios() //Cargar la lista de servicios del local
        todosLosTurnos() // Cargar los turnos con los que trabaja el local en funcion del intervalo establecido en la configuracion
    }

    private fun listaServicios(){

        db.collection("establecimiento")
            .document(idEstablecimiento!!)
            .collection("empleados")
            .document(idEmpleadoSeleccionado)
            .get()
            .addOnSuccessListener { documento ->
                listaServicio.clear()
                listaServicios.clear()

                listaServicios.addAll(documento["servicio"] as ArrayList<String>)

            }.addOnCompleteListener {
                for (servicio in  listaServicios){
                    listaServicio.add(Servicio(servicio))
                }
            }

    }

    private fun comprobarFav(){
        auth = Firebase.auth

        db.collection("usuarios")
            .document(auth.currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { resultado ->
                binding.btnAgregarFav.isChecked = (resultado["estFavoritos"] as MutableList<String>).contains(idEstablecimiento!!)
                //fallo aqui

            }
    }

    private fun getDateTimeCalendar(){
        val cal = Calendar.getInstance()

        dia = cal.get(Calendar.DAY_OF_MONTH)
        mes = cal.get(Calendar.MONTH)
        ano = cal.get(Calendar.YEAR)

    }

    private fun escogerFecha(){
        binding.btnEscoger.setOnClickListener {
            if (idEmpleadoSeleccionado != ""){
               getDateTimeCalendar()
               DatePickerDialog(requireContext(),R.style.DialogTheme,this,ano,mes,dia).show()
            }else{
                requireContext().toast("Debes seleccionar un empleado primero.")
            }

        }


    }

    override fun onDateSet(view: DatePicker?, ano: Int, mes: Int, diames: Int) {
        guardarDia = diames
        guardarMes = mes+1
        guardarAno = ano
        getDateTimeCalendar()
        alerta_listahoras()



    }

    override fun onTimeSet(view: TimePicker?, hora: Int, minuto: Int) {
        guardarHora =  hora
        guardarMinuto = minuto

        binding.diahora.text = "FECHA SELECCIONADA: $guardarDia/$guardarMes/$guardarAno  $guardarHora:$guardarMinuto"
    }


    private fun establecerCita(){
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
                "userId" to auth.currentUser?.uid,
                "nombre" to usuarioRealizaCita,
                "nombrelocal" to nombrelocal,
                "ubicacion" to ubicacion,
                "idEmpleado" to idEmpleadoSeleccionado
            )).addOnSuccessListener {

                //establecerNoti()
                establecerNotificacion()
                snackbarAnularCita(id.toString())
            }


    }


    //REPASAR
    private fun todosLosTurnos(){

        db.collection("establecimiento")
            .document(idEstablecimiento!!)
            .collection("empleados")
            .document(idEmpleadoSeleccionado)
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
                Toast.makeText(activity, "HA FALLADO COMO UNA CASA",Toast.LENGTH_LONG).show()
            }
    }


    private fun alerta_listahoras(){

        //Falta comprobar la fecha
        db.collection("citas")
            .whereEqualTo("establecimientoId", idEstablecimiento!!)
            .whereEqualTo("idEmpleado", idEmpleadoSeleccionado)
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


    private fun sacarUsuarioActual(){
        db.collection("usuarios")
            .document(auth.currentUser?.uid.toString())
            .get().addOnSuccessListener { documento ->
                usuarioRealizaCita = documento["nombre"].toString()
            }
    }


    private fun cargarImagen(){
        val storage = Firebase.storage("gs://coiffearch.appspot.com/")
        val list = mutableListOf<CarouselItem>()
        val storageRef = storage.reference.child("User")
        val carousel: ImageCarousel = binding.carousel
        carousel.registerLifecycle(lifecycle)


        db.collection("establecimiento")
            .document(idEstablecimiento!!)
            .get()
            .addOnSuccessListener { documento ->
                var listaimagenesexposicion = documento["imagenesExposicion"] as MutableList<String>

                if (listaimagenesexposicion.size.toString().toInt() > 0){
                    for (i in documento["imagenesExposicion"] as MutableList<String>){
                        list.add(
                            CarouselItem(
                                imageUrl = i
                            )
                        )
                        carousel.setData(list)
                    }
                } else{
                    list.add(
                        CarouselItem(
                            imageUrl = "https://firebasestorage.googleapis.com/v0/b/coiffearch.appspot.com/o/imagendefecto.PNG?alt=media&token=5f816536-8d9a-408a-bcaf-736881cbd651"
                        )
                    )
                    carousel.setData(list)
                }
            }
    }

    override fun onCartaClick(idEstab: String, dialog: Dialog) {
        turno = idEstab

        binding.btnEscoger.setText("$turno - $guardarDia/$guardarMes/$guardarAno")

        guardarHora = turno.split(":")[0].toInt()
        guardarMinuto = turno.split(":")[1].toInt()

        dialog.dismiss()
    }


    private fun snackbarAnularCita(id:String){
          var snacbar =  Snackbar.make(binding.root, "Reserva existosa", 4500)
            .setBackgroundTint(Color.parseColor("#3A3379"))
            .setTextColor(Color.parseColor("#FDFDFD"))
            .setActionTextColor(-1)
            .setAction("Deshacer") {
                db.collection("citas").document(id).delete().addOnSuccessListener {
                    val bundle = Bundle()
                    bundle.putString("idEstab", idEstablecimiento)

                    val navController: NavController =
                        requireActivity().findNavController(R.id.nav_host_user)
                    navController.run {
                        popBackStack()
                        navigate(R.id.establecimientoFragment,bundle)
                    }
                }
            }
            .setDuration(6000)

        val view = snacbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snacbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snacbar.show()
    }


    private fun snackbarAgregadoFavoritos(){
        var snackbar = Snackbar.make(binding.root, "Agregado a Favoritos", 2000)
            .setBackgroundTint(Color.parseColor("#FFFF0000"))
            .setTextColor(-1)
            .setActionTextColor(-1)
            .setDuration(1000)

       var view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }


    private fun snackbarQuitarFavoritos(){
       var snackbar = Snackbar.make(binding.root, "Eliminado de Favoritos", 2000)
            .setBackgroundTint(Color.parseColor("#FFFF0000"))
            .setTextColor(-1)
            .setActionTextColor(-1)
            .setDuration(1000)

        var view = snackbar.view
        val params = view.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.TOP
        view.layoutParams = params
        snackbar.animationMode = BaseTransientBottomBar.ANIMATION_MODE_FADE
        snackbar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onServicioClick(servicio: String, dialog:Dialog) {
        servicioSeleccionado = servicio
        binding.btnEscogerServicio.text = servicio
        dialog.dismiss()
    }



    private fun establecerNotificacion(){
        val title = "${binding.btnEscogerServicio.text.toString().split("-")[0]}"
        val message = "Recuerda Cita Programada para: \n $guardarDia/$guardarMes/$guardarAno a las $guardarHora:$guardarMinuto"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel =NotificationChannel("cccc", title, importance)
        channel.description = message

        val notifiactionManager =PanelUsuarioActivity.contexto.applicationContext.getSystemService(NotificationManager::class.java)

        notifiactionManager.createNotificationChannel(channel)

        var calendarioox = Calendar.getInstance()

        calendarioox[Calendar.YEAR] = guardarAno
        calendarioox[Calendar.MONTH] = guardarMes -1
        calendarioox[Calendar.DAY_OF_MONTH] = guardarDia -1
        calendarioox[Calendar.HOUR_OF_DAY] =guardarHora
        calendarioox[Calendar.MINUTE] = guardarMinuto -5
        calendarioox[Calendar.SECOND] =0
        calendarioox[Calendar.MILLISECOND] = 0


        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent

        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, com.example.coiffearch.Notification::class.java).let { intent ->
            intent.putExtra(titleExtra, title)
            intent.putExtra(messageExtra, message)
            PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }


        alarmMgr.setRepeating(
            AlarmManager.RTC_WAKEUP,calendarioox.timeInMillis,
            AlarmManager.INTERVAL_DAY,alarmIntent
        )







    }

    private fun lanzarMaps(direccion: String){
        val gmmIntentUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$direccion")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)

        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)

    }




}