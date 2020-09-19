import Flutter
import UIKit
import CoreMotion

public class SwiftPressurePlugin: NSObject, FlutterPlugin {
    let altimeter: CMAltimeter
    var pressure: NSNumber?
    
    init(_ value: CMAltimeter) {
        self.altimeter = value
    }
    
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "com.example.skillbranch.pressure", binaryMessenger: registrar.messenger())
    let instance = SwiftPressurePlugin(CMAltimeter())
    registrar.addMethodCallDelegate(instance, channel: channel)
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    if (call.method == "getPlatformVersion") {
        result("iOS " + UIDevice.current.systemVersion)
    } else if (call.method == "initializeBarometer") {
        if CMAltimeter.isRelativeAltitudeAvailable() {
            altimeter.startRelativeAltitudeUpdates(to: OperationQueue.main) { (data ,error) in
                self.pressure = data?.pressure
            }
            result(true)
        } else {
            result(false)
        }
    } else if (call.method == "fetchCurrentPressure") {
        if (self.pressure == nil) {
            result(nil)
        } else {
            result(self.pressure!.doubleValue / 10)
        }
        
    } else {
        result(FlutterMethodNotImplemented)
    }
  }
}
