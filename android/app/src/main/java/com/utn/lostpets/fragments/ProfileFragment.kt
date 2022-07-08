package com.utn.lostpets.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.R
import com.utn.lostpets.adapters.PublicationsAdapter
import com.utn.lostpets.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

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
//        adapter = PublicationsAdapter(publicationsImages)
//        binding.listaPublicaciones.layoutManager = LinearLayoutManager(activity)
//        binding.listaPublicaciones.adapter = adapter
//        searchByDescripcion("")
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
