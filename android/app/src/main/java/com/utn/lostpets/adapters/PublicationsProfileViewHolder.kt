package com.utn.lostpets.adapters

import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.utn.lostpets.MainActivity
import com.utn.lostpets.R
import com.utn.lostpets.databinding.ItemPublicacionProfileBinding
import com.utn.lostpets.dto.PublicationDEL
import com.utn.lostpets.interfaces.ApiPublicationsService
import com.utn.lostpets.model.Publication
import com.utn.lostpets.utils.FechaCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class PublicationsProfileViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val laView = view
    private val binding = ItemPublicacionProfileBinding.bind(view)
    private val resources = getApplicationContext().getResources()
//    private val deleteButton: AppCompatButton = itemView.findViewById<AppCompatButton>(R.id.deleteButton)
//    private val editButton: AppCompatButton = itemView.findViewById<AppCompatButton>(R.id.editButton)
    private fun getRetrofit(apiUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(publication: Publication, posicion: Int) {
        binding.idDescripcion.text = publication.descripcion
        binding.idContacto.text = publication.contacto
        binding.idFecha.text = FechaCalculator.calcularDistancia(publication.fechaPublicacion)
        /* Insertamos como imagen el base64 */
        val decodedString: ByteArray = Base64.decode(publication.foto, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        binding.idPublicacion.setImageBitmap(decodedByte)

        if (publication.activo) {
            binding.publiActiva.text = "Activa /"
        } else {
            binding.publiActiva.text = "Inactiva /"
            binding.deleteButton.visibility = View.GONE
            binding.editButton.visibility = View.GONE
        }
        if (publication.esPerdido) {
            binding.encontrado.text = "Perdido"
        } else {
            binding.encontrado.text = "Encontrado"
        }
        binding.editButton.setOnClickListener {
            val bundle = bundleOf("esPerdido" to true, "textoTitutlo" to "Editar Publicación","esEdicion" to true)
            var fragmento = laView.findFragment() as Fragment
            var mainActivity =  fragmento.activity as MainActivity
            mainActivity.publication = publication
            val action = R.id.action_profileFragment_to_publicarEnontradoPerdidoFragment
            findNavController(laView.findFragment()).navigate(action, bundle)

        }
        binding.deleteButton.setOnClickListener {
            var apiUrl = "http://www.mengho.link/publications/publicacion/baja/"
            MainScope().launch {
                var id = publication.id
                var activo = false

                var publiAEliminar = PublicationDEL(
                    id,
                    activo
                )
                // binding.loader.progressBar.visibility = View.VISIBLE

                val call = getRetrofit(apiUrl).create(ApiPublicationsService::class.java).delPublication("$apiUrl", publiAEliminar)

                var fragmento = laView.findFragment() as Fragment
                var mainActivity =  fragmento.activity as MainActivity
                mainActivity?.runOnUiThread {
                    if(call.isSuccessful) {
                        showMensaje("Publicación eliminada satisfactoriamente", mainActivity)
                    } else {
                        showMensaje("Hubo un error, vuelva a internar mas tarde", mainActivity)
                    }
                    //binding.loader.progressBar.visibility = View.GONE
                }

                binding.publiActiva.text = "Inactiva /"

                binding.deleteButton.visibility = View.GONE
                binding.editButton.visibility = View.GONE
            }
        }
    }

    private fun showMensaje(algo : String, mainActivity : MainActivity) {
        Toast.makeText(mainActivity, algo, Toast.LENGTH_SHORT).show()
    }
}
