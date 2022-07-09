package com.utn.lostpets.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    lateinit var map: GoogleMap
    private var email: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        email = arguments?.getString("email").toString()
        binding.emailUsuario.text = email
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setup()
    }

    private fun setup() {
        /* Se vuelve a la pantalla de "Login" en caso de cerrarse la sesión */
        binding.logoutButtom.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            // Si el login es con Facebook
            LoginManager.getInstance().logOut()

            val bundle = bundleOf("email" to email)
            val action = R.id.action_mapsFragment_to_loginFragment
            findNavController().navigate(action,bundle)
        }

        /* Navbar */
        binding.navbar.bottomNavigation.selectedItemId = R.id.search;
        binding.navbar.bottomNavigation.setOnItemReselectedListener{ item ->
            when(item.itemId) {

            }
        }
        binding.navbar.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                /* Voy a pantalla de publicaciones */
                R.id.publications -> {
                    val bundle = bundleOf("email" to email)
                    val action = R.id.action_mapsFragment_to_publicationsFragment
                    findNavController().navigate(action,bundle)
                    true
                }
                /* Voy a pantalla del perfil */
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

        /* Acción de ir a "Publicar Encontrado/Perdido" */
        binding.agregarPublicacionButton.setOnClickListener {
            val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
            findNavController().navigate(action)
        }
    }
}