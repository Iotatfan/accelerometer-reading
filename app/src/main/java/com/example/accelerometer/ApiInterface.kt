package com.example.accelerometer

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("data")
    fun sendAccData(
            @Body accelerometerData: AccelerometerData
    ) : Call<String>
    @GET("activity")
    fun getActivity() : Call<String>
}