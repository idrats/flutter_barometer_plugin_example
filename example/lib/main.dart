import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:pressure/pressure.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  var isBarometerInitialized = false;
  double currentPressure;

  @override
  void initState() {
    super.initState();
    Pressure.pressureEvents.listen((val) {
      if (mounted && currentPressure != val) {
        setState(() {
          currentPressure = val;
        });
      }
    });
    // initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await Pressure.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(children: [
            Text('Running on: $_platformVersion\n'),
            // if (!isBarometerInitialized)
            //   OutlineButton(
            //       child: Text("Проинициализировать датчик давления"),
            //       onPressed: () async {
            //         try {
            //           await Pressure.initializeBarometer;
            //           if (mounted) {
            //             setState(() {
            //               isBarometerInitialized = true;
            //             });
            //           }
            //         } catch (e, s) {
            //           print(e);
            //           print(s);
            //         }
            //       }),
            // if (isBarometerInitialized)
            //   OutlineButton(
            //       child: Text("Получить текущее давление"),
            //       onPressed: () async {
            //         try {
            //           final v = await Pressure.fetchCurrentPressure;
            //           currentPressure = v['data'];
            //           if (mounted) setState(() {});
            //         } catch (e, s) {
            //           print(e);
            //           print(s);
            //         }
            //       }),
            if (currentPressure != null)
              Text(
                  "Текущее значение давления: ${currentPressure.toStringAsFixed(1)} миллибар")
          ]),
        ),
      ),
    );
  }
}
