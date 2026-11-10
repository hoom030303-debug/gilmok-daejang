package io.gilmok.daejang.data

import kotlin.math.max
import kotlin.math.min

/**
 * 운전 한 번의 점수를 실제 차량 신호로 계산해요. (LeagueRules의 규칙과 같은 기준)
 *
 * - 과속: 제한 속도보다 10km/h 넘게 5초 이상 → 10km당 한 번에 6점, 최대 30점
 * - 급제동: 1초에 10km/h 넘게 줄고 브레이크 페달이 밟힘 → 10km당 한 번에 5점, 최대 25점
 * - 급가속: 1초에 9km/h 넘게 늘어남 → 10km당 한 번에 4점, 최대 20점
 * - 미리 감속 못 함: 위험 구간 300m 앞부터 제한 속도 아래로 줄이지 않음 → 한 곳에 4점, 최대 25점
 * - 미리 감속 성공: 한 곳에 0.9점 돌려받기
 * - 보너스 구간(스쿨존, 사고 다발, 내리막) 안에서는 감점·가점 모두 2배
 *
 * 차가 아닌 곳에서도 시험할 수 있게 안드로이드에 의존하지 않는 순수 코드로 두었어요.
 */
class DriveScorer {

    // 횟수 (보너스 구간이면 2로 셈)
    private var speeding = 0.0
    private var hardBrake = 0.0
    private var hardAccel = 0.0
    private var missed = 0.0
    private var early = 0.0
    private var hardBrakeInBonus = 0
    private var earlyCount = 0
    private var earlyBonus = 0
    private var missedCount = 0

    private var lastSpeed: Float? = null
    private var lastSpeedAt = 0L
    private var overSince: Long? = null
    private var speedingCounted = false

    var brakePressed = false
    var speedLimit: Int = 0
    var distanceKm: Double = 0.0
        private set
    private var odometerKm: Double? = null
    private var odometerStart: Double? = null

    /** 지금 다가오는 위험 구간 (id → 판단 상태) */
    private val cautions = mutableMapOf<String, Caution>()
    private var bonusUntil = 0L

    private data class Caution(val bonus: Boolean, var slowedDown: Boolean = false, var judged: Boolean = false)

    private fun weight(now: Long) = if (now <= bonusUntil) 2.0 else 1.0

    /** 차량 속도(km/h)가 들어올 때마다 불러요. */
    fun onSpeed(kmh: Float, now: Long) {
        val prev = lastSpeed
        val dtMs = now - lastSpeedAt
        if (prev != null && dtMs in 200..3000) {
            val perSec = (kmh - prev) * 1000f / dtMs
            val w = weight(now)
            if (perSec <= -10f && brakePressed) { hardBrake += w; if (w > 1) hardBrakeInBonus++ }
            if (perSec >= 9f) hardAccel += w
            // 오도미터가 없을 때는 속도로 거리 적분
            if (odometerKm == null) distanceKm += (prev + kmh) / 2.0 * dtMs / 3_600_000.0
        }
        lastSpeed = kmh; lastSpeedAt = now

        // 과속: 5초 넘게 이어지면 한 번
        if (speedLimit > 0 && kmh > speedLimit + 10) {
            val since = overSince ?: now.also { overSince = it }
            if (!speedingCounted && now - since >= 5000) { speeding += weight(now); speedingCounted = true }
        } else { overSince = null; speedingCounted = false }

        // 위험 구간 앞에서 제한 속도 아래로 줄였는지
        if (speedLimit > 0 && kmh <= speedLimit) cautions.values.forEach { if (!it.judged) it.slowedDown = true }
    }

    /** 주행 거리(km). 차의 트립 거리 값이 오면 그 값을 써요. */
    fun onDistance(km: Double) {
        if (odometerStart == null) odometerStart = km
        odometerKm = km
        distanceKm = max(0.0, km - (odometerStart ?: km))
    }

    /** 내비가 알려 주는 위험 구간까지 남은 거리. */
    fun onCaution(id: String, bonus: Boolean, remainMeters: Int, now: Long) {
        if (remainMeters > 300) return
        val c = cautions.getOrPut(id) { Caution(bonus) }
        if (bonus) bonusUntil = now + 20_000 // 구간을 지나는 동안 2배
        if (!c.judged && remainMeters <= 30) {
            c.judged = true
            val w = if (bonus) 2.0 else 1.0
            if (c.slowedDown) { early += w; earlyCount++; if (bonus) earlyBonus++ } else { missed += w; missedCount++ }
        }
    }

    /** 운전이 끝났을 때 항목별 점수. */
    fun result(): List<ScoreItem> {
        val f = 10.0 / max(distanceKm, 10.0)
        fun r1(x: Double) = Math.round(x * 10) / 10.0
        return listOf(
            ScoreItem("과속", "${speeding.toInt()}번", -r1(min(30.0, speeding * 6 * f)), ScoreItem.Kind.SPEED),
            ScoreItem("급제동", "${hardBrake.toInt() - hardBrakeInBonus}번" + if (hardBrakeInBonus > 0) " · 스쿨존 2배" else "", -r1(min(25.0, hardBrake * 5 * f)), ScoreItem.Kind.BRAKE),
            ScoreItem("급가속", "${hardAccel.toInt()}번", -r1(min(20.0, hardAccel * 4 * f)), ScoreItem.Kind.ACCEL),
            ScoreItem("미리 감속 놓침", "${missedCount}곳", -r1(min(25.0, missed * 4)), ScoreItem.Kind.MISSED),
            ScoreItem("미리 감속 성공", "${earlyCount}곳" + if (earlyBonus > 0) " · ${earlyBonus}곳은 2배" else "", r1(early * 0.9), ScoreItem.Kind.EARLY),
        )
    }
}
