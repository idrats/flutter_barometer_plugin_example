package com.example.skillbranch.pressure

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry.Registrar


/** PressurePlugin */
public class PressurePlugin: FlutterPlugin{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private var pressureChannel: EventChannel? = null

  private fun initializeBarometer(context: Context, messenger: BinaryMessenger) {
    pressureChannel = EventChannel(messenger, "com.example.skillbranch.pressure/events")
    val pressureStreamHandler = StreamHandlerImpl(context.getSystemService(SENSOR_SERVICE) as SensorManager, Sensor.TYPE_PRESSURE)
    pressureChannel!!.setStreamHandler(pressureStreamHandler)
  }

  private fun tearDownEventChannel() {
    pressureChannel!!.setStreamHandler(null)
  }

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    initializeBarometer(flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger)
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
      PressurePlugin().initializeBarometer(registrar.activeContext(), registrar.messenger())
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    tearDownEventChannel()
  }
}
