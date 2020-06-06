package pl.perski.eattogether.source.remote.service

import io.reactivex.Observable
import pl.perski.eattogether.model.AddAccountModel
import pl.perski.eattogether.model.UserModel
import pl.perski.eattogether.utils.ApiUtils
import pl.perski.eattogether.source.remote.client.RegisterApiClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

interface IRegisterApiService {
    fun signUp(data: AddAccountModel): Observable<Response<AddAccountModel>>
    fun update(data: UserModel, apiKey: String): Observable<UserModel>
    fun getUser(apiKey: String): Observable<UserModel>
}

class RegisterApiService(private val baseUrl: String = ApiUtils.BASE_URL) :
    IRegisterApiService {
    override fun signUp(data: AddAccountModel): Observable<Response<AddAccountModel>> {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(baseUrl).build().create(RegisterApiClient::class.java).signUp(data)

    }

    override fun update(data: UserModel, apiKey: String): Observable<UserModel> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, false)
            .create(RegisterApiClient::class.java)
            .update(data)
    }

    override fun getUser(apiKey: String): Observable<UserModel> {
        return ApiUtils().getRetrofitWithApiHeader(apiKey, baseUrl, false)
            .create(RegisterApiClient::class.java)
            .getUser()
    }

}