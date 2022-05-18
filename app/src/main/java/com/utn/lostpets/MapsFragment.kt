package com.utn.lostpets

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
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
        //title = "Inicio"
        binding.logoutButtom.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val bundle = bundleOf("email" to email)
            val action = R.id.action_mapsFragment_to_loginFragment
            findNavController().navigate(action,bundle)
        }
    }
}