package com.example.coiffearch.empresa.empleados

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R

class RecyclerEmpleados(var contexto: Context, var empleados:MutableList<Empleado>,var listener:OnItemEmpleadoClickListener):RecyclerView.Adapter<RecyclerEmpleados.EmpleadoHolder>() {
    interface OnItemEmpleadoClickListener{
        fun onBtnEliminarClick(idUser:String)
    }


    inner class EmpleadoHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var campoNombre: TextView = itemView.findViewById(R.id.idNombreEmpleado)
        var btnEliminarEmpleado : ImageView = itemView.findViewById(R.id.imgBorrar)


        fun bind(position: Int){
            btnEliminarEmpleado.setOnClickListener { listener.onBtnEliminarClick(empleados[position].idempleado.toString())}
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoHolder {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.card_empleado,parent, false)
        return EmpleadoHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmpleadoHolder, position: Int) {
       var empleado = empleados[position]
        holder.campoNombre.text = empleado.nombre
        holder.bind(position)
    }

    override fun getItemCount(): Int = empleados.size

}