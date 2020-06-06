package pl.perski.eattogether.source.remote.client

import io.reactivex.Observable
import pl.perski.eattogether.model.EventModel
import retrofit2.http.*

interface EventApiClient {

    @GET("events/current")
    fun getEvents(): Observable<List<EventModel>>

    @PUT("events/join")
    fun joinToEvent(@Query("eventId") eventId: String): Observable<String>

    @DELETE("events/deactivate")
    fun deleteEvent(@Query("eventId") eventId: String): Observable<String>

    @DELETE("events/left")
    fun leftFromEvent(@Query("eventId") eventId: String): Observable<String>

    @POST("events")
    fun addEvent(@Body eventData: EventModel): Observable<EventModel>
}