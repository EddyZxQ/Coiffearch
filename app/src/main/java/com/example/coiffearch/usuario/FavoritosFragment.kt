package com.example.coiffearch.usuario

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coiffearch.R
import com.example.coiffearch.databinding.FragmentFavoritosBinding
import com.example.coiffearch.favadaptador.EstabFav
import com.example.coiffearch.favadaptador.RecyclerFav
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception


class FavoritosFragment : Fragment(), RecyclerFav.OnBotonesClickListener{


    private  var _binding :FragmentFavoritosBinding? = null
    private val binding get() = _binding!!


    private var db = Firebase.firestore
    private  var auth = Firebase.auth

    private var listaIdLocalesFavoritos:MutableList<String> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoritosBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        db.collection("usuarios").document(auth.currentUser?.uid.toString()).addSnapshotListener { value, error ->

            for (idLocal in value?.get("estFavoritos") as MutableList<String> )  {
                listaIdLocalesFavoritos.add(idLocal)
            }

            estabs()
        }


    }


    private fun estabs(){

        try{

            db.collection("establecimiento").whereIn("establecimientoId", listaIdLocalesFavoritos).addSnapshotListener { value, error ->


                var establecimientosFav = value!!.toObjects(EstabFav::class.java)


                establecimientosFav.forEachIndexed { index, estabFav ->
                    estabFav.uid = value.documents[index].id
                }


                binding.listaFavoritos.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(activity)
                    adapter = RecyclerFav(PanelUsuarioActivity.contexto, establecimientosFav,this@FavoritosFragment)
                }

            }

        }catch(e: Exception){
            binding.listaFavoritos.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(activity)
                adapter = RecyclerFav(PanelUsuarioActivity.contexto, mutableListOf(),this@FavoritosFragment)
            }
        }
    }


    override fun onEliminarClick(idEstab: String) {
        alerta_eliminar(idEstab)
    }

    private fun alerta_eliminar(idEstab: String){
        var bindingDialog = layoutInflater.inflate(R.layout.alerta_eliminar, null)
        var alertDialog = Dialog(requireContext())

        alertDialog.setContentView(bindingDialog)
        alertDialog.setCancelable(true)
        alertDialog.show()

        bindingDialog.findViewById<TextView>(R.id.alertDescripcion).text = "¿Estas seguro que deseas eliminar el local de tu lista de favoritos?"
        bindingDialog.findViewById<Button>(R.id.btnAceptarAlerta).setOnClickListener {
            db.collection("usuarios").document(auth.currentUser?.uid.toString()).update("estFavoritos" , FieldValue.arrayRemove(idEstab))
            listaIdLocalesFavoritos.remove(idEstab)
            alertDialog.dismiss()
            estabs()
        }

        bindingDialog.findViewById<Button>(R.id.btnCancelarAlerta).setOnClickListener { alertDialog.dismiss()}

    }

    override fun onCartaClick(idEstab: String) {

        val bundle = Bundle()
        bundle.putString("idEstab", idEstab)
        findNavController().navigate(R.id.action_panelfavoritosFragment_to_establecimientoFragment, bundle)

    }




}