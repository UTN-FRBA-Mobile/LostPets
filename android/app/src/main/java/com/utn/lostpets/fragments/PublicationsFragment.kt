package com.utn.lostpets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.utn.lostpets.adapters.PublicationsAdapter
import com.utn.lostpets.databinding.FragmentPublicationsBinding
import com.utn.lostpets.dataclass.PublicationsResponse
import com.utn.lostpets.interfaces.ApiPublicationsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PublicationsFragment : Fragment() {

    private var _binding: FragmentPublicationsBinding? = null
    private val binding get() = _binding!!

    /* Adapter para listar publicaciones */
    private lateinit var adapter: PublicationsAdapter
    private val publicationsImages = mutableListOf<PublicationsResponse>()

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
        adapter = PublicationsAdapter(publicationsImages)
        binding.listaPublicaciones.layoutManager = LinearLayoutManager(activity)
        binding.listaPublicaciones.adapter = adapter
        searchByDescripcion("")
    }

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://www.mengho.link/publications/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun searchByDescripcion(query: String) {
        /* Creamos un hilo secundario para solicitar las publicaciones*/
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRetrofit().create(ApiPublicationsService::class.java).getPublications("http://www.mengho.link/publications/")
            val publications = call.body()
            activity?.runOnUiThread {
                if(call.isSuccessful) {
                    publicationsImages.clear()
                    publicationsImages.addAll(publications ?: emptyList())
                    adapter.notifyDataSetChanged()
                } else {
                    showError()
                }
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