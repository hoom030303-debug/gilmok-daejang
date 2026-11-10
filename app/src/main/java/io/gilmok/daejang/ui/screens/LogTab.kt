package io.gilmok.daejang.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.data.LeagueRules
import io.gilmok.daejang.data.ScoreItem
import io.gilmok.daejang.data.one
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.Route
import io.gilmok.daejang.ui.components.*
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka
import kotlin.math.abs

// ───────── 15 운전 기록 ─────────

@Composable
fun LogScreen(nav: Nav, voice: CoachVoice) {
    val drives = data.drivesThisWeek()
    val coach = "이번 주엔 목요일 퇴근길이 94점으로 가장 부드러웠어요"
    Screen(header = { Header(nav) }, coach = { CoachBar(coach, voice) }, tabBar = { Tabs(nav, Tab.LOG) }) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                T("이번 주 운전", 33f, C.Text, 900)
                                T("9번 운전했어요 · 126km", 20f, C.Faint)
                            }
                            Segmented("지난주", "이번 주", rightSelected = true, onLeft = {}, onRight = {})
                        }
                        Spacer(Modifier.height(6.dp))
                        drives.forEachIndexed { i, d ->
                            val today = i == 0
                            Row(
                                Modifier.fillMaxWidth().height(155.dp).clip(RoundedCornerShape(33.dp))
                                    .background(if (today) C.PinkSoft else C.Row)
                                    .then(if (today) Modifier.outline(C.PinkLine, 33f, 3f) else Modifier)
                                    .clickable { nav.go(Route.DRIVE) }.padding(horizontal = 27.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(
                                    Modifier.size(97.dp).clip(RoundedCornerShape(30.dp)).background(Color.White),
                                    horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
                                ) {
                                    T(d.day, 20f, if (today) C.Pink else C.Faint, 700)
                                    T("${d.date}", 36f, if (today) C.Pink else C.Text, 600, family = Fredoka, lineHeight = 40f)
                                }
                                Spacer(Modifier.width(30.dp))
                                Column(Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        T(d.title, 27f, C.Text, 700)
                                        if (d.badge != null) { Spacer(Modifier.width(12.dp)); SoftChip(d.badge, C.Green, Color.White) }
                                    }
                                    T("${d.time} · ${d.km}km · ${d.minutes}분", 23f, C.Sub)
                                }
                                Box(Modifier.width(146.dp).height(67.dp).clip(RoundedCornerShape(50)).background(Color.White), contentAlignment = Alignment.Center) {
                                    NumUnit("${d.score}", "점", 36f, if (today) C.Pink else C.Text, 20f, bold = true)
                                }
                            }
                        }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        T("이번 주 요약", 30f, C.Text, 900)
                        T("11.9 월부터 오늘까지", 20f, C.Faint)
                        Spacer(Modifier.height(24.dp))
                        val tiles = listOf("운전" to ("9" to "번"), "달린 거리" to ("126" to "km"), "평균 점수" to ("89.7" to "점"), "급제동" to ("3" to "번"))
                        for (r in 0..1) {
                            Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                                for (c in 0..1) {
                                    val (label, v) = tiles[r * 2 + c]
                                    Column(Modifier.weight(1f).height(188.dp).clip(RoundedCornerShape(27.dp)).background(C.Row).padding(24.dp)) {
                                        T(label, 20f, C.Sub)
                                        Spacer(Modifier.height(12.dp))
                                        NumUnit(v.first, v.second, 48f, C.Text, 21f, bold = true)
                                    }
                                }
                            }
                            Spacer(Modifier.height(18.dp))
                        }
                        Spacer(Modifier.weight(1f))
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(27.dp)).background(C.PinkSoft).padding(24.dp)) {
                            T("목요일이 가장 좋았어요", 21f, C.Pink, 700)
                            T("94점 · 급제동 없이 퇴근", 20f, C.Sub)
                        }
                        Spacer(Modifier.height(18.dp))
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(27.dp)).background(C.Row).clickable { nav.go(Route.SHORT) }.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(data.ME.avatar, 67f)
                            Spacer(Modifier.width(16.dp))
                            Column { T("짧은 운전 1번은 뺐어요", 21f, C.Text, 700); T("집 앞 편의점 1.3km · 순위 제외", 20f, C.Sub) }
                        }
                    }
                }
            },
        )
    }
}

// ───────── 10 이번 운전 상세 ─────────

