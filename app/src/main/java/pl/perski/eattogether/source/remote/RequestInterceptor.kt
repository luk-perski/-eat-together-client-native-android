package pl.perski.eattogether.source.remote

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor(private val apiKey: String) :
    Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val newRequest = request.newBuilder()
            .addHeader("Authorization", apiKey)
            .addHeader(
                "Authorization",
                apiKey
            )
            .addHeader("Content-Type", "application/json")
            .build()
        return chain.proceed(newRequest) //todo add non response error (SocketTimeoutException)
    }
}