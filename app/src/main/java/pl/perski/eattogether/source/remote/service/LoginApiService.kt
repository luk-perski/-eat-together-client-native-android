package pl.perski.eattogether.source.remote.service

import io.reactivex.Observable
import pl.perski.eattogether.model.AccountModel
import pl.perski.eattogether.source.remote.client.LoginApiClient
import pl.perski.eattogether.utils.ApiUtils
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface ILoginApiService {
    fun signIn(data: AccountModel): Observable<Response<Void>>
}

class LoginApiService(private val baseUrl: String = ApiUtils.BASE_URL) :
    ILoginApiService {
    override fun signIn(data: AccountModel): Observable<Response<Void>> {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl).build()

        val postsApi = retrofit.create(LoginApiClient::class.java)
        return postsApi.signIn(data)
    }

}