package com.utn.lostpets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.utn.lostpets.adapters.PublicationsAdapter
import com.utn.lostpets.databinding.FragmentPublicationsBinding
import com.utn.lostpets.dataclass.PublicationsPhotosResponse
import com.utn.lostpets.dataclass.PublicationsResponse
import com.utn.lostpets.interfaces.ApiPublicationsService
import com.utn.lostpets.model.Publication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PublicationsFragment : Fragment() {

    private var _binding: FragmentPublicationsBinding? = null
    private val binding get() = _binding!!

    private val apiUrl = "http://www.mengho.link/publications/";

    /* Adapter para listar publicaciones */
    private lateinit var adapter: PublicationsAdapter
    private val publicacionesFinal = mutableListOf<Publication>()
    private val publicaciones = mutableListOf<Publication>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPublicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    /* Inicialización Recycler View */
    private fun initRecyclerView() {
        adapter = PublicationsAdapter(publicacionesFinal)
        binding.listaPublicaciones.layoutManager = LinearLayoutManager(activity)
        binding.listaPublicaciones.adapter = adapter
        searchByDescripcion("")
    }

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByDescripcion(query: String) {
        /* Creamos un hilo secundario para solicitar las publicaciones y sus respectivas fotos */
        CoroutineScope(Dispatchers.IO).launch {

            binding.loader.progressBar.visibility = View.VISIBLE

            /* Solicitamos las fotos */
            val call = getRetrofit().create(ApiPublicationsService::class.java).getPublications("$apiUrl")
            val publications = call.body()

            /* Por publicación solicitamos sus fotos */
            if (publications != null) {
                for(unaPubli in publications) {
                    var urlExt = unaPubli.foto;
                    val call = getRetrofit().create(ApiPublicationsService::class.java).getPublicationsPhotos("$apiUrl" + "photo/$urlExt/")
                    val photo = call.body()
                    if(photo != null) {
                        var publiFinal = Publication(
                            unaPubli.id,
                            unaPubli.usuario,
                            unaPubli.descripcion,
                            unaPubli.contacto,
                            unaPubli.fechaPublicacion,
                            photo.foto,
                            unaPubli.latitud,
                            unaPubli.longitud,
                            unaPubli.esPerdido,
                            unaPubli.activo
                        );
                        publicaciones.add(publiFinal);
                    }
                }
            }

            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    publicacionesFinal.clear()
                    publicacionesFinal.addAll(publicaciones ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
                binding.loader.progressBar.visibility = View.GONE
            }
        }
    }

    private fun showError() {
        Toast.makeText(activity, "Ha ocurrido un error al solicitar las publicaciones", Toast.LENGTH_SHORT).show()
    }

    /* Avisa cuando el usuario pone un cambio en el buscador
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(!query.isNullOrEmpty()) {
            searchByDescripcion(query)
        }
        return true
    }*/

    /* No nos interesa
    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    } */
}