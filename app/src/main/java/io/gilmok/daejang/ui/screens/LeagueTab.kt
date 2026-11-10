package io.gilmok.daejang.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.data.one
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.Route
import io.gilmok.daejang.ui.components.*
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka

// ───────── 12 리그 (리그 순위 / 성장 순위) ─────────

@Composable
fun LeagueScreen(nav: Nav, voice: CoachVoice, growth: Boolean) {
    val league = data.league()
    val rows = if (growth) league.sortedByDescending { it.growth } else league
    val coach = if (growth) "성장 순위는 지난주의 나와 겨뤄요. 이번 주 성장왕은 바로 나예요"
    else "친구 1과 1.5점 차예요. 주말 동안 부드럽게 달리면 따라잡을 수 있어요"
    val days = listOf(86, 88, 89, 94, 91)

    Screen(header = { Header(nav, Tab.LEAGUE) }, coach = { CoachBar(coach, voice) }) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                T(data.LEAGUE_NAME, 33f, C.Text, 900)
                                T(if (growth) "성장 순위 · 지난주의 나와 겨뤄요" else "리그 순위 · 달린 거리만큼 반영한 이번 주 평균", 20f, C.Faint)
                            }
                            Segmented("리그 순위", "성장 순위", growth, onLeft = { nav.replace(Route.LEAGUE) }, onRight = { nav.replace(Route.GROWTH) })
                        }
                        Spacer(Modifier.height(6.dp))
                        rows.forEachIndexed { i, m ->
                            if (growth) {
                                RankRow("${i + 1}", m.copy(weeklyKm = 99.0), 155f, 97f, right = { GrowthPill(m.growth) }, sub = "지난주 ${m.lastWeek.one()}  →  이번 주 ${m.average.one()}", crown = false, tag = if (i == 0) "성장왕" else null)
                            } else {
                                RankRow(if (m.ranked) "${i + 1}" else "-", m, 155f, 97f, right = { ScoreText(m, 48f) }, sub = leagueSub(m))
                            }
                        }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column {
                        T("내 점수 흐름", 30f, C.Text, 900)
                        T("이번 주 운전 9번", 20f, C.Faint)
                        Spacer(Modifier.height(20.dp))
                        Row(Modifier.height(280.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.Bottom) {
                            val names = listOf("월", "화", "수", "목", "금", "토", "일")
                            names.forEachIndexed { i, n ->
                                val v = days.getOrNull(i)
                                val best = v == days.max()
                                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(48.dp)) {
                                    if (v != null) {
                                        T("$v", 20f, if (best) C.Pink else C.Sub, 700, family = Fredoka)
                                        Box(
                                            Modifier.width(42.dp).height(((v - 70) * 9).dp).clip(RoundedCornerShape(15.dp))
                                                .background(if (best) Brush.verticalGradient(listOf(C.PinkBar, C.Pink)) else Brush.verticalGradient(listOf(Color(0xFFFFD3E2), Color(0xFFFFD3E2)))),
                                        )
                                    } else Box(Modifier.width(42.dp).height(90.dp).outline(C.Line, 15f, 3f))
                                    Spacer(Modifier.height(10.dp))
                                    T(n, 20f, if (best) C.Pink else C.Faint, if (best) 700 else 500)
                                }
                            }
                        }
                        Spacer(Modifier.weight(1f))
                        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(27.dp)).background(C.PinkSoft).padding(24.dp)) {
                            T("목요일이 가장 좋았어요", 21f, C.Pink, 700)
                            T("94점 · 급제동 없이 퇴근", 20f, C.Sub)
                        }
                        Spacer(Modifier.height(24.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                            PrimaryButton("친구 초대", Modifier.weight(1f), Icons.Filled.Add, height = 103f) { nav.go(Route.INVITE) }
                            SecondaryButton("주간 리포트", Modifier.weight(1f), Icons.Filled.Star, bg = C.PurpleSoft, fg = C.Purple, height = 103f) { nav.go(Route.WEEKLY) }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun GrowthPill(g: Double) {
    val up = g >= 0
    Box(
        Modifier.width(188.dp).height(67.dp).clip(RoundedCornerShape(50)).background(if (up) C.GreenSoft else C.RedSoft),
        contentAlignment = Alignment.Center,
    ) { T((if (up) "↑ +" else "↓ −") + kotlin.math.abs(g).one(), 33f, if (up) C.Green else C.Red, 700, family = Fredoka) }
}

// ───────── 05 리그 초대 ─────────

@Composable
fun InviteScreen(nav: Nav, voice: CoachVoice) {
    val code = "482915"
    val coach = "코드는 482 915예요. 친구가 들어오면 바로 알려 드릴게요"
    Screen(header = { BackHeader("리그", { nav.back() }, { nav.go(Route.VOICE) }) }, coach = { CoachBar(coach, voice) }) {
        Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(31.dp)) {
            Card(Modifier.width(940.dp).fillMaxHeight(), background = C.SoftPurpleCard) {
                Column {
                    Pill("리그 초대", Color.White, C.Purple)
                    Spacer(Modifier.height(18.dp))
                    T(data.LEAGUE_NAME, 48f, C.Text, 900)
                    T("친구가 이 코드를 넣으면 바로 같은 리그예요", 21f, C.Sub)
                    Spacer(Modifier.height(30.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        code.forEachIndexed { i, ch ->
                            if (i == 3) { Box(Modifier.padding(horizontal = 14.dp).width(24.dp).height(6.dp).clip(RoundedCornerShape(3.dp)).background(C.PurpleChar)) }
                            Box(Modifier.padding(end = 18.dp).size(121.dp, 146.dp).clip(RoundedCornerShape(27.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                                T("$ch", 72f, C.Purple, 700, family = Fredoka)
                            }
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) { T("참가자", 23f, C.Text, 700); Spacer(Modifier.width(18.dp)); T("4 / 10", 21f, C.Purple, 700, family = Fredoka) }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        data.league().sortedBy { it.person != data.ME }.forEach { m ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) { Avatar(m.person.avatar, 124f); Spacer(Modifier.height(8.dp)); T(m.person.name, 21f, C.Text, 700) }
                        }
                        repeat(2) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.size(124.dp).outline(C.PurpleChar, 37f, 3f), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Add, null, tint = C.PurpleChar, modifier = Modifier.size(40.dp)) }
                                Spacer(Modifier.height(8.dp)); T("초대하기", 20f, C.Faint)
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        SecondaryButton("코드 소리로 듣기", Modifier.weight(1f), Icons.Filled.Notifications, bg = Color.White, fg = C.Purple, height = 97f) { voice.speak("초대 코드는 4, 8, 2, 9, 1, 5예요") }
                        PrimaryButton("휴대폰으로 보내기", Modifier.weight(1f), Icons.Filled.Phone, height = 97f) {}
                    }
                }
            }
            Column(Modifier.width(485.dp), verticalArrangement = Arrangement.spacedBy(30.dp)) {
                Card(Modifier.fillMaxWidth().height(294.dp)) {
                    Column {
                        Pill("이번 주 내기", Color(0xFFFFF0E0), C.Orange)
                        Spacer(Modifier.height(16.dp))
                        T("꼴찌가 커피 사기", 36f, C.Text, 900)
                        T("일요일 밤에 결과가 나와요", 20f, C.Sub)
                        Spacer(Modifier.weight(1f))
                        SecondaryButton("내기 바꾸기", Modifier.fillMaxWidth(), Icons.Filled.Edit, height = 61f) {}
                    }
                }
                Card(Modifier.fillMaxWidth().fillMaxHeight()) {
                    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                        T("이렇게 겨뤄요", 30f, C.Text, 900)
                        Rule(Icons.Filled.Star, "평소 운전 점수로 겨뤄요", "멀리 달린 운전일수록 크게 반영돼요")
                        Rule(Icons.Filled.DateRange, "일주일 50km부터 순위에", "짧은 운전은 순위에 넣지 않아요")
                        Rule(Icons.Filled.Place, "길마다 대장이 있어요", "최근 2주 평균이 가장 높은 사람이 대장")
                        Rule(Icons.Filled.Warning, "스쿨존·사고 다발 구간은 2배", "감점도 가점도 2배로 계산해요")
                    }
                }
            }
        }
    }
}

