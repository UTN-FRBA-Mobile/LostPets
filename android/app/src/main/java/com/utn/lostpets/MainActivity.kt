package com.utn.lostpets

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.utn.lostpets.model.Publication

class MainActivity : AppCompatActivity() {

    var latitude = 2.0
    var longitude = 2.0
    var publication: Publication? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}