package com.mazharulsabbir.smsbroadcastreceiver.data.retrofit


import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "RetrofitBuilder"

object RetrofitBuilder {
    private const val baseUrl: String = "https://jsonplaceholder.typicode.com"

    private fun getRetrofit(token: String?): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val client: OkHttpClient = OkHttpClient.Builder()
            .readTimeout(2, TimeUnit.MINUTES)
            .addInterceptor { chain: Interceptor.Chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("Accept", "application/json")
                    .build()

                chain.proceed(newRequest)
            }
            .build()

        val instance = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create()) //important
            .addConverterFactory(GsonConverterFactory.create(gson))

        return instance.build()
    }

    fun getApiService(token: String? = null): SmsReceiverApiService = getRetrofit(token)
        .create(SmsReceiverApiService::class.java)
}