@Composable
private fun Rule(icon: ImageVector, title: String, sub: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(67.dp).clip(RoundedCornerShape(20.dp)).background(C.PurpleSoft), contentAlignment = Alignment.Center) { Icon(icon, null, tint = C.Purple, modifier = Modifier.size(32.dp)) }
        Spacer(Modifier.width(18.dp))
        Column { T(title, 22f, C.Text, 700); T(sub, 20f, C.Sub) }
    }
}

// ───────── 19 주간 리포트 ─────────

@Composable
fun WeeklyScreen(nav: Nav, voice: CoachVoice) {
    val next = data.nextRoad()
    val coach = "이번 주는 2위에 성장왕까지! 다음 주엔 은행나무길만 잡아 봐요"
    Screen(header = { CloseHeader({ nav.close() }, { nav.demoOpen = true }) }, coach = { CoachBar(coach, voice, label = "대장 코치 · 주간 리포트") }) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize(), background = C.SoftBlueCard) {
                    Box(Modifier.fillMaxSize()) {
                        Column {
                            Pill("11월 둘째 주 시즌 끝", Color.White, C.Purple, Icons.Filled.Star)
                            Spacer(Modifier.height(16.dp))
                            T("이번 주 대장은 친구 1, 나는 2위", 39f, C.Text, 900)
                            T("${data.LEAGUE_NAME} · 4명 중 3명 순위 진입", 21f, C.Sub)
                        }
                        // 시상대
                        Podium(x = 43f, h = 167f, rank = "2", name = "나", top = Color(0xFFFFB3CB), bottom = Color(0xFFFF78A5), char = R.drawable.char_me_full, cw = 156f, ch = 227f, badge = "↑ 성장왕 +4.8")
                        Podium(x = 320f, h = 227f, rank = "1", name = "친구 1", top = Color(0xFFFFCB8E), bottom = Color(0xFFFF9F43), char = R.drawable.char_f1_full, cw = 175f, ch = 255f)
                        Podium(x = 598f, h = 121f, rank = "3", name = "친구 2", top = Color(0xFFA6CCF5), bottom = Color(0xFF4795E8), char = R.drawable.char_f2_full, cw = 163f, ch = 227f)
                        Row(
                            Modifier.align(Alignment.BottomStart).fillMaxWidth().height(91.dp).clip(RoundedCornerShape(30.dp)).background(Color.White).padding(horizontal = 18.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Avatar(data.F3.avatar, 61f)
                            Spacer(Modifier.width(24.dp))
                            T("커피 내기 결과", 21f, C.Sub, 700)
                            Spacer(Modifier.width(40.dp))
                            T("이번 주 커피는 친구 3이 사요", 23f, C.Text, 700, modifier = Modifier.weight(1f))
                            T("50km 미달", 20f, C.Faint)
                        }
                    }
                }
            },
            right = {
                Column(verticalArrangement = Arrangement.spacedBy(30.dp)) {
                    Card(Modifier.fillMaxWidth().height(400.dp)) {
                        Column {
                            Pill("좋아진 점", C.GreenSoft, C.Green, Icons.Filled.ThumbUp)
                            Spacer(Modifier.height(16.dp))
                            T("급가속이 절반으로\n줄었어요", 36f, C.Text, 900, lineHeight = 50f)
                            Spacer(Modifier.weight(1f))
                            CountBar("지난주", 1f, Color(0xFFD7DCE5), "6번", C.Sub)
                            Spacer(Modifier.height(16.dp))
                            CountBar("이번 주", 0.5f, C.GreenBar, "3번", C.Green)
                        }
                    }
                    Card(Modifier.fillMaxWidth().fillMaxHeight()) {
                        Column {
                            Pill("다음 주 목표", C.PurpleSoft, C.Purple)
                            Spacer(Modifier.height(16.dp))
                            T("${next.name}에서\n미리 속도 줄이기", 36f, C.Text, 900, lineHeight = 50f)
                            Row(verticalAlignment = Alignment.Bottom) {
                                T("지금 대장 ${next.leader.name}과 ${next.gap.one()}점 차예요", 20f, C.Sub, modifier = Modifier.weight(1f).padding(bottom = 20.dp))
                                Avatar(next.leader.avatar, 94f)
                            }
                            Spacer(Modifier.weight(1f))
                            PrimaryButton("월요일 출발 때 알려 주기", Modifier.fillMaxWidth(), Icons.Filled.Notifications) { nav.tab(Tab.LEAGUE) }
                        }
                    }
                }
            },
        )
    }
}

