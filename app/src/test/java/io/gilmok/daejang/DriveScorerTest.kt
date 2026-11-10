package io.gilmok.daejang

import io.gilmok.daejang.data.DriveScorer
import io.gilmok.daejang.data.LeagueRules
import org.junit.Assert.assertEquals
import org.junit.Test

/** 기획서의 예시(23.4km 출근길 = 91점)를 실제 신호 흐름으로 재현해요. */
class DriveScorerTest {

    @Test
    fun commuteExampleScores91() {
        val s = DriveScorer()
        var t = 0L
        fun speed(v: Float) { s.onSpeed(v, t); t += 1000 }

        s.speedLimit = 50
        s.onDistance(1000.0)
        // 급가속 2번 (1초에 12km/h씩)
        speed(0f); speed(12f); speed(24f); speed(30f)
        // 스쿨존(보너스) 안에서 급제동 1번
        s.onCaution("school", bonus = true, remainMeters = 250, now = t)
        s.brakePressed = true; speed(18f); s.brakePressed = false
        speed(20f)
        s.onCaution("school", bonus = true, remainMeters = 20, now = t)   // 미리 줄임 → 2배 가점
        // 한빛사거리 미리 감속 성공
        s.onCaution("hanbit", bonus = false, remainMeters = 200, now = t); speed(27f)
        s.onCaution("hanbit", bonus = false, remainMeters = 10, now = t)
        // 은행나무길 내리막(보너스 아님 판정용) 놓침: 제한 속도 위로 달린 채 도착
        s.speedLimit = 30
        s.onCaution("hill", bonus = false, remainMeters = 250, now = t + 30_000); s.onSpeed(45f, t + 30_000)
        s.onCaution("hill", bonus = false, remainMeters = 10, now = t + 31_000)
        s.onDistance(1023.4)

        val items = s.result()
        assertEquals(-4.3, items[1].points, 0.05)   // 급제동 스쿨존 2배
        assertEquals(-3.4, items[2].points, 0.05)   // 급가속 2번
        assertEquals(-4.0, items[3].points, 0.05)   // 미리 감속 놓침 1곳
        assertEquals(2.7, items[4].points, 0.05)    // 미리 감속 성공 2곳 (1곳 2배)
        assertEquals(91, LeagueRules.driveScore(items))
    }
}
