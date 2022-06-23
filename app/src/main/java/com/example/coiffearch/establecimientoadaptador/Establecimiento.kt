package com.example.coiffearch.establecimientoadaptador

import com.google.firebase.firestore.Exclude

data class Establecimiento (var nombre:String?,
                            var provincia:String?,
                            var municipio:String?,
                            var calle:String?,
                            var horario:String?,
                            var estado:Boolean?,
                            var idEstablecimiento:String?,
                            var imagenesExposicion:ArrayList<String>?,
                            var fotoPortada:String?){


    @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String? = null

    constructor():this(null, null, null,null,null, null, null, null,null)
}



