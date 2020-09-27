package pl.perski.eattogether.model

import java.io.Serializable
import java.util.*

data class EventModel(
    val id: Int? = null,
    val creatorAccountId: Int? = null,
    val date: Date,
    val placeName: String,
    val placeLocation: String,
    val locationLongitude: Double,
    val locationLatitude: Double,
    val description: String? = null,
    val creatorName: String? = null,
    val callerJoin: Boolean? = null,
    val callerIsCreator: Boolean? = null,
    val participants: String? = null
) : Serializable