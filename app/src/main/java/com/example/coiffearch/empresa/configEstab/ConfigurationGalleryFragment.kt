package com.example.coiffearch.empresa.configEstab

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coiffearch.databinding.FragmentConfigurationGalleryBinding
import com.example.coiffearch.utiles.toast
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*


class ConfigurationGalleryFragment : Fragment(), RecyclerGalleryConfiguration.onImageClickListener {


    private var _binding: FragmentConfigurationGalleryBinding? = null
    private val binding get() = _binding!!

    private var db = Firebase.firestore
    private var idEstab:String? = null

    var listaImagenes: MutableList<Gallery> = mutableListOf()
    var listaUrl:MutableList<String> = mutableListOf()

    private  var responseLauncer = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ activityResult ->

        for (i in 0 until activityResult.data?.clipData?.itemCount!!){
          // listaImagenes.add(Gallery(activityResult.data?.clipData?.getItemAt(i)?.uri!!))
           subirFotoLocal(activityResult.data?.clipData?.getItemAt(i)?.uri!!, UUID.randomUUID().toString())

        }

        setUp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConfigurationGalleryBinding.inflate(inflater, container, false)
        return binding.root
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idEstab = arguments?.getString("idEstab")
        listarElementos()

        binding.btnAgregarFotos.setOnClickListener { cogerVariasFotos() }
        binding.btnActualizarImagesGaleria.setOnClickListener { actualizarGalleria() }


    }

    private fun actualizarGalleria() {
        db.collection("establecimiento").document(idEstab!!).update("imagenesExposicion",listaUrl).addOnCompleteListener {
            activity?.onBackPressed()
            requireActivity().toast("Galleria actualizada correctamente")
        }
    }

    private fun  cogerVariasFotos(){
        var cogerVariasFotos = Intent(Intent.ACTION_PICK)
        cogerVariasFotos.type = "image/*"
        cogerVariasFotos.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)


        responseLauncer.launch(cogerVariasFotos)
    }


    private fun setUp() {
        binding.galleriaImagenes.layoutManager = GridLayoutManager(activity, 3, LinearLayoutManager.VERTICAL, false)
        binding.galleriaImagenes.setHasFixedSize(true)
        binding.galleriaImagenes.adapter = RecyclerGalleryConfiguration(activity?.baseContext!!, listaUrl,this)

    }

    override fun itemImageClick(image: Uri) {

    }

    override fun itemEliminarBtnClick(fotourl: String) {
        db.collection("establecimiento").document(idEstab.toString()).update("imagenesExposicion",FieldValue.arrayRemove(fotourl))
        var imagen :StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fotourl)
        listaImagenes.remove(Gallery(null, fotourl))
        imagen.delete().addOnCompleteListener { listarElementos() }
    }


    fun subirFotoLocal(mUri: Uri, idLocal:String){
        val folder: StorageReference = FirebaseStorage.getInstance().reference.child("imagesLocales").child(idEstab!!)
        val fileName: StorageReference = folder.child(idLocal)
        fileName.putFile(mUri).addOnSuccessListener {
            fileName.downloadUrl.addOnSuccessListener { url ->
                listaUrl.add(url.toString())
                db.collection("establecimiento").document(idEstab!!).update("imagenesExposicion",listaUrl).addOnCompleteListener {
                    setUp()
                }
            }
        }



    }


    private  fun listarElementos(){
        db.collection("establecimiento").document(idEstab!!).get().addOnSuccessListener {
            listaUrl = it.data?.get("imagenesExposicion") as MutableList<String>
        }.addOnCompleteListener {
            for ( item in listaUrl)  listaImagenes.add(Gallery(null, item))
        }.addOnCompleteListener {
            setUp()
        }

    }





}