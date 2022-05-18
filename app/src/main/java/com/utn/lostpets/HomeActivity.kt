package com.utn.lostpets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.utn.lostpets.databinding.ActivityHomeBinding
import com.utn.lostpets.databinding.ActivityMainBinding

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var binding: ActivityHomeBinding
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        setup(email ?: "")
    }

    private fun setup(email: String) {
        title = "Inicio"

        binding.logoutButtom.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            onBackPressed()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
    }
}