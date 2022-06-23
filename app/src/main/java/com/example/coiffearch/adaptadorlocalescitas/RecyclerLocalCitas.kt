package com.example.coiffearch.adaptadorlocalescitas

import com.example.coiffearch.R



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class RecyclerLocalCitas(private val context: Context,
                    val locales: MutableList<LocalCitas>,
                    private var itemClickListener: OnBotonesClickListener2
): RecyclerView.Adapter<RecyclerLocalCitas.MiHolder>() {

    interface OnBotonesClickListener2{
        fun onCartaClick(idEstab:String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiHolder {
        val itemView= LayoutInflater.from(context).inflate(R.layout.card_local_selector,parent,false)
        return MiHolder(itemView)

    }

    override fun onBindViewHolder(holder: MiHolder, position: Int) {
        val loc= locales[position]
        holder.idNombre.setText(loc.nombreLocal)
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return locales.size
    }


    inner class MiHolder(itemView: View): RecyclerView.ViewHolder(itemView){


        var imgLocal: ImageView
        var idNombre: TextView



        init {

            imgLocal= itemView.findViewById(R.id.imgLocalC)
            idNombre = itemView.findViewById(R.id.idNombreLocalC)


        }

        fun bind(position: Int){


            itemView.setOnClickListener {
                itemClickListener.onCartaClick(locales[position].id)
            }


            if (locales[position].imgUrl.isNotEmpty()){
                Glide.with(context).load(locales[position].imgUrl).into(imgLocal)
            }else{
                Glide.with(context).load("https://firebasestorage.googleapis.com/v0/b/coiffearch.appspot.com/o/imagendefecto.PNG?alt=media&token=5f816536-8d9a-408a-bcaf-736881cbd651").into(imgLocal)
            }

        }

    }
}