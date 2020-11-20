package com.example.accelerometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), SensorEventListener {
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor
    private lateinit var mGyroscope: Sensor
    private lateinit var writer: FileWriter
    private lateinit var tvAcc : TextView
    private lateinit var btnRecord : TextView
    private lateinit var btnExport : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRecord = findViewById(R.id.record_btn)
        btnExport = findViewById(R.id.export_btn)

        btnRecord.text = "Record"
        btnExport.text = "Stop"

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)


        btnRecord.setOnClickListener {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
            val timestamp = Calendar.getInstance().timeInMillis
            val formatter = SimpleDateFormat("dd_MM_yyyy-hh:mm:ss")
            val time = formatter.format(timestamp)

            Log.d("Time", time.toString())

            val filename = "Accelerometer_" + time.toString() + ".csv"

            val path = getExternalFilesDir(null)
            val file = path.toString() + '/' + filename
            writer = FileWriter(file, true)
        }

        btnExport.setOnClickListener {
            writer.close()
            mSensorManager.unregisterListener(this)
        }
    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event != null ) {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    tvAcc = findViewById(R.id.tv_accelerometer)

                    var normalZ : Float = event.values[2]-(0.8*9.8+(1-0.8)*event.values[2]).toFloat()

                    tvAcc.text = "Accelerometer" +
                            "\n X : " + event.values[0].toString() +
                            "\n Y : " + event.values[1].toString() +
                            "\n Z : " + normalZ.toString()
                    val x = event.values[0]
                    val y = event.values[1]
                    val z = normalZ

                    writer.write(event.values[0].toString() + ',' +
                            event.values[1].toString() + ',' +
                            normalZ.toString() + '\n')
                }
            }
        }
    }
}
