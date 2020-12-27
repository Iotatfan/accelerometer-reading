package com.example.accelerometer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val baseUrl : String = "http://192.168.1.2:8000/"               // API address & port

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var writer: FileWriter
    private lateinit var tvAcc : TextView
    private lateinit var btnRecord : TextView
    private lateinit var btnExport : TextView
    private lateinit var apiInterface: ApiInterface
    private lateinit var ax : ArrayList<Double>
    private lateinit var ay : ArrayList<Double>
    private lateinit var az : ArrayList<Double>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var myLat : Double? = null
    private var myLong : Double? = null
    private var handler: Handler = Handler()
    private var accData : AccelerometerData = AccelerometerData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.record_btn)
        btnExport = findViewById(R.id.export_btn)

        ax = ArrayList();  ay = ArrayList(); az = ArrayList()

        btnRecord.text = "Start"
        btnExport.text = "Stop"

        checkPermission()

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)


        btnRecord.setOnClickListener {
//            Initialize Interface, Enable This When API is ready
            apiInterface = getClient().create(ApiInterface::class.java)
            Log.d("API", apiInterface.toString())

//            Initialize Sensor
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            val timestamp = Calendar.getInstance().timeInMillis
            val formatter = SimpleDateFormat("dd_MM_yyyy-hh:mm:ss")
            val time = formatter.format(timestamp)

            Log.d("Time", time.toString())

            sendTask.run()
            retrieveTask.run()
        }

        btnExport.setOnClickListener {
//            writer.close()
            mSensorManager.unregisterListener(this)
            handler.removeCallbacks(sendTask)
            handler.removeCallbacks(retrieveTask)
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null ) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    ax.add(event.values[0].toDouble())
                    ay.add(event.values[1].toDouble())
                    az.add(event.values[2].toDouble())
                }
            }
        }
    }

    private fun checkPermission() {
        val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        Log.d("Permission", "Permission Granted")
                    } else {
                        Log.d("Permission", "Permission Denied")
                    }
                }
    }

    private fun getLocation() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 500
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations){
                    myLong = location.longitude
                    myLat = location.latitude
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        } else {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper())
        }

    }


    private fun getActivityData() {
        val call : Call<String> = apiInterface.getActivity()
        Log.d("Get", call.toString())
        call.enqueue(object  : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                Log.d("Get Success", response.body().toString())
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.d("Get Failed", t.toString())
            }

        })
    }

    private fun sendData(accelerometerData: AccelerometerData) {
        Log.d("Data", accelerometerData.ax?.size.toString())
        val call : Call<AccelerometerData> = apiInterface.sendAccData(accelerometerData)
        call.enqueue(object  : Callback<AccelerometerData> {
            override fun onResponse(call: Call<AccelerometerData>, response: Response<AccelerometerData>) {
                if (response.isSuccessful) {
                    Log.d("Post Success", "Data Sent")
                } else {
                    Log.d("Post Failed", "Failed to Send Data")
                }
            }
            override fun onFailure(call: Call<AccelerometerData>, t: Throwable) {
                Log.d("Post Failed", t.toString())
            }

        })
    }

    private fun getClient (): Retrofit {

        val gson : Gson = GsonBuilder()
                .setLenient()
                .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun clearArray () {
        ay.clear()
        ax.clear()
        az.clear()
    }

    private val sendTask = object : Runnable {
        override fun run() {
            accData.ax = ax
            accData.ay = ay
            accData.az = az
            accData.long = myLong
            accData.lat = myLat

            getLocation()
            sendData(accData)
            clearArray()
            handler.postDelayed(this, 1000)
        }
    }

    private val retrieveTask = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 1000)
            getActivityData()
        }
    }
}
