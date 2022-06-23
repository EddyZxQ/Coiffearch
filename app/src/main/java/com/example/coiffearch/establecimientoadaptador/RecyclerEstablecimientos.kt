package com.example.coiffearch.establecimientoadaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coiffearch.R

class RecyclerEstablecimientos(
    private var context: Context,
    var establecimientos: MutableList<Establecimiento>,
    private var itemClickListener:OnCajaClickListener
    ):RecyclerView.Adapter<RecyclerEstablecimientos.MiHolder> (){


    interface OnCajaClickListener{
        fun onItemClick(idEstab:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiHolder {
       var itemView = LayoutInflater.from(context).inflate(R.layout.item_establecimiento, parent, false)
        return  MiHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiHolder, position: Int) {
        var establecimiento = establecimientos[position]
        holder.campoNombre.text = establecimiento.nombre
        holder.campoProvincia.text = establecimiento.provincia
        holder.campoCalle.text = establecimiento.municipio + ", "+establecimiento.calle
        holder.campoHorario.text = establecimiento.horario
        holder.campoEstado.text =  if (establecimiento.estado== true) "Abierto" else "Cerrado"



        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return  establecimientos.size
    }

    inner class MiHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(position: Int) {
            itemView.setOnClickListener {
                itemClickListener.onItemClick(establecimientos[position].uid?:"")
            }

            if (establecimientos[position].fotoPortada != ""){
                Glide.with(context).load(establecimientos[position].fotoPortada).into(itemView.findViewById(R.id.imgEstablecimiento))
            }else{
                Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/coiffearch.appspot.com/o/imagendefecto.PNG?alt=media&token=5f816536-8d9a-408a-bcaf-736881cbd651").into(itemView.findViewById(R.id.imgEstablecimiento))
            }
        }


        var campoNombre: TextView
        var campoProvincia: TextView
        var campoHorario: TextView
        var campoEstado: TextView
        var campoCalle: TextView


        init {
            campoNombre = itemView.findViewById(R.id.campoTitulo)
            campoProvincia = itemView.findViewById(R.id.campoProvincia)
            campoCalle = itemView.findViewById(R.id.campoCalle)
            campoHorario = itemView.findViewById(R.id.campoHorario)
            campoEstado = itemView.findViewById(R.id.campoEstado)



        }


    }


}