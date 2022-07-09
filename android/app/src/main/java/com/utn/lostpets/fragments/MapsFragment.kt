package com.utn.lostpets.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.facebook.login.LoginManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    lateinit var map: GoogleMap
    private var email: String = ""

    companion object {
        const val REQUEST_CODE_LOCATION = 0;
    }

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
        enableLocation()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
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
                    true
                }
                R.id.search -> {
                    true
                }

                else -> false
            }
        }
    }

    private fun isLocalizationPermissionGranted() = ContextCompat.checkSelfPermission(requireActivity(),
        android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation(){
        if(!::map.isInitialized) return
        if(isLocalizationPermissionGranted()){
            map.isMyLocationEnabled = true;
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(
                requireActivity(),
                "Por favor, habilita los permisos para acceder a la localizacion del movil.",
                Toast.LENGTH_SHORT
            ).show()
        }else{
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MapsFragment.REQUEST_CODE_LOCATION
            );
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MapsFragment.REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true;
            }else {
                Toast.makeText(
                    requireActivity(),
                    "Por favor activa la localizacion del movil.",
                    Toast.LENGTH_SHORT
                ).show();
            }
            else -> {}
        }
    }
}