package com.example.coiffearch.utiles

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.coiffearch.R

class ProgressBarBtn(context: Context, view: View) {

    var layout = view.findViewById<CardView>(R.id.btnCargando)
    var texto = view.findViewById<TextView>(R.id.btntexto)
    var cargando  = view.findViewById<ProgressBar>(R.id.cargando)

    fun cargar(){
        cargando.visibility = View.VISIBLE
        texto.visibility = View.INVISIBLE
    }

    fun cancel(){
        cargando.visibility = View.INVISIBLE
        texto.visibility = View.VISIBLE
    }

    fun texto(btntexto:String){
        texto.text = btntexto
        texto.visibility = View.VISIBLE
        cargando.visibility = View.INVISIBLE
    }
}