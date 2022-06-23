package com.example.coiffearch.adaptadorDiasMes

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.empresa.agenda.AgendaFragment


class RecyclerCalendario(
    private var contexto:Context,
    private var listaDeDias:MutableList<Calendario>,
    private  var itemClickListener: CalendarioClickListener
):RecyclerView.Adapter<RecyclerCalendario.HolderCalendario>()
{

    private var selectedItemPosition: Int = AgendaFragment.diaHoy


    var seleccion = 0

    interface CalendarioClickListener{
        fun onCartaClick(dia:String,diaTexto:String, mes:String, ano:String)
    }

    inner class HolderCalendario(itemView:View):RecyclerView.ViewHolder(itemView){

        var fieldDia: TextView
        var fieldDiaTexto: TextView


        init {

            fieldDia = itemView.findViewById(R.id.fieldDia)
            fieldDiaTexto = itemView.findViewById(R.id.fieldDiaTexto)

        }

        @SuppressLint("NotifyDataSetChanged")
        fun bind (position:Int){

            itemView.setOnClickListener {

                selectedItemPosition = position

                itemClickListener.onCartaClick(listaDeDias[position].dia,listaDeDias[position].diaTexto,listaDeDias[position].mes, listaDeDias[position].ano )

            }
        }




    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCalendario {
        val itemView =LayoutInflater.from(contexto).inflate(R.layout.item_calendario, parent,false)
        return HolderCalendario(itemView)
    }

    override fun onBindViewHolder(holder: HolderCalendario, position: Int) {
        val listDias = listaDeDias[position]
        holder.fieldDia.text = listDias.dia
        holder.fieldDiaTexto.text = listDias.diaTexto
        holder.bind(position)







        if(selectedItemPosition == position){
            holder.itemView.performClick()
            holder.itemView.backgroundTintList = contexto.resources.getColorStateList(R.color.btn_toggle, contexto.theme)
            holder.fieldDia.setTextColor(Color.WHITE)
            holder.fieldDiaTexto.setTextColor(Color.WHITE)
        }else{
            holder.itemView.backgroundTintList = contexto.resources.getColorStateList(R.color.btn_untoggle, contexto.theme)
            holder.fieldDia.setTextColor(Color.BLACK)
            holder.fieldDiaTexto.setTextColor(Color.BLACK)
        }


    // holder.itemView.setBackgroundColor(Color.parseColor("#E49B83"))


    }

    override fun getItemCount(): Int {
       return listaDeDias.size
    }


}