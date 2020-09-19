package com.example.skillbranch.pressure

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import io.flutter.plugin.common.EventChannel

internal class StreamHandlerImpl(private val sensorManager: SensorManager, sensorType: Int) : EventChannel.StreamHandler {
    private var sensorEventListener: SensorEventListener? = null
    private var sensor: Sensor = sensorManager.getDefaultSensor(sensorType)

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        sensorEventListener = createSensorEventListener(events)
        sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onCancel(arguments: Any?) {
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun createSensorEventListener(events: EventChannel.EventSink?): SensorEventListener {
        return object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null) {
                    events?.success(event.values[0].toDouble())
                }
            }
        }
    }

}