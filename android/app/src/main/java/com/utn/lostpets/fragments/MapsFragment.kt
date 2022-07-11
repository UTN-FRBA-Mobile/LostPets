package com.utn.lostpets.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentMapsBinding

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    lateinit var map: GoogleMap
    private var email: String = ""

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var bottomSheet: View? = null

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): CoordinatorLayout {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableLocation()
        markers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        bottomSheet = activity?.findViewById<View>(R.id.bottom_sheet) as ConstraintLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        setup()
    }

    private fun setup() {

        /* Recupero el mail del usuario */
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        email = sharedPref.getString("email", "email").toString()
        binding.emailUsuario.text = email

        /* Se vuelve a la pantalla de "Login" en caso de cerrarse la sesión */
        binding.logoutButtom.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            /* Obtenemos el valor del email */
            val sharedPref =
                activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            email = sharedPref.getString("email", "email").toString()

            val action = R.id.action_mapsFragment_to_loginFragment
            findNavController().navigate(action)
        }

        /* Navbar */
        binding.navbar.bottomNavigation.selectedItemId = R.id.search
        binding.navbar.bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {

            }
        }
        binding.navbar.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                /* Voy a pantalla de publicaciones */
                R.id.publications -> {
                    val action = R.id.action_mapsFragment_to_publicationsFragment
                    findNavController().navigate(action)
                    true
                }
                /* Voy a pantalla del perfil */
                R.id.profile -> {
                    val action = R.id.action_mapsFragment_to_profileFragment
                    findNavController().navigate(action)
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

    private fun isLocalizationPermissionGranted() = ContextCompat.checkSelfPermission(
        requireActivity(),
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableLocation() {
        if (!::map.isInitialized) return
        if (isLocalizationPermissionGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(
                requireActivity(),
                "Por favor, habilita los permisos para acceder a la localizacion del movil.",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MapsFragment.REQUEST_CODE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MapsFragment.REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Por favor activa la localizacion del movil.",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    private fun markers() {
        val sydney = LatLng(-34.0, 151.0)
        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))

    }
}