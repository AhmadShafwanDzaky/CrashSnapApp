package com.capstone.crashsnap.data.remote.response

data class NearbyPlaceResponse(
    val results: List<PlaceResult>,
    val status: String
)

data class PlaceResult(
    val name: String,
    val geometry: Geometry
)

data class Geometry(
    val location: Location
)

data class Location(
    val lat: Double,
    val lng: Double
)