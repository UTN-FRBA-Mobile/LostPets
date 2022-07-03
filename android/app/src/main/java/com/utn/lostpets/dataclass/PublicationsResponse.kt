package com.utn.lostpets.dataclass

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class PublicationsResponse(@SerializedName("id") var id: Integer,
                                @SerializedName("usuario") var usuario: String,
                                @SerializedName("descripcion") var descripcion: String,
                                @SerializedName("contacto") var contacto: String,
                                @SerializedName("fechaPublicacion") var fechaPublicacion: String,
                                @SerializedName("foto") var foto: String,
                                @SerializedName("latitud") var latitud: Double,
                                @SerializedName("longitud") var longitud: Double,
                                @SerializedName("esPerdido") var esPerdido: Boolean,
                                @SerializedName("activo") var activo: Boolean)