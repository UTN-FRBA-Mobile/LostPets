package com.utn.lostpets.fragments

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentMapsBinding
import com.utn.lostpets.dataclass.PublicationsResponse
import com.utn.lostpets.dialogs.EncontradoPerdidoDialog
import com.utn.lostpets.interfaces.ApiPublicationsService
import com.utn.lostpets.utils.FechaCalculator
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    lateinit var map: GoogleMap
    private var email: String = ""
    private val apiUrl = "http://www.mengho.link/publications/"
    private val final_publications = mutableListOf<PublicationsResponse>()

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

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
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            this.childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        var bottomSheet1 = activity?.findViewById(R.id.bottom_sheet) as ConstraintLayout
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet1)
        setBottomSheetVisibility(false)
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

        var esPerdido = true
        /* Acción de ir a "Publicar Encontrado/Perdido" */
        binding.agregarPublicacionButton.setOnClickListener {
            val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
            val dialog = EncontradoPerdidoDialog(requireContext())
            dialog.show()
            val perdidoButton = dialog.findViewById(R.id.perdido_button) as Button
            val encontradoButton = dialog.findViewById(R.id.encontrado_button) as Button

            perdidoButton.setOnClickListener {
                val bundle = bundleOf(
                    "esPerdido" to true,
                    "textoTitutlo" to "Cargar Perdido",
                    "esEdicion" to false
                )
                val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
                findNavController().navigate(action, bundle)
                dialog.cancel()
            }

            encontradoButton.setOnClickListener {
                val bundle = bundleOf(
                    "esPerdido" to false,
                    "textoTitutlo" to "Cargar Encontrado",
                    "esEdicion" to false
                )
                val action = R.id.action_mapsFragment_to_publicarEnontradoPerdidoFragment
                findNavController().navigate(action, bundle)
                dialog.cancel()
            }
        }

        /* Cargando markers */

        loadingLostAnimals()
    }

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun loadingLostAnimals() {
        MainScope().launch {
            var call: Response<List<PublicationsResponse>>

            call = getRetrofit().create(ApiPublicationsService::class.java).getPublications(apiUrl)

            val publications = call.body()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    if (publications != null) {
                        final_publications.clear()
                        final_publications.addAll(publications)
                        publications.map { publicacion -> markers(publicacion) }
                    }
                } else {
                    showError()
                }
            }
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

    private fun markers(publication: PublicationsResponse) {
        map.addMarker(
            MarkerOptions().position(LatLng(publication.latitud, publication.longitud))
                .title(publication.id.toString()).icon(BitmapDescriptorFactory.defaultMarker(getMarkerColor(publication.esPerdido)))
        )
        map.setOnMarkerClickListener { marker ->
            onMarkerClicked(marker)
            false
        }

    }

    private fun onMarkerClicked(marker: Marker) {
        var bottomSheet = activity?.findViewById(R.id.bottom_sheet) as ConstraintLayout
        var publication = final_publications.get(marker.title.toInt() - 1)
        MainScope().launch {
            var urlExt = publication.foto
            val call = getRetrofit().create(ApiPublicationsService::class.java)
                .getPublicationsPhotos("$apiUrl" + "photo/$urlExt/")
            val photo = call.body()

            activity?.runOnUiThread {
                if (call.isSuccessful) {
                    val decodedString: ByteArray = Base64.decode(photo?.foto, Base64.DEFAULT)
                    val decodedByte =
                        BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    bottomSheet.findViewById<ImageView>(R.id.photo_id).setImageBitmap(decodedByte)
                } else {
                    showError()
                }
            }


        }
        if(publication.esPerdido){
            marker.title = "Animal perdido"
        }else {
            marker.title = "Animal encontrado"
        }
        bottomSheet.findViewById<TextView>(R.id.description).text = publication.descripcion
        bottomSheet.findViewById<TextView>(R.id.contact).text = publication.contacto
        bottomSheet.findViewById<TextView>(R.id.lost_time).text =
            FechaCalculator.calcularDistancia(publication.fechaPublicacion)

        setBottomSheetVisibility(true)
    }

    private fun setBottomSheetVisibility(isVisible: Boolean) {
        val updatedState =
            if (isVisible) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.state = updatedState
    }

    private fun showError() {
        Toast.makeText(
            activity,
            "Ha ocurrido un error al solicitar las publicaciones",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getMarkerColor(isLost: Boolean): Float {
        if(isLost)
            return BitmapDescriptorFactory.HUE_RED;
        else
            return BitmapDescriptorFactory.HUE_GREEN;
    }

}