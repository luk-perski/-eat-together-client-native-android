package pl.perski.eattogether.utils

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import pl.perski.eattogether.source.remote.RequestInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class ApiUtils {
    companion object {
//        const val BASE_URL: String = "http://192.168.43.171:2501/api/v1/"
        const val BASE_URL: String = "http://192.168.1.14:2501/api/v1/"
    }

    private var gson = GsonBuilder()
        .setLenient()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        .create()

    fun getRetrofitWithApiHeader(
        apiKey: String,
        baseUrl: String,
        isStringResponse: Boolean
    ): Retrofit {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(RequestInterceptor(apiKey))
            .build()
        val converterFactory =
            if (isStringResponse) ScalarsConverterFactory.create() else GsonConverterFactory.create(
                gson
            )

        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(converterFactory)
            .client(client)
            .baseUrl(baseUrl).build()
    }
}