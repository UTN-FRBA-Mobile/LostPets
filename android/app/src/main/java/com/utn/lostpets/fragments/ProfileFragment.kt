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
import com.utn.lostpets.adapters.PublicationsAdapter
import com.utn.lostpets.databinding.FragmentProfileBinding
import com.utn.lostpets.dataclass.PublicationsResponse
import com.utn.lostpets.interfaces.ApiPublicationsService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.utn.lostpets.model.Publication
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var email: String = ""

    private val apiUrl = "http://www.mengho.link/publications/";

    /* Adapter para listar publicaciones */
    private lateinit var adapter: PublicationsAdapter
    private val publicaciones = mutableListOf<Publication>()

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
        adapter = PublicationsAdapter(publicaciones)
        binding.listaPublicaciones.layoutManager = LinearLayoutManager(activity)
        binding.listaPublicaciones.adapter = adapter
        searchByDescripcion("")
    }

    private fun getRetrofit() : Retrofit {
        return Retrofit.Builder()
            .baseUrl("$apiUrl/$email")
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
                    publicaciones.clear()
                    publicaciones.addAll(publications ?: emptyList())
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun setup() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                /* Voy a pantalla de publis */
                R.id.publications -> {
                    val bundle = bundleOf("email" to email)
                    val action = R.id.action_mapsFragment_to_publicationsFragment
                    findNavController().navigate(action,bundle)
                    true
                }
                R.id.profile -> {
                    val bundle = bundleOf("email" to email)
                    val action = R.id.action_mapsFragment_to_profileFragment
                    findNavController().navigate(action,bundle)
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
