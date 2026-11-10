package io.gilmok.daejang.data

import androidx.compose.ui.graphics.Color
import io.gilmok.daejang.R

/** 시연용 예시 데이터. 금요일 아침 출근을 막 마친 상태예요. */
object DemoVehicleDataSource : VehicleDataSource {

    val ME = Person("me", "나", Color(0xFFE0457E), Color(0xFFFFE0EC), R.drawable.av_me)
    val F1 = Person("f1", "친구 1", Color(0xFFE2770E), Color(0xFFFFE6CC), R.drawable.av_f1)
    val F2 = Person("f2", "친구 2", Color(0xFF4795E8), Color(0xFFDCEBFD), R.drawable.av_f2)
    val F3 = Person("f3", "친구 3", Color(0xFF12A37A), Color(0xFFD6F5EA), R.drawable.av_f3)

    const val LEAGUE_NAME = "대학 동기 리그"
    const val TIME_LEFT = "2일 16시간"

    override fun me() = ME

    override fun league() = listOf(
        LeagueMember(F1, weeklyKm = 148.0, average = 91.2, lastWeek = 90.6, roadsHeld = 8),
        LeagueMember(ME, weeklyKm = 126.0, average = 89.7, lastWeek = 84.9, roadsHeld = 8),
        LeagueMember(F2, weeklyKm = 97.0, average = 86.4, lastWeek = 87.7, roadsHeld = 4),
        LeagueMember(F3, weeklyKm = 41.0, average = 82.1, lastWeek = 80.0, roadsHeld = 2),
    )

    override fun drivesThisWeek(): List<Drive> = listOfNotNull(live.value?.first) + demoDrives().let { if (live.value != null) it.drop(1) else it }

    private fun demoDrives() = listOf(
        Drive("금", 13, "출근길", "오늘 07:18", 23.4, 41, 91, badge = "대장 되찾음"),
        Drive("목", 12, "퇴근길", "18:35", 22.8, 44, 94),
        Drive("목", 12, "출근길", "07:20", 23.4, 40, 94),
        Drive("수", 11, "퇴근길", "18:40", 23.1, 43, 89),
    )

    /** 차에서 실제로 기록한 마지막 운전. 있으면 예시 데이터 대신 이 값을 보여 줘요. */
    private val live = androidx.compose.runtime.mutableStateOf<Pair<Drive, List<ScoreItem>>?>(null)

    fun recordLiveDrive(drive: Drive, items: List<ScoreItem>) { previous = lastDrive().score; live.value = drive to items }
    private var previous = 88
    fun isLive() = live.value != null
    /** 바로 전 운전 점수 (예시 데이터에서는 88점 → 91점, +3) */
    fun previousScore() = previous

    override fun lastDrive() = live.value?.first ?: demoDrives().first()

    override fun lastDriveItems() = live.value?.second ?: demoItems()

    private fun demoItems() = listOf(
        ScoreItem("과속", "0번", 0.0, ScoreItem.Kind.SPEED),
        ScoreItem("급제동", "1번 · 스쿨존 2배", -4.3, ScoreItem.Kind.BRAKE),
        ScoreItem("급가속", "2번", -3.4, ScoreItem.Kind.ACCEL),
        ScoreItem("미리 감속 놓침", "1곳", -4.0, ScoreItem.Kind.MISSED),
        ScoreItem("미리 감속 성공", "2곳 · 1곳은 2배", 2.7, ScoreItem.Kind.EARLY),
    )

    /** 오늘 아침 97점으로 지나 되찾은 길. 출발 전에는 88.6 대 89.0이었어요. */
    override fun retakenRoad() = Road(
        name = "한빛사거리", kind = "교차로 구간", leader = F1,
        leaderAverage = 89.0, myAverage = 88.6, myPasses = 14,
        passGrid = listOf(2, 1, 2, 1, 1, 0, 0, 2, 1, 1, 2, 1, 0, 0), todayIndex = 11,
        history = emptyList(),
    )

    /** 다음 목표 길. */
    override fun nextRoad() = Road(
        name = "은행나무길 내리막", kind = "내리막 구간", leader = F3,
        leaderAverage = 87.4, myAverage = 86.9, myPasses = 6,
        passGrid = listOf(1, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0, 0), todayIndex = 11,
        history = listOf(
            LeaderChange("11.12 목", F3, 87.4),
            LeaderChange("11.5 목", ME, 86.1),
            LeaderChange("10.30 금", F2, 85.4),
        ),
    )

    const val RETAKE_SCORE = 97
}
