package com.example.coiffearch.empresa.empleados

data class Empleado(
    var idempleado:String?,
    var nombre:String?,
    var aperturaHora:String?,
    var cierreHora:String?,
    var finComida:String?,
    var inicioComida:String?,
    var intervaloCitas:String?
    ){

    constructor():this(null,null,null,null,null,null,null)
}