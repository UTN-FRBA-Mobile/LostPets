package com.utn.lostpets.dto

class PublicationDTO(var usuario: String,
                  var descripcion: String,
                  var contacto: String,
                  var fecha_publicacion: String,
                  var foto: String,
                  var latitud: Double,
                  var longitud: Double,
                  var es_perdido: Boolean,
                  var activo: Boolean)
