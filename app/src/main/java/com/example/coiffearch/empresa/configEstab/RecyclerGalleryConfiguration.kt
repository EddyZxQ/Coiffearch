package com.example.coiffearch.empresa.configEstab

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coiffearch.R

class RecyclerGalleryConfiguration(var contexto:Context, var galleria:MutableList<String>, var itemListener: onImageClickListener):RecyclerView.Adapter<RecyclerGalleryConfiguration.GalleryHolder>() {

    interface onImageClickListener{
        fun itemImageClick(image: Uri)
        fun itemEliminarBtnClick(fotourl: String)
    }

    inner class GalleryHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var  imagen: ImageView = itemView.findViewById(R.id.item_image)
        var btnEliminar : ImageView = itemView.findViewById(R.id.btnEliminar)


        fun bind(posicion: Int){

            btnEliminar.setOnClickListener {
                itemListener.itemEliminarBtnClick(galleria[posicion])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
        var itemview = LayoutInflater.from(contexto).inflate(R.layout.item_foto, parent, false)
        return  GalleryHolder(itemview)

    }

    override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
        val img = galleria[position]
        Glide.with(contexto).load(img).into(holder.imagen)
        holder.bind(position)
    }

    override fun getItemCount(): Int = galleria.size


}