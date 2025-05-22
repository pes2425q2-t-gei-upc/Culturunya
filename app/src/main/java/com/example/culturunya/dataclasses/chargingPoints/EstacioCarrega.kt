package com.example.culturunya.dataclasses.chargingPoints

data class EstacioCarrega(
    val ciutat: String,
    val direccio: String,
    val gestio: String,
    val id_punt: String,
    val lat: Double,
    val lng: Double,
    val nplaces: String,
    val potencia: Int,
    val provincia: String,
    val tipus_acces: String,
    val tipus_carregador: List<String>,
    val tipus_velocitat: List<String>
)