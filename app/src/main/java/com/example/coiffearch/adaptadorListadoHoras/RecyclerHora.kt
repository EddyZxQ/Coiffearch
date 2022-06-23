package com.example.coiffearch.adaptadorListadoHoras

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.adaptadorCalendario.CitaCalendario




class RecyclerHora(private val context: Context,
                              val horas: MutableList<Hora>,
                              private var itemClickListener: OnBotonesClickListener2,
                              private var  dialog: Dialog,
                              private var listaTurnosOcupados:MutableList<String>
): RecyclerView.Adapter<RecyclerHora.MiHolder>() {

    interface OnBotonesClickListener2{
        fun onCartaClick(idEstab:String, dialog: Dialog)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiHolder {
        val itemView= LayoutInflater.from(context).inflate(R.layout.card_hora,parent,false)
        return MiHolder(itemView)

    }

    override fun onBindViewHolder(holder: MiHolder, position: Int) {
        val hor= horas[position]
        holder.hora.setText(hor.hora)
        

        if (holder.hora.text.toString() in listaTurnosOcupados){
           // horas.removeAt(position)
           // holder.hora.text = ""
            //holder.hora.visibility = View.GONE-
            holder.hora.text = listaTurnosOcupados.find { it == holder.hora.text.toString()}+" OCUPADO"
            holder.hora.visibility = View.GONE
        }

        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return horas.size
    }


    inner class MiHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        var hora: TextView
       // var estadoReserva: TextView

        init {
            hora = itemView.findViewById(R.id.cHoraTurno)
            //estadoReserva = itemView.findViewById(R.id.estadoReserva)
        }

        fun bind(position: Int){
            itemView.setOnClickListener {
                itemClickListener.onCartaClick(horas[position].hora,dialog)
            }

        }

    }
}