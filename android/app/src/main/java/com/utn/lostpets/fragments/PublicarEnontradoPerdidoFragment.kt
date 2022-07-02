package com.utn.lostpets.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64.encodeToString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.utn.lostpets.databinding.FragmentPublicarEnontradoPerdidoBinding
import com.utn.lostpets.dto.PublicationDTO
import com.utn.lostpets.interfaces.ApiPublicationsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class PublicarEnontradoPerdidoFragment : Fragment() {

    private var _binding: FragmentPublicarEnontradoPerdidoBinding? = null
    private val binding get() = _binding!!

    private val apiUrl = "http://www.mengho.link/publications/publicacion/";
    private var publicationImage: Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicarEnontradoPerdidoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun showError() {
        Toast.makeText(activity, "Ha ocurrido un error al solicitar las publicaciones", Toast.LENGTH_SHORT).show()
    }

    private fun showHola(algo : String) {
        Toast.makeText(activity, algo, Toast.LENGTH_SHORT).show()
    }

    private fun setup() {
        /* Se vuelve a la pantalla de "Login" en caso de cerrarse la sesión */

        binding.publicarButton.setOnClickListener {
            /* Creamos un hilo secundario para solicitar las publicaciones y sus respectivas fotos */
            CoroutineScope(Dispatchers.IO).launch {
                var descripcion = binding.descripcionEditText.text.toString();
                var contacto = binding.contactoEditText.text.toString();
                val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                val fecha = sdf.format(Date())
                var imageBitmapString = bitMapToString(publicationImage!!)

                var publiFinal = PublicationDTO(
                    "carlos@gmail.com",
                    descripcion,
                    contacto,
                    fecha,
                    imageBitmapString!!,
                    -34.639757,
                    -58.452142,
                    true,
                    true
                );
                //binding.loader.progressBar.visibility = View.VISIBLE

                /* Solicitamos las fotos */
                val call = getRetrofit().create(ApiPublicationsService::class.java).postPublications("$apiUrl",publiFinal)
                val publications = call.body()


                activity?.runOnUiThread {
                    if(call.isSuccessful) {
                        showHola("Api retorno Ok")
                    } else {
                        showHola("Api retorno error")
                    }
                    //binding.loader.progressBar.visibility = View.GONE


                }
            }

        }

        binding.cargarImagenButton.setOnClickListener{
            dispatchTakePictureIntent()
        }
    }

    val REQUEST_CODE = 42

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity()!!.packageManager) != null){
            startActivityForResult(takePictureIntent,REQUEST_CODE)
        } else{
            //Toast.makeText(this,"Unable to open camera",Toast.LENGTH_SHORT)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            publicationImage = imageBitmap
            binding.cargarImagenImageView.setImageBitmap(imageBitmap)
        }
    }

    fun bitMapToString(bitmap: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b: ByteArray = baos.toByteArray()
        return Base64.getEncoder().encodeToString(b)
    }
}
