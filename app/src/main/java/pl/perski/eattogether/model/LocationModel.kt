package pl.perski.eattogether.model

data class LocationModel(
    var longitude: Double,
    var latitude: Double,
    var address: String = ""
)
