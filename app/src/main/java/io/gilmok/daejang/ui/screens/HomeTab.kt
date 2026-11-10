package io.gilmok.daejang.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.Canvas
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.data.LeagueRules
import io.gilmok.daejang.data.one
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.Route
import io.gilmok.daejang.ui.components.*
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka
import io.gilmok.daejang.ui.theme.Noto
import kotlinx.coroutines.delay

// ───────── 11 홈 ─────────

@Composable
fun HomeScreen(nav: Nav, voice: CoachVoice) {
    val drive = data.lastDrive()
    val retaken = data.retakenRoad()
    val next = data.nextRoad()
    val league = data.league()
    val coach = "${retaken.name}를 되찾았어요. 내일은 ${next.name}만 미리 줄여 봐요"

    Screen(
        header = { Header(nav, Tab.HOME) },
        coach = { CoachBar(coach, voice) },
    ) {
        TwoColumns(
            equal = true,
            left = {
                Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                    // 최근 운전
                    Card(Modifier.fillMaxWidth().height(455.dp), background = C.PurpleCard) {
                        Column(Modifier.fillMaxSize()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                T("최근 운전", 26f, Color.White, 700)
                                Spacer(Modifier.width(20.dp))
                                T("${drive.time} ${drive.title} · ${drive.km}km", 21f, Color(0xD9FFFFFF))
                                Spacer(Modifier.weight(1f))
                                Row(
                                    Modifier.clip(RoundedCornerShape(50)).background(Color(0x2EFFFFFF))
                                        .clickable { nav.go(Route.DRIVE) }.padding(start = 22.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) { T("자세히", 22f, Color.White, 700); Chevron(Color.White, 30f) }
                            }
                            Row(Modifier.padding(top = 10.dp)) {
                                Row(verticalAlignment = Alignment.Bottom, modifier = Modifier.width(320.dp)) {
                                    T("${drive.score}", 152f, Color.White, 700, family = Fredoka, lineHeight = 170f)
                                    T("점", 36f, Color(0xD9FFFFFF), 700, modifier = Modifier.padding(bottom = 30.dp, start = 10.dp))
                                }
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 20.dp)) {
                                    Pill("${retaken.name} 대장 되찾음", Color.White, C.Green)
                                    Pill("급제동 1번", Color.White, C.Red, Icons.Filled.Warning)
                                    Pill("지난 운전보다 +3점", Color.White, C.Purple)
                                }
                            }
                            Spacer(Modifier.weight(1f))
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                listOf("과속" to ("0" to "번"), "급제동" to ("1" to "번"), "급가속" to ("2" to "번"), "미리 감속" to ("2" to "곳")).forEach { (label, v) ->
                                    Column(
                                        Modifier.width(153.dp).height(97.dp).clip(RoundedCornerShape(20.dp)).background(Color(0x26FFFFFF)).padding(horizontal = 18.dp, vertical = 12.dp),
                                    ) {
                                        T(label, 20f, Color(0xD9FFFFFF))
                                        NumUnit(v.first, v.second, 36f, Color.White, 20f, Color(0xD9FFFFFF))
                                    }
                                }
                            }
                        }
                    }
                    // 다음 목표
                    Card(Modifier.fillMaxWidth().height(352.dp), onClick = { nav.go(Route.ROAD) }) {
                        Column {
                            Row {
                                Column(Modifier.weight(1f)) {
                                    SoftChip("다음 목표", C.PurpleSoft, C.Purple)
                                    Spacer(Modifier.height(16.dp))
                                    T(next.name, 39f, C.Text, 900)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    T("평균 차이", 20f, C.Faint)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        T(next.gap.one(), 52f, C.Purple, 700, family = Fredoka)
                                        Chevron()
                                    }
                                }
                            }
                            T("내일 출근길에 ${next.needed}점 이상으로 지나면 대장 자리를 가져와요", 21f, C.Sub)
                            Spacer(Modifier.height(26.dp))
                            ScoreBar(data.ME.avatar, "나", next.myAverage, C.PinkBar, C.Pink)
                            Spacer(Modifier.height(16.dp))
                            ScoreBar(next.leader.avatar, next.leader.name, next.leaderAverage, C.GreenBar, C.Green)
                        }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                T("이번 주 순위", 33f, C.Text, 900)
                                T("${data.LEAGUE_NAME} · ${data.TIME_LEFT} 남음", 20f, C.Faint)
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        league.forEachIndexed { i, m ->
                            RankRow(if (m.ranked) "${i + 1}" else "-", m, 112f, 79f, right = { ScoreText(m) }, sub = leagueSub(m))
                        }
                        Row(
                            Modifier.fillMaxWidth().height(140.dp).clip(RoundedCornerShape(33.dp)).background(C.GreenSoft)
                                .clickable { nav.go(Route.GROWTH) }.padding(horizontal = 30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            T("+4.8", 48f, C.Green, 700, family = Fredoka)
                            Spacer(Modifier.width(36.dp))
                            Column(Modifier.weight(1f)) {
                                T("성장 순위 1위", 26f, C.Text, 700)
                                T("지난주의 나보다 4.8점 올랐어요", 20f, C.Sub)
                            }
                            Chevron(C.Green)
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun ScoreBar(avatar: Int, name: String, score: Double, bar: Color, text: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(avatar, 44f)
        Spacer(Modifier.width(16.dp))
        T(name, 22f, C.Text, 700, modifier = Modifier.width(100.dp))
        Bar(((score - 73) / 27).toFloat(), bar, Modifier.weight(1f))
        T(score.one(), 30f, text, 700, family = Fredoka, modifier = Modifier.padding(start = 40.dp))
    }
}

// ───────── 07 운전자 확인 ─────────

@Composable
fun DriverScreen(nav: Nav, voice: CoachVoice) {
    var seconds by remember { mutableIntStateOf(7) }
    LaunchedEffect(Unit) {
        while (seconds > 0) { delay(1000); seconds-- }
        nav.replace(Route.GOAL)
    }
    val coach = "오늘도 나예요? 대답이 없으면 지난번처럼 나로 기록할게요"
    LaunchedEffect(Unit) { voice.speak(coach) }

    Screen(header = { CloseHeader({ nav.close() }, { nav.demoOpen = true }) }, coach = { CoachBar(coach, voice) }) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize(), background = C.SoftPurpleCard) {
                    Column {
                        Pill("시동 켜짐 · 출발 전", Color.White, C.Purple)
                        Spacer(Modifier.height(20.dp))
                        T("오늘은 누가 운전해요?", 45f, C.Text, 900)
                        T("고른 사람의 리그 점수로 기록해요", 21f, C.Sub)
                        Spacer(Modifier.height(28.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DriverCard("나", "지난번 운전자", selected = true, Modifier.weight(1f)) {
                                Box(Modifier.size(206.dp).clip(CircleShape).background(data.ME.soft), contentAlignment = Alignment.Center) {
                                    Picture(R.drawable.char_me_full, Modifier.size(150.dp, 219.dp).offset(y = 30.dp))
                                }
                            }
                            DriverCard("가족", "이 차에 등록된 가족", selected = false, Modifier.weight(1f)) { IconCircle(Icons.Filled.Person) }
                            DriverCard("손님", "점수를 기록하지 않아요", selected = false, Modifier.weight(1f)) { IconCircle(Icons.Filled.Person) }
                        }
                        Spacer(Modifier.height(20.dp))
                        Row(
                            Modifier.fillMaxWidth().height(79.dp).clip(RoundedCornerShape(24.dp)).background(Color(0x99FFFFFF))
                                .outline(C.PurpleChar, 24f), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
                        ) { Icon(Icons.Filled.Add, null, tint = C.Purple, modifier = Modifier.size(30.dp)); T("이 차 운전자 추가하기", 23f, C.Purple, 700) }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(Modifier.height(10.dp))
                        T("대답이 없으면", 23f, C.Sub, 700)
                        Spacer(Modifier.height(24.dp))
                        Box(Modifier.size(261.dp), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.fillMaxSize()) {
                                drawArc(C.PurpleSoft, 0f, 360f, false, style = Stroke(26.dp.toPx()))
                                drawArc(C.Purple, -90f, 360f * seconds / 7f, false, style = Stroke(26.dp.toPx(), cap = StrokeCap.Round))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                T("$seconds", 90f, C.Purple, 700, family = Fredoka, lineHeight = 100f)
                                T("초 뒤", 20f, C.Sub, 700)
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                        T("'나'로 기록할게요", 30f, C.Text, 900)
                        Spacer(Modifier.weight(1f))
                        Column(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(28.dp)).background(C.Row).padding(28.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            T("말로 골라도 돼요", 23f, C.Text, 700)
                            listOf("“나야”", "“엄마가 운전해”", "“손님이야”").forEach {
                                Box(Modifier.clip(RoundedCornerShape(50)).background(Color.White).padding(horizontal = 18.dp, vertical = 4.dp)) { T(it, 20f, C.Sub) }
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun DriverCard(name: String, sub: String, selected: Boolean, modifier: Modifier, art: @Composable () -> Unit) {
    Column(
        modifier.height(455.dp).clip(RoundedCornerShape(30.dp)).background(Color.White)
            .then(if (selected) Modifier.outline(C.Purple, 30f, 4f) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(42.dp))
        Box { art()
            if (selected) Box(Modifier.offset(x = 150.dp, y = (-20).dp).size(48.dp).clip(CircleShape).background(C.Purple), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }
        Spacer(Modifier.weight(1f))
        T(name, 36f, C.Text, 900)
        T(sub, 20f, if (selected) C.Purple else C.Faint)
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun IconCircle(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(Modifier.size(206.dp).clip(CircleShape).background(Color(0xFFE8EDF5)), contentAlignment = Alignment.Center) {
        Icon(icon, null, tint = C.Faint, modifier = Modifier.size(90.dp))
    }
}

// ───────── 08 오늘의 목표 ─────────

@Composable
fun GoalScreen(nav: Nav, voice: CoachVoice) {
    val road = data.retakenRoad()
    val need = LeagueRules.scoreNeededToTake(road.myAverage, road.myPasses, road.leaderAverage)
    val coach = "운전하는 동안은 화면을 닫고 소리로만 알려 드려요"
    LaunchedEffect(Unit) { voice.speak("오늘 출근길 ${road.name}를 ${need}점 이상으로 지나면 대장 자리를 되찾아요") }

    Screen(
        header = { CloseHeader({ nav.close() }, { nav.demoOpen = true }) },
        coach = {
            CoachBar(coach, voice, label = "대장 코치") {
                PrimaryButton("닫고 출발하기", Modifier.width(328.dp)) { nav.replace(Route.PARKING) }
            }
        },
    ) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize(), background = C.SoftBlueCard) {
                    Box(Modifier.fillMaxSize()) {
                        Column {
                            Pill("출발 전 · 오늘의 목표", Color.White, C.Purple)
                            Spacer(Modifier.height(18.dp))
                            Box(Modifier.fillMaxWidth().height(297.dp).clip(RoundedCornerShape(40.dp)).background(Color.White).padding(horizontal = 40.dp), contentAlignment = Alignment.CenterStart) {
                                Text(buildAnnotatedString {
                                    val base = SpanStyle(fontFamily = Noto, fontWeight = FontWeight(900), fontSize = 39.sp, color = C.Text)
                                    withStyle(base) { append("오늘 출근길 ${road.name}를\n") }
                                    withStyle(base.copy(color = C.Purple)) { append("${need}점") }
                                    withStyle(base) { append(" 이상으로 지나면\n대장 자리를 되찾아요") }
                                }, lineHeight = 64.sp)
                            }
                        }
                        Picture(R.drawable.pose_megaphone, Modifier.offset(x = 17.dp, y = 481.dp).size(253.dp, 237.dp))
                        Column(
                            Modifier.offset(x = 322.dp, y = 419.dp).size(546.dp, 346.dp).clip(RoundedCornerShape(36.dp)).background(Color.White).padding(30.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            T("${road.name} 대장 점수", 22f, C.Sub, 700)
                            DuelRow(data.ME.avatar, "나", road.myAverage, C.PinkBar, C.Pink)
                            DuelRow(road.leader.avatar, road.leader.name, road.leaderAverage, C.OrangeBar, C.Orange)
                            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(C.PurpleSoft).padding(18.dp)) {
                                T("${(road.leaderAverage - road.myAverage).one()}점 차라 오늘 한 번이면 뒤집을 수 있어요", 20f, C.Purple, 700)
                            }
                        }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        T("오늘 갈 길", 30f, C.Text, 900)
                        T("집 → 회사 · 약 40분", 20f, C.Faint)
                        Spacer(Modifier.height(30.dp))
                        RouteStop("집", "07:18 출발", C.Faint, null)
                        RouteStop("새솔초 앞 스쿨존", "감점 2배 구간, 천천히", C.Gold, "조심")
                        RouteStop(road.name, "${need}점 이상으로 지나기", C.Purple, "목표", strong = true)
                        RouteStop("은행나무길 내리막", "미리 속도 줄이기", C.OrangeBar, "조심")
                        RouteStop("회사", "07:58 도착 예정", C.Faint, null, last = true)
                    }
                }
            },
        )
    }
}

@Composable
private fun DuelRow(avatar: Int, name: String, score: Double, bar: Color, text: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Avatar(avatar, 67f)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            T(name, 21f, C.Text, 700)
            Bar(((score - 72.7) / 27.3).toFloat(), bar, Modifier.fillMaxWidth(0.85f))
        }
        T(score.one(), 36f, text, 700, family = Fredoka)
    }
}

@Composable
private fun RouteStop(title: String, sub: String, color: Color, tag: String?, strong: Boolean = false, last: Boolean = false) {
    Row(Modifier.height(if (last) 80.dp else 145.dp)) {
        Box(Modifier.width(36.dp), contentAlignment = Alignment.TopCenter) {
            if (!last) Box(Modifier.offset(y = 20.dp).width(4.dp).height(145.dp).background(C.Line))
            Box(
                Modifier.size(if (strong) 40.dp else 27.dp).clip(CircleShape)
                    .background(if (strong) color else Color.White)
                    .then(if (!strong) Modifier.outline(color, 27f, 5f) else Modifier),
            )
        }
        Spacer(Modifier.width(20.dp))
        Column(Modifier.weight(1f)) {
            T(title, 24f, if (strong) C.Purple else C.Text, 700)
            T(sub, 20f, C.Sub)
        }
        if (tag != null) SoftChip(tag, if (strong) C.PurpleSoft else C.GoldSoft, if (strong) C.Purple else C.GoldText)
    }
}