@Composable
private fun CountBar(label: String, f: Float, color: Color, value: String, vColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        T(label, 21f, C.Sub, modifier = Modifier.width(100.dp))
        Box(Modifier.width((230 * f).dp).height(24.dp).clip(RoundedCornerShape(50)).background(color))
        Spacer(Modifier.width(16.dp))
        T(value, 33f, vColor, 700)
    }
}

@Composable
private fun Podium(x: Float, h: Float, rank: String, name: String, top: Color, bottom: Color, char: Int, cw: Float, ch: Float, badge: String? = null) {
    val baseY = 640f
    Box(Modifier.offset(x.dp, (baseY - h).dp).size(227.dp, h.dp).clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)).background(Brush.verticalGradient(listOf(top, bottom))), contentAlignment = Alignment.TopCenter) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = if (h < 150f) 6.dp else 20.dp)) {
            T(rank, 64f, Color.White, 700, family = Fredoka, lineHeight = 70f)
            T(name, 20f, Color.White, 700)
        }
    }
    Picture(char, Modifier.offset((x + (227 - cw) / 2).dp, (baseY - h - ch + 10).dp).size(cw.dp, ch.dp))
    if (badge != null) Box(Modifier.offset((x + 36).dp, (baseY - h - ch - 40).dp).clip(RoundedCornerShape(50)).background(C.Green).padding(horizontal = 20.dp, vertical = 8.dp)) { T(badge, 20f, Color.White, 700) }
}

