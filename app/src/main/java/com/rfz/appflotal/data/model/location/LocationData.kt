package com.rfz.appflotal.data.model.location

data class LocationData(
    val lat:          Double,
    val lng:          Double,
    val pais:         String?,
    val estado:       String?,   // adminArea
    val municipio:    String?,   // subAdminArea
    val ciudad:       String?,   // locality
    val colonia:      String?,   // subLocality
    val codigoPostal: String?
)