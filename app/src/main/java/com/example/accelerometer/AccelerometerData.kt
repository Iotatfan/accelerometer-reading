package com.example.accelerometer

import com.google.gson.annotations.SerializedName

class AccelerometerData {
    @SerializedName("ax") var ax: ArrayList<Double>? = null
    @SerializedName("ay") var ay: ArrayList<Double>? = null
    @SerializedName("az") var az: ArrayList<Double>? = null
    @SerializedName("long") var long: Double? = null
    @SerializedName("lat") var lat: Double? = null
}