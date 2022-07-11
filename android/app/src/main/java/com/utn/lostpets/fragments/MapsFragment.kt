package com.utn.lostpets.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
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
import com.utn.lostpets.dialogs.EncontradoPerdidoDialog

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
    ): FrameLayout {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
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

        /* Recupero el mail del usuario */
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        email = sharedPref.getString("email", "email").toString()
        binding.emailUsuario.text = email

        /* Se vuelve a la pantalla de "Login" en caso de cerrarse la sesión */
        binding.logoutButtom.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            /* Obtenemos el valor del email */
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return@setOnClickListener
            email = sharedPref.getString("email", "email").toString()

            val action = R.id.action_mapsFragment_to_loginFragment
            findNavController().navigate(action)
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

        var esPerdido = true
        /* Acción de ir a "Publicar Encontrado/Perdido" */
        binding.agregarPublicacionButton.setOnClickListener {
            val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
            val dialog = EncontradoPerdidoDialog(requireContext())
            dialog.show()
            val perdidoButton = dialog.findViewById(R.id.perdido_button) as Button
            val encontradoButton = dialog.findViewById(R.id.encontrado_button) as Button

            perdidoButton.setOnClickListener {
                val bundle = bundleOf("esPerdido" to true, "textoTitutlo" to "Cargar Perdido", "esEdicion" to false)
                val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
                findNavController().navigate(action, bundle)
                dialog.cancel()
            }

            encontradoButton.setOnClickListener {
                val bundle = bundleOf("esPerdido" to false, "textoTitutlo" to "Cargar Encontrado", "esEdicion" to false)
                val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
                findNavController().navigate(action, bundle)
                dialog.cancel()
            }
            //dialog.cancel()
            //findNavController().navigate(action)
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