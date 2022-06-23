package com.example.coiffearch.adaptadorCalendario


import com.example.coiffearch.R



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerCitasCalendario(private val context: Context,
                    val citas: MutableList<CitaCalendario>,
                    private var itemClickListener: OnBotonesClickListener2
): RecyclerView.Adapter<RecyclerCitasCalendario.MiHolder>() {

    interface OnBotonesClickListener2{
        fun onCartaClick(idEstab:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiHolder {
        val itemView= LayoutInflater.from(context).inflate(R.layout.card_cita,parent,false)
        return MiHolder(itemView)

    }

    override fun onBindViewHolder(holder: MiHolder, position: Int) {
        val cit= citas[position]
        holder.idNombre.setText(cit.nombreCliente)
        holder.ctiempoCita.setText(cit.tiempoCita)
        holder.cservicio.setText(cit.servicio)
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return citas.size
    }


    inner class MiHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        var idNombre: TextView
        var ctiempoCita: TextView
        var cservicio: TextView



        init {

            idNombre = itemView.findViewById(R.id.cNombre)
            ctiempoCita= itemView.findViewById(R.id.cHora)
            cservicio= itemView.findViewById(R.id.cServicio)

        }

        fun bind(position: Int){

            //A lo mejor hay que pasar dos id

            itemView.setOnClickListener {
                itemClickListener.onCartaClick(citas[position].idEstablecimiento)
            }




        }

    }
}