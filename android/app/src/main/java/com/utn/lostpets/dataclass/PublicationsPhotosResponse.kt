package com.utn.lostpets.dataclass

import com.google.gson.annotations.SerializedName

data class PublicationsPhotosResponse(@SerializedName("id") var id: Integer,
                                      @SerializedName("foto") var foto: String)
