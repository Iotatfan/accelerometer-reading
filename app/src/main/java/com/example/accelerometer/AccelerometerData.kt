package com.example.accelerometer

import com.google.gson.annotations.SerializedName

class AccelerometerData {
    @SerializedName("ax") var ax: Double? = null
    @SerializedName("ay") var ay: Double? = null
    @SerializedName("az") var az: Double? = null
    @SerializedName("long") var long: Double? = null
    @SerializedName("lat") var lat: Double? = null
    @SerializedName("activity") var activity: String? = null
}