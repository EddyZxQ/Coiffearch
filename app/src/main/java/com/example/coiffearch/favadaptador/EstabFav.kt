package com.example.coiffearch.favadaptador

import com.google.firebase.firestore.Exclude

data class EstabFav (var establecimientoId:String?,
                     var nombre:String?,
                     var estado:Boolean?,
                     var provincia:String?,
                     var municipio:String?,
                     var calle:String?,
                     var aperturaHora:String?,
                     var cierreHora:String?,
                     var horario:String?,
                     var imagenesExposicion:MutableList<String>?,
                     var fotoPortada:String?)
{

    @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String? = null

    constructor():this(null, null, null, null, null, null, null, null, null, null,null)
}