
package com.utn.lostpets.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LocationViewModel: ViewModel() {
    val latitude =  MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()
}