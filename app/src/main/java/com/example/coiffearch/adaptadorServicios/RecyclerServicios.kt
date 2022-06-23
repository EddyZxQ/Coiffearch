package com.example.coiffearch.adaptadorServicios

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R

class RecyclerServicios(var contexto: Context, var listaServicios:MutableList<Servicio>, var itemListener:onServicioClickListener, private var  dialog: Dialog):RecyclerView.Adapter<RecyclerServicios.ServiciosHolder>() {

    interface onServicioClickListener{
        fun onServicioClick(servicio:String,dialog:Dialog)
    }


    inner class ServiciosHolder(itemview: View):RecyclerView.ViewHolder(itemview){

        var servicio: TextView = itemview.findViewById(R.id.textoServicio)

        var cartaServicio: CardView = itemview.findViewById(R.id.cartaServicio)

        fun bind(servicio:String, dialog:Dialog){ cartaServicio.setOnClickListener { itemListener.onServicioClick(servicio,dialog) } }


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiciosHolder {
        var itemview = LayoutInflater.from(contexto).inflate(R.layout.item_servicio,parent, false)
        return ServiciosHolder(itemview)
    }

    override fun onBindViewHolder(holder: ServiciosHolder, position: Int) {
       var servicio = listaServicios[position].servicio

        holder.servicio.text = servicio
        holder.bind(servicio, dialog)
    }

    override fun getItemCount(): Int {
       return listaServicios.size
    }


}