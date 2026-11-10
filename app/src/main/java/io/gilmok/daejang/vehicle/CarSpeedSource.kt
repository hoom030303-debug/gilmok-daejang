package io.gilmok.daejang.vehicle

import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * 계기판과 같은 차량 속도(PERF_VEHICLE_SPEED, m/s)를 km/h로 바꿔 넘겨요.
 * Pleos Connect 개발 문서 FAQ에서 안내하는 방법이에요. 차가 아닌 기기에서는 null을 돌려줘요.
 */
class CarSpeedSource private constructor(private val car: Car, private val manager: CarPropertyManager, private val callback: CarPropertyManager.CarPropertyEventCallback) {

    fun release() {
        runCatching { manager.unregisterCallback(callback); car.disconnect() }
    }

    companion object {
        fun create(context: Context, onKmh: (Float) -> Unit): CarSpeedSource? {
            if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)) return null
            return runCatching {
                val car = Car.createCar(context)
                val manager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
                val cb = object : CarPropertyManager.CarPropertyEventCallback {
                    override fun onChangeEvent(value: CarPropertyValue<*>) {
                        (value.value as? Float)?.let { onKmh(it * 3.6f) }
                    }
                    override fun onErrorEvent(propId: Int, zone: Int) {}
                }
                manager.registerCallback(cb, VehiclePropertyIds.PERF_VEHICLE_SPEED, CarPropertyManager.SENSOR_RATE_NORMAL)
                CarSpeedSource(car, manager, cb)
            }.onFailure { Log.i("GilmokVehicle", "차량 속도 연결 실패: ${it.message}") }.getOrNull()
        }
    }
}
