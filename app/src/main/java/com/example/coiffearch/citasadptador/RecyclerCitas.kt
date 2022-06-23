package com.example.coiffearch.citasadptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R

class RecyclerCitas(private val context: Context, val citas:MutableList<Cita>, private var itemClickListener: OnItemRecyclerClickListener):RecyclerView.Adapter<RecyclerCitas.MiHolder>() {

    interface OnItemRecyclerClickListener{
        fun onItemClick(idCita:String)
        fun onDireccionClick(ubicacion:String)
        fun onEliminarClick(idCita:String)
        fun onItemClick2(idCita:String,view:View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerCitas.MiHolder {
        var itemView = LayoutInflater.from(context).inflate(R.layout.item_cita, parent, false)
        return MiHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerCitas.MiHolder, position: Int) {
        var cita =citas[position]

        holder.campoAccion.text = cita.accion
        holder.campoHora.text = cita.hora
        holder.campoFecha.text = cita.fecha
        holder.campoNombre.text = cita.nombrelocal
        holder.campoUbi.text = cita.ubicacion
        holder.bind(position)

    }

    override fun getItemCount(): Int {
        return citas.size
    }

    inner class MiHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        var campoAccion:TextView
        var campoHora:TextView
        var btnEliminar: TextView
        var campoFecha:TextView
        lateinit var id:String
        var expandableLayout: LinearLayout

        var campoNombre: TextView
        var campoUbi:TextView

        var citaItem: RelativeLayout


        fun bind(position: Int) {
            btnEliminar = itemView.findViewById(R.id.btnEliminar)

            btnEliminar.setOnClickListener {
                itemClickListener.onItemClick(citas[position].citaId?:"")
                itemClickListener.onEliminarClick(citas[position].citaId?:"")
            }

            citaItem.setOnClickListener { itemClickListener.onItemClick2(citas[position].citaId?:"", itemView) }

            campoUbi.setOnClickListener { itemClickListener.onDireccionClick(citas[position].ubicacion?:"") }
        }

        init {
            campoAccion = itemView.findViewById(R.id.campoAccion)
            campoHora = itemView.findViewById(R.id.campoHora)
            campoFecha = itemView.findViewById(R.id.campoFecha)
            btnEliminar = itemView.findViewById(R.id.btnEliminar)

            campoNombre= itemView.findViewById(R.id.campoNombreEstab)
            campoUbi =itemView.findViewById(R.id.campoUbicacionEstab)
            expandableLayout = itemView.findViewById(R.id.item_desc_expandible)
            citaItem = itemView.findViewById(R.id.item_cita_layout)

        }


    }


}