package com.mazharulsabbir.smsbroadcastreceiver.data.retrofit

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface SmsReceiverApiService {

    @GET("/posts/1")
    suspend fun getPosts(): Response<Any>

}