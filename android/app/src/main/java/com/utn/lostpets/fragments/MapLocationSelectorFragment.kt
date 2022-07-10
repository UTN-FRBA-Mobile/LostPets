package com.utn.lostpets.fragments


/* las que importo yo */
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.utn.lostpets.MainActivity
import com.utn.lostpets.R
import com.utn.lostpets.databinding.FragmentMapLocationSelectorBinding


class MapLocationSelectorFragment : Fragment(),OnMapReadyCallback {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentMapLocationSelectorBinding? = null
    private val binding get() = _binding!!

    val Fragment.packageManager get() = activity?.packageManager
    var longitude = 0.0
    var latitude = 0.0

    lateinit var mMap: GoogleMap

    // New variables for Current Place Picker
    private val TAG = "MapsActivity"
    var lstPlaces: ListView? = null
    private var mPlacesClient: PlacesClient? = null
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private var mLastKnownLocation: Location? = null

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private val mDefaultLocation = LatLng(-33.8523341, 151.2106085)
    private val DEFAULT_ZOOM = 15
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private var mLocationPermissionGranted = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapLocationSelectorBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true);
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        /*val manager = getFragmentManager()
        val mapFragmentUncast = manager?.findFragmentById(R.id.map)
        val mapFragment = mapFragmentUncast as SupportMapFragment
*/
        val mapFragment = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!

        //val mGoogleMap = (getChildFragmentManager().findFragmentById(R.id.map) as SupportMapFragment).getMap();
        mapFragment.getMapAsync(this)

        val toolbar = activity?.findViewById<View>(R.id.toolbar) as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // Initialize the Places client
        val apiKey = getString(R.string.google_maps_key)
        Places.initialize(activity, apiKey)
        mPlacesClient = Places.createClient(activity)
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((activity as AppCompatActivity))

        // The geographical location where the device is currently located. That is, the last-known
        // location retrieved by the Fused Location Provider.
        val mLastKnownLocation: Location? = null

        setup()
    }


    /**
     * Populates the app bar with the menu.
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.location_picker_menu, menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }


    /**
     * Handles user clicks on menu items.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_geolocate -> {

                // COMMENTED OUT UNTIL WE DEFINE THE METHOD
                // Present the current place picker
                pickCurrentPlace()
                true
            }
            else ->                 // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                super.onOptionsItemSelected(item)
        }
    }


    /**
     * Prompts the user for permission to use the device location.
     */
    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        mLocationPermissionGranted = false
        if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.815717, -58.459093)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        //
        // PASTE THE LINES BELOW THIS COMMENT
        //

        // Enable the zoom controls for the map
        mMap.getUiSettings().isZoomControlsEnabled = true

        // Prompt the user for permission.
        getLocationPermission()
        mMap.setOnMapClickListener(OnMapClickListener { point ->
            val marker = MarkerOptions().position(LatLng(point.latitude, point.longitude))
                .title("New Marker")
            mMap.clear()
            mMap.addMarker(marker)
            println(point.latitude.toString() + "---" + point.longitude)
            this.latitude = point.latitude
            this.longitude = point.longitude
        })
    }


    /**
     * Get the current location of the device, and position the map's camera
     */
    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                val locationResult = mFusedLocationProviderClient!!.lastLocation
                locationResult.addOnSuccessListener(requireActivity(),
                    OnSuccessListener<Location?> { location ->
                        if (location != null) {
                            // Set the map's camera position to the current location of the device.
                            val mLastKnownLocation = location
                            Log.d(TAG,"Latitude: " + mLastKnownLocation.getLatitude())
                            Log.d(TAG,"Longitude: " + mLastKnownLocation.getLongitude())
                            mMap!!.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),
                                    DEFAULT_ZOOM.toFloat()
                                )
                            )
                        } else {
                            Log.d(TAG,"Current location is null. Using defaults.")
                            mMap!!.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM.toFloat()
                                    )
                            )
                        }
                    })
            }
        } catch (e: Exception) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    /**
     * Fetch a list of likely places, and show the current place on the map - provided the user
     * has granted location permission.
     */
    private fun pickCurrentPlace() {
        if (mMap == null) {
            return
        }
        if (mLocationPermissionGranted) {
            getDeviceLocation()
        } else {
            // The user has not granted permission.
            Log.i(TAG,"The user did not grant location permission.")

            // Add a default marker, because the user hasn't selected a place.
            mMap!!.addMarker(
                MarkerOptions()
                    .title(getString(R.string.default_info_title))
                    .position(mDefaultLocation)
                    .snippet(getString(R.string.default_info_snippet))
            )

            // Prompt the user for permission.
            getLocationPermission()
        }
    }

    /**
     * When user taps an item in the Places list, add a marker to the map with the place details
     * ESTO NO DEBERIA SER NECESARIO
     */
   /* private val listClickedHandler =
        OnItemClickListener { parent, v, position, id -> // position will give us the index of which place was selected in the array
            val markerLatLng: LatLng = mLikelyPlaceLatLngs.get(position)
            var markerSnippet: String = mLikelyPlaceAddresses.get(position)
            if (mLikelyPlaceAttributions.get(position) != null) {
                markerSnippet = """
                     $markerSnippet
                     ${mLikelyPlaceAttributions.get(position)}
                     """.trimIndent()
            }

            // Add a marker for the selected place, with an info window
            // showing information about that place.
            mMap!!.addMarker(
                MarkerOptions()
                    .title(mLikelyPlaceNames.get(position))
                    .position(markerLatLng)
                    .snippet(markerSnippet)
            )

            // Position the map's camera at the location of the marker.
            mMap!!.moveCamera(CameraUpdateFactory.newLatLng(markerLatLng))
        }
*/

    private fun setup() {
        binding.confirmarUbicacionButton.setOnClickListener {
            var mainActivity = activity as MainActivity
            mainActivity.latitude = this.latitude
            mainActivity.longitude = this.longitude
            getActivity()?.onBackPressed();
        }
    }
}