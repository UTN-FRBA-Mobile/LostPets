package com.utn.lostpets.model

class Publication(var id: Integer,
                  var usuario: String,
                  var descripcion: String,
                  var contacto: String,
                  var fechaPublicacion: String,
                  var foto: String,
                  var latitud: Double,
                  var longitud: Double,
                  var esPerdido: Boolean,
                  var activo: Boolean)