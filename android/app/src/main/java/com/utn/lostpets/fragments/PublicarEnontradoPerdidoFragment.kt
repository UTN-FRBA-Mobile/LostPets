package com.utn.lostpets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.utn.lostpets.databinding.FragmentPublicarEnontradoPerdidoBinding
import com.utn.lostpets.dto.PublicationDTO
import com.utn.lostpets.interfaces.ApiPublicationsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PublicarEnontradoPerdidoFragment : Fragment() {

    private var _binding: FragmentPublicarEnontradoPerdidoBinding? = null
    private val binding get() = _binding!!

    private val apiUrl = "http://www.mengho.link/publications/publicacion/";

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
                var publiFinal = PublicationDTO(
                    "carlos@gmail.com",
                    "Primera Prueba",
                    "Tel 123456789",
                    "2022-06-03 12:00:00",
                    "https://t1.ea.ltmcdn.com/es/posts/7/9/5/por_que_las_gatas_se_comen_a_sus_gatitos_recien_nacidos_22597_600.jpg",
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
    }

}

/*
    "usuario":"nicoomelnyk@gmail.com",
    "descripcion":"Animal perdido",
    "contacto":"Tel 123456789",
    "fecha_publicacion":"2022-06-03 12:00:00",
    "foto":"https://t1.ea.ltmcdn.com/es/posts/7/9/5/por_que_las_gatas_se_comen_a_sus_gatitos_recien_nacidos_22597_600.jpg",
    "latitud":-34.639757,
    "longitud":-58.452142,
    "es_perdido":true,
    "activo":true

 */