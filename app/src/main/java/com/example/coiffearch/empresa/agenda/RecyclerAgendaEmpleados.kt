package com.example.coiffearch.empresa.agenda

import com.example.coiffearch.empresa.empleados.Empleado
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R

class RecyclerAgendaEmpleados(var contexto: Context, var empleados:MutableList<Empleado>, var listener:OnItemEmpleadoClickListener):RecyclerView.Adapter<RecyclerAgendaEmpleados.EmpleadoHolder>() {
    interface OnItemEmpleadoClickListener{
        fun onCajaClick(idUser:String)
    }


    inner class EmpleadoHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var campoNombre: TextView = itemView.findViewById(R.id.idNombreEmpleado)


        fun bind(position: Int){
            itemView.setOnClickListener { listener.onCajaClick(empleados[position].idempleado.toString())}
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoHolder {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.item_seleccionar_empleado,parent, false)
        return EmpleadoHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmpleadoHolder, position: Int) {
        var empleado = empleados[position]
        holder.campoNombre.text = empleado.nombre
        holder.bind(position)
    }

    override fun getItemCount(): Int = empleados.size

}