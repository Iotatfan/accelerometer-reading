package com.example.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.TextView
import com.github.mikephil.charting.charts.LineChart
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
    private lateinit var lineChart : LineChart
    private lateinit var apiInterface: ApiInterface
    private lateinit var ax : ArrayList<Double>
    private lateinit var ay : ArrayList<Double>
    private lateinit var az : ArrayList<Double>

    private var handler: Handler = Handler()
    private var accData : AccelerometerData = AccelerometerData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.record_btn)
        btnExport = findViewById(R.id.export_btn)
        lineChart = findViewById(R.id.line_chart)

        ax = ArrayList();  ay = ArrayList(); az = ArrayList()

        btnRecord.text = "Start"
        btnExport.text = "Stop"

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

//                    tvAcc = findViewById(R.id.tv_accelerometer)
//
//                    var normalZ : Float = event.values[2]-(0.8*9.8+(1-0.8)*event.values[2]).toFloat()
//
//                    tvAcc.text = "Accelerometer" +
//                            "\n X : " + event.values[0].toString() +
//                            "\n Y : " + event.values[1].toString() +
//                            "\n Z : " + normalZ.toString()
//                    val x = event.values[0]
//                    val y = event.values[1]
//                    val z = normalZ
//
                }
            }
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
        Log.d("Data", accelerometerData.ax.toString())
        val call : Call<AccelerometerData> = apiInterface.sendAccData(accelerometerData)
        call.enqueue(object  : Callback<AccelerometerData> {
            override fun onResponse(call: Call<AccelerometerData>, response: Response<AccelerometerData>) {
                if (response.isSuccessful) {
                    Log.d("Post Success", "Data Sent")
                } else {
                    Log.d("Post Failed", "Failed to Add Data")
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

    private val sendTask = object : Runnable {
        override fun run() {
            accData.ax = ax
            accData.ay = ay
            accData.az = az
            accData.long = 1.1
            accData.lat = 1.1

            sendData(accData)
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
