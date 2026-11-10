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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.data.one
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.components.*
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka
import io.gilmok.daejang.ui.theme.Noto

/**
 * 21 음성 질문.
 * 차에서는 SpeechToText SDK로 질문을 받아 아래 질문 중 하나로 고르고, 답은 기록에서 그대로 꺼내 말해요.
 * (숫자, 장소, 친구 이름은 기록 값을 그대로 쓰고 지어내지 않아요.)
 */
private data class Question(val ask: String, val hint: String, val answerTop: String, val answerRest: String)

@Composable
fun VoiceScreen(nav: Nav, voice: CoachVoice) {
    val league = data.league()
    val me = league.first { it.person == data.ME }
    val top = league.first()
    val drive = data.lastDrive()
    val next = data.nextRoad()
    val gap = (top.average - me.average).one()
    val questions = listOf(
        Question("방금 몇 점이야?", "이번 운전 점수", "방금 운전은 ${drive.score}점이에요", "지난 운전보다 3점 올랐어요"),
        Question("${next.name.substringBefore(" ")} 대장 누구야?", "길마다 지금 대장", "${next.name} 대장은 ${next.leader.name}예요", "나와 ${next.gap.one()}점 차라 ${next.needed}점 이상이면 가져와요"),
        Question("이번 주 몇 등이야?", "리그 순위와 남은 점수", "이번 주 ${data.LEAGUE_NAME} 2위예요", "1위 ${top.person.name}과 ${gap}점 차예요"),
        Question("내일 목표 알려 줘", "가장 가까운 대장 자리", "내일은 ${next.name}이에요", "${next.needed}점 이상으로 지나면 대장이에요"),
        Question("${top.person.name}이랑 몇 점 차이야?", "지금 들은 질문", "${top.person.name}과 ${gap}점 차예요", "주말 동안 부드럽게 달리면\n1위를 따라잡을 수 있어요"),
    )
    var selected by remember { mutableIntStateOf(4) }
    val q = questions[selected]
    val spoken = "${q.answerTop}. ${q.answerRest.replace("\n", " ")}"
    LaunchedEffect(selected) { voice.speak(spoken) }

    Screen(
        header = { CloseHeader({ nav.close() }, { nav.demoOpen = true }) },
        coach = { CoachBar(spoken, voice) },
    ) {
        TwoColumns(
            left = {
                Card(Modifier.fillMaxSize(), background = C.SoftBlueCard) {
                    Box(Modifier.fillMaxSize()) {
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Pill("대장 코치 대답", Color.White, C.Purple)
                            Spacer(Modifier.weight(1f))
                            Pill("“${q.ask}”", C.PinkBar, Color.White, size = 22f)
                        }
                        Box(
                            Modifier.offset(y = 72.dp).fillMaxWidth().height(297.dp).clip(RoundedCornerShape(40.dp)).background(Color.White).padding(horizontal = 40.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(buildAnnotatedString {
                                val base = SpanStyle(fontFamily = Noto, fontWeight = FontWeight(900), fontSize = 39.sp, color = C.Text)
                                withStyle(base) { append(q.answerTop + "\n") }
                                withStyle(base) { append(q.answerRest) }
                            }, lineHeight = 64.sp)
                        }
                        Picture(R.drawable.pose_megaphone, Modifier.offset(x = 17.dp, y = 481.dp).size(253.dp, 237.dp))
                        Column(
                            Modifier.offset(x = 322.dp, y = 419.dp).size(546.dp, 346.dp).clip(RoundedCornerShape(36.dp)).background(Color.White).padding(30.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                        ) {
                            T("이번 주 평균", 22f, C.Sub, 700)
                            listOf(me to C.PinkBar, top to C.OrangeBar).forEach { (m, bar) ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Avatar(m.person.avatar, 67f)
                                    Spacer(Modifier.width(16.dp))
                                    Column(Modifier.weight(1f)) {
                                        T(m.person.name, 21f, C.Text, 700)
                                        Bar(((m.average - 72.7) / 27.3).toFloat(), bar, Modifier.fillMaxWidth(0.85f))
                                    }
                                    T(m.average.one(), 36f, m.person.color, 700, family = Fredoka)
                                }
                            }
                            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(C.PurpleSoft).padding(18.dp)) {
                                T("남은 이틀, 1위까지 ${gap}점", 20f, C.Purple, 700)
                            }
                        }
                    }
                }
            },
            right = {
                Card(Modifier.fillMaxSize()) {
                    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Column {
                            T("이렇게 물어봐요", 30f, C.Text, 900)
                            T("운전 중에도 말로만 물어봐요", 20f, C.Faint)
                        }
                        questions.forEachIndexed { i, it ->
                            val on = i == selected
                            Column(
                                Modifier.fillMaxWidth().height(115.dp).clip(RoundedCornerShape(27.dp))
                                    .background(if (on) C.PinkSoft else C.Row)
                                    .then(if (on) Modifier.outline(C.PinkBar, 27f, 2f) else Modifier)
                                    .clickable { selected = i }.padding(horizontal = 27.dp),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                T("“${it.ask}”", 24f, if (on) C.Pink else C.Text, 700)
                                T(it.hint, 20f, C.Sub)
                            }
                        }
                    }
                }
            },
        )
    }
}
