package com.example.accelerometer

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @POST("data")
    fun sendAccData(
        @Field("ax") ax : Float,
        @Field("ay") ay : Float,
        @Field("az") az : Float,
        @Field("long") long : Float,
        @Field("lat") lat : Float
    ) : Call<AccelerometerData>
    @GET("activity")
    fun getActivity() : Call<AccelerometerData>
}