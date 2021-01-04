package com.example.accelerometer

import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
    @POST("getactivity")
    fun sendAccData(
            @Body accelerometerData: AccelerometerData
    ) : Call<String>
    @POST("data")
    fun sendLocActivity(
            @Body activityLocation: ActivityLocation
    ) : Call<String>
}