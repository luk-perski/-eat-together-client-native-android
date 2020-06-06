package pl.perski.eattogether.source.remote.client

import io.reactivex.Observable
import pl.perski.eattogether.model.AccountModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiClient {

    @POST("login")
    fun signIn(@Body data: AccountModel): Observable<Response<Void>>

}