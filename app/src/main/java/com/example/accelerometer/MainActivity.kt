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
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val baseUrl : String = "URL_HERE"

    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var writer: FileWriter
    private lateinit var tvAcc : TextView
    private lateinit var btnRecord : TextView
    private lateinit var btnExport : TextView
    private lateinit var lineChart : LineChart
    private lateinit var apiInterface: ApiInterface
    private var handler: Handler = Handler()
    private lateinit var retrofit : Retrofit
    private lateinit var ax : ArrayList<Float>
    private lateinit var ay : ArrayList<Float>
    private lateinit var az : ArrayList<Float>

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
//            apiInterface = getClient().create(apiInterface::class.java)

//            Initialize Sensor

            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            val timestamp = Calendar.getInstance().timeInMillis
            val formatter = SimpleDateFormat("dd_MM_yyyy-hh:mm:ss")
            val time = formatter.format(timestamp)

            Log.d("Time", time.toString())

            sendTask.run()

//            val filename = "Accelerometer_" + time.toString() + ".csv"
//
//            val path = getExternalFilesDir(null)
//            val file = path.toString() + '/' + filename
//            writer = FileWriter(file, true)

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
                    ax.add(event.values[0])
                    ay.add(event.values[1])
                    az.add(event.values[2])

                    Log.d("ACCX", ax.toString())

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
//                    writer.write(event.values[0].toString() + ',' +
//                            event.values[1].toString() + ',' +
//                            normalZ.toString() + '\n')
                }
            }
        }
    }

    private fun getActivityData() {
        val call : Call<AccelerometerData> = this.apiInterface.getActivity()
    }

    private fun sendData(ax: Double, ay: Double, az: Double, long: Double, lat: Double) {
        Log.d("Runnable", "Task Running")
//        var call : Call<AccelerometerData> = apiInterface.sendAccData(ax,ay,az,long,lat)
    }

    private fun getClient (): Retrofit {
        this.retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return this.retrofit
    }

    private val sendTask = object : Runnable {
        override fun run() {
            sendData(1.0,1.0,1.0,1.0,1.0)
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
