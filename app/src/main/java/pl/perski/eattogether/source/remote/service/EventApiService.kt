package pl.perski.eattogether.source.remote.service

import io.reactivex.Observable
import pl.perski.eattogether.model.EventModel
import pl.perski.eattogether.source.remote.client.EventApiClient
import pl.perski.eattogether.utils.ApiUtils

interface IEventApiService {
    fun getEvents(apiKey: String): Observable<List<EventModel>>
    fun jointToEvent(eventId: Int, apiKey: String): Observable<String>
    fun leftFromEvent(eventId: Int, apiKey: String): Observable<String>
    fun deleteEvent(eventId: Int, apiKey: String): Observable<String>
    fun addEvent(data: EventModel, apiKey: String): Observable<EventModel>
}

class EventApiService(private val baseUrl: String = ApiUtils.BASE_URL) : IEventApiService {

    override fun getEvents(apiKey: String): Observable<List<EventModel>> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, false)
            .create(EventApiClient::class.java)
            .getEvents()

    }

    override fun jointToEvent(eventId: Int, apiKey: String): Observable<String> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, true)
            .create(EventApiClient::class.java)
            .joinToEvent(eventId.toString())
    }

    override fun leftFromEvent(eventId: Int, apiKey: String): Observable<String> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, true)
            .create(EventApiClient::class.java)
            .leftFromEvent(eventId.toString())
    }

    override fun deleteEvent(eventId: Int, apiKey: String): Observable<String> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, true)
            .create(EventApiClient::class.java)
            .deleteEvent(eventId.toString())
    }

    override fun addEvent(data: EventModel, apiKey: String): Observable<EventModel> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, false)
            .create(EventApiClient::class.java)
            .addEvent(data)
    }

}