package pl.perski.eattogether.source.remote.client

import io.reactivex.Observable
import pl.perski.eattogether.model.AddAccountModel
import pl.perski.eattogether.model.UserModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface RegisterApiClient {

    @POST("accounts/sign-up")
    fun signUp(@Body data: AddAccountModel): Observable<Response<AddAccountModel>>

    @PATCH("users")
    fun update(@Body data: UserModel): Observable<UserModel>

    @GET("users/account")
    fun getUser(): Observable<UserModel>

}