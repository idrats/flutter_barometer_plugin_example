import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';

class Pressure {
  static const MethodChannel _channel =
      const MethodChannel('com.example.skillbranch.pressure');

  static const EventChannel _eventChannel =
      const EventChannel('com.example.skillbranch.pressure/events');

  static Stream<double> get pressureEvents => _eventChannel
      .receiveBroadcastStream()
      .map((event) => double.tryParse(event.toString()));

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> get initializeBarometer async =>
      _channel.invokeMethod<bool>('initializeBarometer');
  static Future<Map<String, dynamic>> get fetchCurrentPressure async {
    final val = await _channel.invokeMethod<String>('fetchCurrentPressure');
    return json.decode(val);
  }
}