// ───────── 23 리그 없음 ─────────

@Composable
fun NoLeagueScreen(nav: Nav, voice: CoachVoice) {
    val coach = "혼자여도 점수는 쌓여요. 친구가 오면 그 점수로 바로 겨뤄요"
    NoticeScreen(
        nav, voice, coach,
        pose = R.drawable.pose_wave, circle = Color(0xFFE1D0FF),
        chip = "아직 혼자예요", chipBg = C.PurpleSoft, chipFg = C.Purple,
        title = "친구가 오기 전까지는\n지난주의 나와 겨뤄요",
        sub = "리그 순위는 친구가 한 명만 들어와도 바로 열려요",
        panel = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    T("이번 주 성장", 20f, C.Sub, 700)
                    T("+4.8", 52f, C.Green, 700, family = Fredoka)
                    T("지난주 84.9 → 이번 주 89.7", 20f, C.Sub)
                }
                repeat(3) { Box(Modifier.padding(start = 16.dp).size(97.dp).outline(C.PurpleChar, 27f, 3f), contentAlignment = Alignment.Center) { Icon(Icons.Filled.Add, null, tint = C.PurpleChar) } }
            }
        },
        buttons = {
            Row(horizontalArrangement = Arrangement.spacedBy(27.dp)) {
                PrimaryButton("친구 초대하기", Modifier.weight(1f).height(97.dp)) { nav.go(Route.INVITE) }
                SecondaryButton("초대 코드 넣기", Modifier.weight(1f), height = 97f) {}
            }
        },
        header = { Header(nav, Tab.LEAGUE) { StatusChip(rank = "혼자", middle = "성장 +4.8") } },
    )
}
