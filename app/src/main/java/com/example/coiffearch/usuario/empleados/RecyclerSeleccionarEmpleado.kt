package com.example.coiffearch.usuario.empleados

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.coiffearch.R
import com.example.coiffearch.empresa.empleados.Empleado

class RecyclerSeleccionarEmpleado(var contexto: Context, var empleados:MutableList<Empleado>, var listener:OnItemEmpleadoClickListener):
    RecyclerView.Adapter<RecyclerSeleccionarEmpleado.EmpleadoHolder>() {
    interface OnItemEmpleadoClickListener{
        fun onCajaClick(idUser:String)
    }


    inner class EmpleadoHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var campoNombre: TextView = itemView.findViewById(R.id.nombreEmpleado)

        fun bind(position: Int){ itemView.setOnClickListener { listener.onCajaClick(empleados[position].idempleado.toString()) } }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmpleadoHolder {
        var itemView = LayoutInflater.from(contexto).inflate(R.layout.item_empleado,parent, false)
        return EmpleadoHolder(itemView)
    }

    override fun onBindViewHolder(holder: EmpleadoHolder, position: Int) {
        var empleado = empleados[position]
        holder.campoNombre.text = empleado.nombre
        holder.bind(position)
    }

    override fun getItemCount(): Int = empleados.size

}