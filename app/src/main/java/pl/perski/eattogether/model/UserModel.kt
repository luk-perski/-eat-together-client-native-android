package pl.perski.eattogether.model

data class UserModel(
    val id: Int? = null,
    val firstName: String,
    val lastName: String,
    val companyName: String,
    val description: String,
    val userLocationLongitude: Double,
    val userLocationLatitude: Double,
    val userLocationAddress: String,
    val distanceRange: Double
)
