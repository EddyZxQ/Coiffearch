package com.example.coiffearch.citasadptador

import com.google.firebase.firestore.Exclude


data class Cita(var accion:String?,
                var fecha: String?,
                var hora: String?,
                var citaId:String?,
                var establecimientoId:String?,
                var nombrelocal:String?,
                var ubicacion:String?,
                var idEmpleado:String?){

    @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String? = null

    constructor():this(null,null, null, null, null,null,null,null)
}