@Composable
fun DriveDetailScreen(nav: Nav, voice: CoachVoice) {
    val d = data.lastDrive()
    val items = data.lastDriveItems()
    val total = LeagueRules.driveScore(items)
    val minus = items.filter { it.points < 0 }.sumOf { -it.points }
    val plus = items.filter { it.points > 0 }.sumOf { it.points }
    val worst = items.minBy { it.points }
    val coach = "감점이 가장 큰 건 새솔초 앞 ${worst.title}이라 이걸 고칠 점으로 골랐어요"
    Screen(header = { BackHeader("운전 기록", { nav.back() }) }, coach = { CoachBar(coach, voice) }, tabBar = { Tabs(nav, Tab.LOG) }) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(Modifier.weight(1f)) {
                                T("이번 운전 상세", 33f, C.Text, 900)
                                T("${d.title} · ${d.time} · ${d.km}km · ${d.minutes}분", 20f, C.Faint)
                            }
                            NumUnit("$total", "점", 64f, C.Purple, 22f)
                        }
                        Spacer(Modifier.height(24.dp))
                        Picture(R.drawable.map_route, Modifier.fillMaxSize().clip(RoundedCornerShape(36.dp)), ContentScale.Crop)
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        T("항목별 점수", 30f, C.Text, 900)
                        T("10km 기준으로 맞춰 계산해요", 20f, C.Faint)
                        Spacer(Modifier.height(14.dp))
                        items.forEachIndexed { i, it ->
                            ScoreLine(it)
                            if (i < items.lastIndex) Box(Modifier.fillMaxWidth().height(2.dp).background(C.Line))
                        }
                        Spacer(Modifier.weight(1f))
                        Row(
                            Modifier.fillMaxWidth().height(121.dp).clip(RoundedCornerShape(30.dp)).background(C.PurpleSoft).padding(horizontal = 30.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                T("합계", 23f, C.Purple, 700)
                                T("100 − ${minus.one()} + ${plus.one()}", 21f, C.Sub)
                            }
                            NumUnit("$total", "점", 61f, C.Purple, 23f, C.Purple)
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun ScoreLine(item: ScoreItem) {
    val (bg, fg) = when (item.kind) {
        ScoreItem.Kind.SPEED -> C.Row to C.Sub
        ScoreItem.Kind.BRAKE -> C.RedSoft to C.Red
        ScoreItem.Kind.ACCEL -> C.GoldSoft to C.GoldText
        ScoreItem.Kind.MISSED -> Color(0xFFFFF0E0) to C.Orange
        ScoreItem.Kind.EARLY -> C.GreenSoft to C.Green
    }
    Row(Modifier.fillMaxWidth().height(110.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(61.dp).clip(RoundedCornerShape(18.dp)).background(bg), contentAlignment = Alignment.Center) {
            Icon(if (item.kind == ScoreItem.Kind.EARLY) Icons.Filled.Check else if (item.kind == ScoreItem.Kind.SPEED) Icons.Filled.Info else Icons.Filled.Warning, null, tint = fg, modifier = Modifier.size(30.dp))
        }
        Spacer(Modifier.width(22.dp))
        Column(Modifier.weight(1f)) { T(item.title, 23f, C.Text, 700); T(item.detail, 20f, C.Faint) }
        val txt = when { item.points > 0 -> "+${item.points.one()}"; item.points < 0 -> "-${abs(item.points).one()}"; else -> "0" }
        T(txt, 33f, when { item.points > 0 -> C.Green; item.kind == ScoreItem.Kind.BRAKE -> C.Red; item.points < 0 -> C.Text; else -> C.Sub }, 700, family = Fredoka)
    }
}

// ───────── 09 주차 코칭 ─────────

@Composable
fun ParkingScreen(nav: Nav, voice: CoachVoice) {
    val d = data.lastDrive()
    val road = data.retakenRoad()
    val newAvg = LeagueRules.newAverage(road.myAverage, road.myPasses, data.RETAKE_SCORE)
    val league = data.league()
    val me = league.first { it.person == data.ME }
    val top = league.first()
    val coach = "새솔초 앞 급제동 한 번이 4.3점이었어요. 퇴근길엔 미리 천천히 가 봐요"
    LaunchedEffect(Unit) { voice.speak("${d.score}점이에요. ${road.name} 대장 자리를 되찾았어요. $coach") }

    Screen(header = { CloseHeader({ nav.close() }, { nav.demoOpen = true }) }, coach = { CoachBar(coach, voice) }) {
        TwoColumns(
            equal = true,
            left = {
                Card(Modifier.fillMaxSize(), background = C.SoftPurpleCard) {
                    Box(Modifier.fillMaxSize()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Pill("주차 완료", Color.White, C.Purple)
                            Spacer(Modifier.width(14.dp))
                            T("${d.title} ${d.km}km · ${d.minutes}분", 20f, C.Sub)
                            Spacer(Modifier.weight(1f))
                            Row(
                                Modifier.clip(RoundedCornerShape(50)).background(Color.White).clickable { nav.go(Route.DRIVE) }
                                    .padding(start = 22.dp, end = 10.dp, top = 6.dp, bottom = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) { T("운전 자세히 보기", 20f, C.Purple, 700); Chevron(C.Purple, 28f) }
                        }
                        Box(Modifier.offset(x = 88.dp, y = 120.dp).size(358.dp), contentAlignment = Alignment.Center) {
                            Canvas(Modifier.fillMaxSize()) {
                                drawArc(Color.White, 0f, 360f, false, style = Stroke(52.dp.toPx()))
                                drawArc(Brush.sweepGradient(listOf(C.PurpleChar, C.Purple, C.PurpleChar)), -90f, 360f * d.score / 100f, false, style = Stroke(52.dp.toPx(), cap = StrokeCap.Round))
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                T("이번 운전", 23f, C.Sub, 700)
                                T("${d.score}", 130f, C.Text, 700, family = Fredoka, lineHeight = 140f)
                                T("점 / 100", 21f, C.Faint, 700)
                            }
                        }
                        Picture(R.drawable.pose_flag, Modifier.offset(x = 450.dp, y = 360.dp).size(230.dp, 218.dp))
                        Row(Modifier.align(Alignment.BottomStart).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Stat("지난 운전보다", "+3", "점", C.Green, Modifier.weight(1f))
                            Stat("이번 주 평균", me.average.one(), "", C.Text, Modifier.weight(1f))
                            Stat("순위", "2", "위", C.Purple, Modifier.weight(1f))
                        }
                    }
                }
            },
            right = {
                Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                    Card(Modifier.fillMaxWidth().height(261.dp), background = androidx.compose.ui.graphics.SolidColor(C.GreenSoft)) {
                        Row {
                            Column(Modifier.weight(1f)) {
                                Pill("대장 자리 되찾음", Color.White, C.Green)
                                Spacer(Modifier.height(14.dp))
                                T(road.name, 36f, C.Text, 900)
                                T("${data.RETAKE_SCORE}점으로 지나 ${road.leader.name}을 앞질렀어요", 20f, C.Sub)
                                T("내 평균 ${road.myAverage.one()} → ${newAvg.one()}", 21f, C.Green, 700)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxHeight()) {
                                Avatar(road.leader.avatar, 97f)
                                Avatar(data.ME.avatar, 97f, Modifier.outline(Color.White, 29f, 5f))
                            }
                        }
                    }
                    Card(Modifier.fillMaxWidth().height(258.dp)) {
                        Column {
                            Pill("고칠 점 하나", C.RedSoft, C.Red, Icons.Filled.Warning)
                            Spacer(Modifier.height(14.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                T("새솔초등학교 앞 급제동 1번", 33f, C.Text, 900, modifier = Modifier.weight(1f))
                                T("-4.3", 45f, C.Red, 700, family = Fredoka)
                            }
                            T("스쿨존 보너스 구간이라 감점이 2배예요", 20f, C.Sub)
                            Spacer(Modifier.height(8.dp))
                            T("→  다음엔 신호 두 블록 전부터 천천히 멈춰 보세요", 20f, C.Purple, 700)
                        }
                    }
                    Card(Modifier.fillMaxWidth().fillMaxHeight()) {
                        Column {
                            T("이번 주 1위까지", 22f, C.Sub, 700)
                            NumUnit((top.average - me.average).one(), "점 남았어요", 52f, C.Purple, 26f, C.Text)
                            Spacer(Modifier.weight(1f))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Avatar(data.ME.avatar, 49f)
                                Bar(0.92f, C.PinkBar, Modifier.weight(1f).padding(horizontal = 14.dp))
                                Avatar(top.person.avatar, 49f)
                            }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun Stat(label: String, value: String, unit: String, color: Color, modifier: Modifier) {
    Column(
        modifier.height(194.dp).clip(RoundedCornerShape(30.dp)).background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        T(label, 20f, C.Sub)
        Spacer(Modifier.height(10.dp))
        if (unit.isEmpty()) T(value, 48f, color, 700, family = Fredoka)
        else NumUnit(value, unit, 48f, color, 36f, color)
    }
}

// ───────── 24 짧은 운전 · 26 오프라인 ─────────

@Composable
fun ShortDriveScreen(nav: Nav, voice: CoachVoice) {
    val coach = "짧은 운전은 내 기록에만 남겨 둘게요. 다음 운전부터 다시 겨뤄요"
    NoticeScreen(
        nav, voice, coach,
        pose = R.drawable.pose_pointer, circle = Color(0xFFFFE6A8),
        chip = "순위에는 넣지 않았어요", chipBg = C.GoldSoft, chipFg = C.GoldText,
        title = "${LeagueRules.MIN_DRIVE_KM.toInt()}km보다 짧은 운전은\n순위에 넣지 않아요",
        sub = "점수가 너무 크게 흔들리지 않게 하려는 규칙이에요",
        panel = {
            Row { T("이번 운전", 20f, C.Sub, 700); Spacer(Modifier.weight(1f)); T("집 앞 편의점 · 4분", 20f, C.Faint) }
            NumUnit("1.3", "km", 48f, C.Text, 21f)
            Spacer(Modifier.height(10.dp))
            Bar(0.65f, C.Gold, Modifier.fillMaxWidth(), 14f)
        },
        buttons = { PrimaryButton("확인했어요", Modifier.fillMaxWidth().height(97.dp)) { nav.tab(Tab.LOG) } },
    )
}

@Composable
fun OfflineScreen(nav: Nav, voice: CoachVoice) {
    val coach = "인터넷이 다시 연결되면 리그 순위에 바로 반영해 드릴게요"
    NoticeScreen(
        nav, voice, coach,
        pose = R.drawable.pose_basic, circle = Color(0xFFD7E6FB),
        chip = "연결이 끊겼어요", chipBg = C.Row, chipFg = C.Sub,
        title = "점수는 차에\n안전하게 저장했어요",
        sub = "연결되면 ${data.LEAGUE_NAME}에 바로 올릴게요",
        panel = {
            T("올릴 차례를 기다리는 운전", 20f, C.Sub, 700)
            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Color.White).padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(61.dp).clip(RoundedCornerShape(18.dp)).background(C.PurpleSoft), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Refresh, null, tint = C.Purple, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.width(18.dp))
                Column(Modifier.weight(1f)) { T("퇴근길 · 18:42", 23f, C.Text, 700); T("18.2km · 저장됨", 20f, C.Faint) }
                NumUnit("88", "점", 39f, C.Purple, 20f)
            }
        },
        buttons = {
            Row(horizontalArrangement = Arrangement.spacedBy(27.dp)) {
                PrimaryButton("확인했어요", Modifier.weight(1f).height(97.dp)) { nav.tab(Tab.LOG) }
                SecondaryButton("다시 연결하기", Modifier.weight(1f), height = 97f) {}
            }
        },
    )
}

@Composable
fun NoticeScreen(
    nav: Nav, voice: CoachVoice, coach: String,
    pose: Int, circle: Color,
    chip: String, chipBg: Color, chipFg: Color,
    title: String, sub: String,
    panel: @Composable () -> Unit,
    buttons: @Composable () -> Unit,
    header: (@Composable () -> Unit)? = null,
    tabBar: (@Composable () -> Unit)? = null,
) {
    Screen(header = header ?: { CloseHeader({ nav.close() }, { nav.demoOpen = true }) }, coach = { CoachBar(coach, voice) }, tabBar = tabBar) {
        Card(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(516.dp).clip(CircleShape).background(Brush.verticalGradient(listOf(circle.copy(alpha = 0.35f), circle))), contentAlignment = Alignment.Center) {
                    Picture(pose, Modifier.size(370.dp, 350.dp))
                }
                Spacer(Modifier.width(80.dp))
                Column(Modifier.weight(1f)) {
                    Pill(chip, chipBg, chipFg)
                    Spacer(Modifier.height(20.dp))
                    T(title, 52f, C.Text, 900, lineHeight = 70f)
                    Spacer(Modifier.height(10.dp))
                    T(sub, 23f, C.Sub)
                    Spacer(Modifier.height(44.dp))
                    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(36.dp)).background(C.Row).padding(30.dp)) { panel() }
                    Spacer(Modifier.height(36.dp))
                    buttons()
                }
            }
        }
    }
}
