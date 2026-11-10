package io.gilmok.daejang.vehicle

import ai.pleos.playground.navi.constants.CautionPointType
import ai.pleos.playground.navi.data.CameraAlert
import ai.pleos.playground.navi.data.CautionPoint
import ai.pleos.playground.navi.helper.NaviHelper
import ai.pleos.playground.navi.helper.listener.NaviHelperEventListener
import ai.pleos.playground.vehicle.Vehicle
import ai.pleos.playground.vehicle.constant.control.VehicleGear
import ai.pleos.playground.vehicle.listener.BrakePedalStatusListener
import ai.pleos.playground.vehicle.listener.DistanceDrivenListener
import ai.pleos.playground.vehicle.listener.GearSelectionListener
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import io.gilmok.daejang.data.DemoVehicleDataSource
import io.gilmok.daejang.data.Drive
import io.gilmok.daejang.data.DriveScorer
import io.gilmok.daejang.data.LeagueRules
import java.util.Calendar

/**
 * Pleos Connect 차량 신호를 받아 운전 한 번을 기록하고 점수를 매겨요.
 *
 * - Vehicle SDK: 기어(출발·주차 순간), 브레이크 페달, 트립 주행 거리
 * - NaviHelper SDK: 지금 도로의 제한 속도, 위험 구간(스쿨존·사고 다발·내리막)까지 남은 거리, 단속 카메라
 * - AAOS CarPropertyManager: 차량 속도 PERF_VEHICLE_SPEED (Vehicle SDK에 속도가 없어 공식 안내대로 사용)
 *
 * 차가 아닌 기기(휴대폰, 일반 에뮬레이터)에서는 연결이 실패하고, 앱은 예시 데이터로 그대로 동작해요.
 */
class PleosVehicleLink(private val context: Context) {

    enum class Status { DEMO, CONNECTED }

    /** 연결 상태. 화면과 README 시연 안내에 써요. */
    val status = mutableStateOf(Status.DEMO)
    /** 주차 순간 true가 되면 화면이 주차 코칭을 띄워요. */
    val parkedEvent = mutableStateOf(0)

    private val main = Handler(Looper.getMainLooper())
    private var vehicle: Vehicle? = null
    private var navi: NaviHelper? = null
    private var speed: CarSpeedSource? = null
    private var scorer: DriveScorer? = null
    private var startedAt = 0L

    fun start() {
        runCatching {
            vehicle = Vehicle(context).also { v ->
                v.initialize()
                v.getGear().registerGearSelection(gearListener)
                v.getBrake().registerBrakePedalStatus(brakeListener)
                v.getOdometer().registerTripCurrentDrivingDistance(distanceListener)
            }
            status.value = Status.CONNECTED
        }.onFailure { Log.i(TAG, "Vehicle SDK 없음, 예시 데이터로 동작: ${it.message}") }

        runCatching {
            navi = NaviHelper(context).also { n ->
                n.initialize({ n.addListener(naviListener) }, { Log.i(TAG, "NaviHelper 연결 실패: ${it.message}") })
            }
        }.onFailure { Log.i(TAG, "NaviHelper 없음: ${it.message}") }

        speed = CarSpeedSource.create(context) { kmh -> main.post { scorer?.onSpeed(kmh, System.currentTimeMillis()) } }
    }

    fun release() {
        runCatching {
            vehicle?.getGear()?.unregisterGearSelection(gearListener)
            vehicle?.getBrake()?.unregisterBrakePedalStatus(brakeListener)
            vehicle?.getOdometer()?.unregisterTripCurrentDrivingDistance(distanceListener)
            vehicle?.release()
        }
        runCatching { navi?.removeListener(naviListener); navi?.release() }
        speed?.release()
    }

    // ───────── 운전 시작과 끝 ─────────

    private fun onGear(gear: VehicleGear) {
        when {
            gear == VehicleGear.GEAR_PARK -> finishDrive()
            scorer == null && (gear == VehicleGear.GEAR_DRIVE || gear == VehicleGear.GEAR_REVERSE) -> {
                scorer = DriveScorer(); startedAt = System.currentTimeMillis()
            }
        }
    }

    private fun finishDrive() {
        val s = scorer ?: return
        scorer = null
        val items = s.result()
        val km = Math.round(s.distanceKm * 10) / 10.0
        val minutes = ((System.currentTimeMillis() - startedAt) / 60_000).toInt().coerceAtLeast(1)
        val cal = Calendar.getInstance()
        val days = listOf("일", "월", "화", "수", "목", "금", "토")
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val drive = Drive(
            day = days[cal.get(Calendar.DAY_OF_WEEK) - 1], date = cal.get(Calendar.DAY_OF_MONTH),
            title = if (hour < 12) "출근길" else "퇴근길",
            time = "오늘 %02d:%02d".format(hour, cal.get(Calendar.MINUTE)),
            km = km, minutes = minutes, score = LeagueRules.driveScore(items),
        )
        DemoVehicleDataSource.recordLiveDrive(drive, items)
        parkedEvent.value++
    }

    // ───────── 리스너 ─────────

    private val gearListener = object : GearSelectionListener {
        override fun onSelected(gear: VehicleGear?) { if (gear != null) main.post { onGear(gear) } }
        override fun onFailed(e: Exception) { Log.w(TAG, "기어", e) }
    }
    private val brakeListener = object : BrakePedalStatusListener {
        override fun onBrakePedalStatusUpdated(pressed: Boolean?) { main.post { scorer?.brakePressed = pressed == true } }
        override fun onFailed(e: Exception) { Log.w(TAG, "브레이크", e) }
    }
    private val distanceListener = object : DistanceDrivenListener {
        override fun onDistanceDrivenUpdated(km: Float?) { if (km != null) main.post { scorer?.onDistance(km.toDouble()) } }
        override fun onFailed(e: Exception) { Log.w(TAG, "주행 거리", e) }
    }
    private val naviListener = object : NaviHelperEventListener {
        override fun onCurrentRoadSpeedLimit(limit: Int) { main.post { scorer?.speedLimit = limit } }
        override fun onCautionPointInfo(points: List<CautionPoint>) {
            val now = System.currentTimeMillis()
            main.post { points.forEach { p -> scorer?.onCaution(p.id, p.type in BONUS, p.remainDistance, now) } }
        }
        override fun onCameraAlert(alerts: List<CameraAlert>) {
            val now = System.currentTimeMillis()
            main.post { alerts.forEach { a -> scorer?.onCaution("cam_" + a.id, false, a.remainDistance, now) } }
        }
    }

    companion object {
        private const val TAG = "GilmokVehicle"
        /** 감점·가점을 2배로 계산하는 보너스 구간 */
        private val BONUS = setOf(
            CautionPointType.SCHOOL_ZONE, CautionPointType.ACCIDENT_PRONE_AREA, CautionPointType.DOWNHILL_CAUTION_AREA,
        )
    }
}
