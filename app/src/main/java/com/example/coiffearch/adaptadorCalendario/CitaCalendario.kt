package com.example.coiffearch.adaptadorCalendario

data class CitaCalendario(
    var idEstablecimiento:String,
    var idCita:String,
    var idPropietario:String,
    var nombreCliente:String,
    var tiempoCita:String,
    var dia:String,
    var mes:String,
    var ano:String,
    var servicio:String)