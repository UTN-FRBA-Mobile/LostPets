package com.utn.lostpets.fragments

import android.Manifest
import android.R.attr
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.utn.lostpets.MainActivity
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentPublicarEnontradoPerdidoBinding
import com.utn.lostpets.dto.PublicationDTO
import com.utn.lostpets.interfaces.ApiPublicationsService
import com.utn.lostpets.interfaces.LocationDataPass
import com.utn.lostpets.model.LocationViewModel
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

    val Fragment.packageManager get() = activity?.packageManager

    public var longitude = 0.0;
    public var latitude = 0.0;

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


    val CAMERA_REQUEST_CODE = 42
    val GALLERY_REQUEST_CODE = 13
    val GALLERY_PERMISSION_CODE = 1001;

    private val viewModel: LocationViewModel by activityViewModels()

    private fun setup() {

        /* Se vuelve a la pantalla de "Login" en caso de cerrarse la sesión */

        binding.publicarButton.setOnClickListener {
            /* Creamos un hilo secundario para solicitar las publicaciones y sus respectivas fotos */
            CoroutineScope(Dispatchers.IO).launch {
                var mainActivity = activity as MainActivity
                latitude = mainActivity.latitude
                longitude = mainActivity.longitude
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
                    latitude,
                    longitude,
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

        binding.cargarImagenGaleriaButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, GALLERY_PERMISSION_CODE)
                } else{
                    chooseImageGallery();
                }
            }else{
                chooseImageGallery();
            }
        }

        /* Acción de ir a "Cargar Ubicacion" */
        binding.cargarUbicacionButton.setOnClickListener {
            var clase = this.javaClass.name
            val bundle = bundleOf("latitude" to latitude,"longitude" to longitude)
            val action = R.id.action_publicarEnontradoPerdidoFragment_to_mapLocationSelectorFragment
            findNavController().navigate(action,bundle)
        }
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity()!!.packageManager) != null){
            startActivityForResult(takePictureIntent,CAMERA_REQUEST_CODE)
        } else{
            //Toast.makeText(this,"Unable to open camera",Toast.LENGTH_SHORT)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE  && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            publicationImage = imageBitmap
            binding.cargarImagenImageView.setImageBitmap(imageBitmap)
        }
        if  (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageData = data?.data
            val imageUri: Uri = data!!.getData()!!
            val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            GALLERY_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageGallery()
                }else{
                    Toast.makeText(requireActivity(),"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(publicationImage != null){
            binding.cargarImagenImageView.setImageBitmap(publicationImage)
        }
    }

}
