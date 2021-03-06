package com.example.skillbranch.pressure

import android.app.Activity
import com.google.gson.Gson
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar


/** PressurePlugin */
public class PressurePluginOld: FlutterPlugin, MethodCallHandler, SensorEventListener, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var mSensorManager: SensorManager
  private lateinit var pressure : Sensor
  private var activity: Activity? = null
  private var lastPressureVal : Double? = null


  private fun initializeBarometer(): Boolean{
    mSensorManager = activity?.applicationContext!!.getSystemService(SENSOR_SERVICE) as SensorManager
    pressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
    mSensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL);
    return true
  }

  override fun onSensorChanged(event: SensorEvent) {
    lastPressureVal = event.values[0].toDouble()
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int){}

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.example.skillbranch.pressure")
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    channel.setMethodCallHandler(this)
  }

  override fun onDetachedFromActivity() {
    activity = null
    channel.setMethodCallHandler(null)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    activity = null
    channel.setMethodCallHandler(null)
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity
    channel.setMethodCallHandler(this)
  }

  // This static function is optional and equivalent to onAttachedToEngine. It supports the old
  // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
  // plugin registration via this function while apps migrate to use the new Android APIs
  // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
  //
  // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
  // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
  // depending on the user's project. onAttachedToEngine or registerWith must both be defined
  // in the same class.
  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "com.example.skillbranch.pressure")
      channel.setMethodCallHandler(PressurePluginOld())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (activity?.application == null) {
      result.error(call.method, "failed application registration", Exception("failed application registration"))
    }
    when (call.method) {
      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")
      "initializeBarometer" -> {
        initializeBarometer()
        result.success(true)
      }
      "fetchCurrentPressure" ->
        result.success(Gson().toJson(hashMapOf(
                "success" to true,
                "data" to lastPressureVal,
                "error" to null
        ))
      )
      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
