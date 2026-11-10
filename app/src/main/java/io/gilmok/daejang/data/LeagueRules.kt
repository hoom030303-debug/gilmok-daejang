package io.gilmok.daejang.data

import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * 길목대장 점수 규칙.
 *
 * 1. 운전 점수 = 100 - 감점 + 가점 (감점은 달린 거리 10km 기준으로 맞춰 계산)
 * 2. 스쿨존·사고 다발 구간(보너스 구간)은 감점과 가점이 모두 2배
 * 3. 길의 대장 = 최근 2주 동안 그 길을 2번 이상 지난 사람 중 그 길 평균 점수가 가장 높은 사람
 * 4. 리그 순위 = 이번 주 운전 점수의 거리 가중 평균, 일주일 50km 이상 달린 사람만
 * 5. 성장 순위 = 이번 주 평균 - 지난주 평균
 * 6. 2km보다 짧은 운전은 점수만 남기고 순위에는 넣지 않음
 */
object LeagueRules {

    const val MIN_WEEKLY_KM = 50.0
    const val MIN_DRIVE_KM = 2.0
    const val MIN_PASSES_FOR_LEADER = 2

    /** 항목별 점수(감점은 음수, 가점은 양수)를 더해 운전 점수를 만들어요. */
    fun driveScore(items: List<ScoreItem>): Int =
        (100.0 + items.sumOf { it.points }).roundToInt().coerceIn(0, 100)

    /** 한 번 더 지났을 때 평균이 지금 대장보다 높아지려면 몇 점 이상이어야 하는지. */
    fun scoreNeededToTake(myAverage: Double, myPasses: Int, leaderAverage: Double): Int {
        val needed = leaderAverage * (myPasses + 1) - myAverage * myPasses
        return (floor(needed) + 1).toInt().coerceAtMost(100)
    }

    /** 한 번 더 지난 뒤의 새 평균. */
    fun newAverage(myAverage: Double, myPasses: Int, score: Int): Double =
        (myAverage * myPasses + score) / (myPasses + 1)

    /** 거리 가중 평균. */
    fun weightedAverage(drives: List<Drive>): Double {
        val counted = drives.filter { it.km >= MIN_DRIVE_KM }
        val km = counted.sumOf { it.km }
        if (km == 0.0) return 0.0
        return counted.sumOf { it.score * it.km } / km
    }

    fun isRanked(weeklyKm: Double) = weeklyKm >= MIN_WEEKLY_KM
}

fun Double.one(): String = String.format("%.1f", this)
