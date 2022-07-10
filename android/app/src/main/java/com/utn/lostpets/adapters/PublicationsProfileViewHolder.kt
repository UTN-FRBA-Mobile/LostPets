package com.utn.lostpets.adapters

import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
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
        }
        if (publication.esPerdido) {
            binding.encontrado.text = "No encontrado"
        } else {
            binding.encontrado.text = "Encontrado!"
        }
        binding.editButton.setOnClickListener {
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

                binding.publiActiva.text = "Inactiva /"
            }
        }
    }
}
