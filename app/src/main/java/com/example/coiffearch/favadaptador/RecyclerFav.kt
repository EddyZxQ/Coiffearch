package com.example.coiffearch.favadaptador

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coiffearch.R

class RecyclerFav(var context: Context, var establecimientosFavoritos: MutableList<EstabFav>, private var itemClickListener: OnBotonesClickListener):
    RecyclerView.Adapter<RecyclerFav.MiHolder>(){


    interface OnBotonesClickListener{
        fun onEliminarClick(idEstab:String)
        fun onCartaClick(idEstab:String)
    }

    inner class MiHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var nEstab: TextView
        var nEstado: TextView
        var nProvincia: TextView
        var nCalle:TextView
        var nHorario:TextView
        var btnEliminar: TextView
        var imgLocal: ImageView
        var cartaEstabFav:CardView


        fun bind(position: Int) {

            //Toda LA CARD
            //itemView.setOnClickListener { itemClickListener.onCartaClick(establecimientosFavoritos[position].idEstab) }

            btnEliminar.setOnClickListener {
                itemClickListener.onEliminarClick(establecimientosFavoritos[position].establecimientoId?:"")
            }

            cartaEstabFav.setOnClickListener {
                itemClickListener.onCartaClick(establecimientosFavoritos[position].establecimientoId?:"")
            }

            if (establecimientosFavoritos[position].fotoPortada != ""){
                Glide.with(context).load(establecimientosFavoritos[position].fotoPortada).into(imgLocal)
            }else{
                Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/coiffearch.appspot.com/o/imagendefecto.PNG?alt=media&token=5f816536-8d9a-408a-bcaf-736881cbd651").into(imgLocal)
            }
        }

        init {
            nEstab = itemView.findViewById(R.id.nEstab)
            nEstado = itemView.findViewById(R.id.nEstado)
            nProvincia = itemView.findViewById(R.id.nProvincia)
            nCalle = itemView.findViewById(R.id.nCalle)
            nHorario = itemView.findViewById(R.id.nHorario)
            btnEliminar = itemView.findViewById(R.id.btnEliminarListaFav)
            imgLocal = itemView.findViewById(R.id.imgLocalFav)
            cartaEstabFav = itemView.findViewById(R.id.cartaEstabFav)

            // btnEliminar.setOnClickListener { Toast.makeText(context, "$campoAccion", Toast.LENGTH_LONG).show() }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiHolder {
       var itemView = LayoutInflater.from(context).inflate(R.layout.estabfav_item, parent, false)
        return MiHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiHolder, position: Int) {
        var estab =establecimientosFavoritos[position]
        holder.nEstab.text = estab.nombre
        holder.nEstado.text = if (estab.estado == true) "Abierto" else "Cerrado"
        holder.nHorario.text = estab.horario
        holder.nProvincia.text = "${estab.provincia}"
        holder.nCalle.text = "${estab.municipio}, ${estab.calle}"
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return  establecimientosFavoritos.size
    }
}