package com.utn.lostpets.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.utn.lostpets.R
import com.utn.lostpets.adapters.PublicationsProfileAdapter
import com.utn.lostpets.databinding.FragmentProfileBinding
import com.utn.lostpets.interfaces.ApiPublicationsService
import com.utn.lostpets.model.Publication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var email: String = ""

    private val apiUrl = "http://www.mengho.link/publications/"

    /* Adapter para listar publicaciones */
    private lateinit var adapter: PublicationsProfileAdapter
    private val publicacionesFinal = mutableListOf<Publication>()
    private val publicaciones = mutableListOf<Publication>()
    private val publicacionesPerdidos = mutableListOf<Publication>()
    private val publicacionesEncontrados = mutableListOf<Publication>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
        initRecyclerView()
    }

    /* Inicialización Recycler View */
    private fun initRecyclerView() {
        adapter = PublicationsProfileAdapter(publicaciones)
        binding.listaPublicacionesPerfil.layoutManager = LinearLayoutManager(activity)
        binding.listaPublicacionesPerfil.adapter = adapter
        searchByDescripcion("")
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            // TODO: descomentar y eliminar la linea que sigue al comentario
//            .baseUrl("$apiUrl/$email/")
            .baseUrl("$apiUrl/nicoomelnyk@gmail.com/")
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
                for (unaPubli in publications) {
                    var urlExt = unaPubli.foto
                    val call = getRetrofit().create(ApiPublicationsService::class.java).getPublicationsPhotos("$apiUrl" + "photo/$urlExt/")
                    val photo = call.body()
                    if (photo != null) {
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
                        )
                        publicaciones.add(publiFinal)
                    }
                }
            }

            activity?.runOnUiThread {
                if (call.isSuccessful) {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun deletePublication(publication: Publication) {
        if (publication.id != null) {
        }
    }

    private fun setup() {

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                /* Voy a pantalla de publis */
                R.id.publications -> {
                    val bundle = bundleOf("email" to email)
                    val action = R.id.action_mapsFragment_to_publicationsFragment
                    findNavController().navigate(action, bundle)
                    true
                }
                R.id.profile -> {
                    val bundle = bundleOf("email" to email)
                    val action = R.id.action_mapsFragment_to_profileFragment
                    findNavController().navigate(action, bundle)
                    true
                }
                R.id.search -> {
                    true
                }

                else -> false
            }
        }
    }
}
