package io.gilmok.daejang.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

data class Person(
    val id: String,
    val name: String,
    val color: Color,
    val soft: Color,
    @DrawableRes val avatar: Int,
)

data class ScoreItem(
    val title: String,
    val detail: String,
    val points: Double,
    val kind: Kind,
) {
    enum class Kind { SPEED, BRAKE, ACCEL, MISSED, EARLY }
}

data class Drive(
    val day: String,
    val date: Int,
    val title: String,
    val time: String,
    val km: Double,
    val minutes: Int,
    val score: Int,
    val badge: String? = null,
)

data class LeagueMember(
    val person: Person,
    val weeklyKm: Double,
    val average: Double,
    val lastWeek: Double,
    val roadsHeld: Int,
) {
    val growth get() = average - lastWeek
    val ranked get() = LeagueRules.isRanked(weeklyKm)
}

data class LeaderChange(val date: String, val person: Person, val average: Double)

data class Road(
    val name: String,
    val kind: String,
    val leader: Person,
    val leaderAverage: Double,
    val myAverage: Double,
    val myPasses: Int,
    /** 최근 2주 요일별 통과 횟수 (월~일, 지난주 7칸 + 이번 주 7칸) */
    val passGrid: List<Int>,
    val todayIndex: Int,
    val history: List<LeaderChange>,
) {
    val gap get() = leaderAverage - myAverage
    val needed get() = LeagueRules.scoreNeededToTake(myAverage, myPasses, leaderAverage)
}
