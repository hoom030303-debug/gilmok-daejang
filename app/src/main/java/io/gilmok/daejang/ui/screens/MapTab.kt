package io.gilmok.daejang.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.data.one
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.Route
import io.gilmok.daejang.ui.components.*
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka
import io.gilmok.daejang.ui.theme.L

// ───────── 13 친구 지도 ─────────

@Composable
fun MapScreen(nav: Nav, voice: CoachVoice) {
    val road = data.nextRoad()
    val coach = "${road.name}은 ${road.leader.name}과 ${road.gap.one()}점 차예요. 내일 출근길에 가져와 봐요"
    Screen(header = { Header(nav) }, coach = { CoachBar(coach, voice) }, tabBar = { Tabs(nav, Tab.MAP) }) {
        TwoColumns(
            left = {
                Picture(
                    R.drawable.map_friends,
                    Modifier.fillMaxSize().clip(RoundedCornerShape(L.RADIUS.dp)),
                    ContentScale.FillBounds,
                )
            },
            right = {
                Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                    Card(Modifier.fillMaxWidth().height(455.dp)) {
                        Column {
                            Pill("가져올 수 있는 길", C.PurpleSoft, C.Purple, Icons.Filled.Place)
                            Spacer(Modifier.height(18.dp))
                            T(road.name, 36f, C.Text, 900)
                            T("최근 2주 동안 ${road.myPasses}번 지나갔어요", 20f, C.Sub)
                            Spacer(Modifier.height(18.dp))
                            PersonScore(road.leader.avatar, road.leader.name, "지금 대장", road.leaderAverage, C.Green)
                            Spacer(Modifier.height(12.dp))
                            PersonScore(data.ME.avatar, "나", "내 평균", road.myAverage, C.Pink)
                            Spacer(Modifier.weight(1f))
                            PrimaryButton("길 자세히 보기", Modifier.fillMaxWidth()) { nav.go(Route.ROAD) }
                        }
                    }
                    Card(Modifier.fillMaxWidth().fillMaxHeight()) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            T("이번 주 지킨 길", 27f, C.Text, 700)
                            Spacer(Modifier.height(6.dp))
                            val league = data.league()
                            val bars = listOf(C.OrangeBar, C.PinkBar, C.Blue, C.GreenBar)
                            league.forEachIndexed { i, m ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Avatar(m.person.avatar, 42f)
                                    Spacer(Modifier.width(14.dp))
                                    T(m.person.name, 22f, C.Text, 700, modifier = Modifier.width(110.dp))
                                    Bar(m.roadsHeld / 9f, bars[i], Modifier.weight(1f))
                                    T("${m.roadsHeld}곳", 22f, C.Text, 700, modifier = Modifier.padding(start = 30.dp))
                                }
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun PersonScore(avatar: Int, name: String, sub: String, score: Double, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(avatar, 61f)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            T(name, 23f, C.Text, 700)
            T(sub, 20f, C.Faint)
        }
        T(score.one(), 39f, color, 700, family = Fredoka)
    }
}

// ───────── 14 길 상세 ─────────

@Composable
fun RoadScreen(nav: Nav, voice: CoachVoice) {
    val road = data.nextRoad()
    val coach = "내일 출근길에 다시 지나요. 내리막 들어가기 전에 미리 속도를 줄여 봐요"
    Screen(header = { BackHeader("친구 지도", { nav.back() }) }, coach = { CoachBar(coach, voice) }, tabBar = { Tabs(nav, Tab.MAP) }) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        Pill("가져올 수 있는 길", C.PurpleSoft, C.Purple, Icons.Filled.Place)
                        Spacer(Modifier.height(16.dp))
                        T(road.name, 48f, C.Text, 900)
                        T("${road.kind} · 출근길에 매일 지나요", 21f, C.Sub)
                        Spacer(Modifier.height(24.dp))
                        Picture(R.drawable.map_road, Modifier.fillMaxWidth().height(343.dp).clip(RoundedCornerShape(36.dp)), ContentScale.FillBounds)
                        Spacer(Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                            StatTile("지금 대장", road.leader.name, road.leaderAverage.one(), road.leader.avatar, road.leader.color, Modifier.weight(1f))
                            StatTile("내 평균", "나", road.myAverage.one(), data.ME.avatar, C.Pink, Modifier.weight(1f))
                            Column(
                                Modifier.weight(1f).height(206.dp).clip(RoundedCornerShape(30.dp)).background(C.PurpleCard).padding(24.dp),
                            ) {
                                T("가져오려면", 20f, Color(0xFFE9DEFF), 700)
                                Spacer(Modifier.height(4.dp))
                                NumUnit("${road.needed}", "점", 61f, Color.White, 22f, Color.White)
                                Spacer(Modifier.weight(1f))
                                T("이상으로 한 번 지나기", 20f, Color.White, 700)
                            }
                        }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        T("최근 2주", 24f, C.Sub, 700)
                        NumUnit("${road.myPasses}", "번 지나갔어요", 64f, C.Text, 24f, C.Text)
                        Spacer(Modifier.height(20.dp))
                        PassGrid(road.passGrid, road.todayIndex)
                        Spacer(Modifier.height(28.dp))
                        Box(Modifier.fillMaxWidth().height(2.dp).background(C.Line))
                        Spacer(Modifier.height(28.dp))
                        T("대장이 바뀐 날", 27f, C.Text, 700)
                        Spacer(Modifier.height(20.dp))
                        road.history.forEachIndexed { i, h ->
                            val latest = i == 0
                            Row(Modifier.height(124.dp), verticalAlignment = Alignment.Top) {
                                Avatar(h.person.avatar, 61f)
                                Spacer(Modifier.width(18.dp))
                                Column(Modifier.weight(1f)) {
                                    T(h.date, 20f, C.Faint)
                                    T(if (h.person == data.ME) "내가 대장이 됐어요" else "${h.person.name}${if (h.person == data.F3) "이" else "가"} 대장이 됐어요", 22f, if (latest) C.Text else C.Sub, 700)
                                }
                                T(h.average.one(), 30f, if (latest) h.person.color else C.Faint, 700, family = Fredoka)
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun StatTile(label: String, name: String, value: String, avatar: Int, color: Color, modifier: Modifier) {
    Column(modifier.height(206.dp).clip(RoundedCornerShape(30.dp)).background(C.Row).padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(avatar, 61f)
            Spacer(Modifier.width(14.dp))
            Column { T(label, 20f, C.Faint); T(name, 21f, C.Text, 700) }
        }
        Spacer(Modifier.weight(1f))
        T(value, 55f, color, 700, family = Fredoka)
    }
}

@Composable
private fun PassGrid(grid: List<Int>, today: Int) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) { days.forEach { Box(Modifier.width(55.dp), contentAlignment = Alignment.Center) { T(it, 20f, C.Faint) } } }
        for (w in 0..1) Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            for (d in 0..6) {
                val i = w * 7 + d
                val n = grid[i]
                Box(
                    Modifier.size(55.dp).clip(RoundedCornerShape(16.dp))
                        .background(if (i == today && n > 0) C.Purple else if (n > 0) Color(0xFFCDB6FF) else C.Row),
                    contentAlignment = Alignment.Center,
                ) { if (n > 0) T("$n", 21f, if (i == today) Color.White else C.Purple, 700, family = Fredoka) }
            }
        }
    }
}
