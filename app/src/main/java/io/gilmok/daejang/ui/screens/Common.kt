package io.gilmok.daejang.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.gilmok.daejang.data.DemoVehicleDataSource
import io.gilmok.daejang.data.LeagueMember
import io.gilmok.daejang.data.one
import io.gilmok.daejang.ui.Nav
import io.gilmok.daejang.ui.Route
import io.gilmok.daejang.ui.components.Avatar
import io.gilmok.daejang.ui.components.T
import io.gilmok.daejang.ui.components.Tab
import io.gilmok.daejang.ui.components.TabHeader
import io.gilmok.daejang.ui.components.outline
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka

val data = DemoVehicleDataSource

@Composable
fun Header(nav: Nav, tab: Tab, status: @Composable () -> Unit = { io.gilmok.daejang.ui.components.StatusChip() }) = TabHeader(
    current = tab,
    onTab = { nav.tab(it) },
    onMic = { nav.go(Route.VOICE) },
    onDemo = { nav.demoOpen = true },
    status = status,
)

@Composable
fun Chevron(color: Color = C.Faint, size: Float = 34f) =
    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = color, modifier = Modifier.size(size.dp))

/** 리그 순위 한 줄 (홈과 리그 탭에서 같이 써요) */
@Composable
fun RankRow(rank: String, m: LeagueMember, height: Float, avatar: Float, right: @Composable () -> Unit, sub: String, crown: Boolean = rank == "1", tag: String? = null) {
    val me = m.person == data.ME
    val faded = !m.ranked
    Row(
        Modifier.fillMaxWidth().height(height.dp).clip(RoundedCornerShape(33.dp))
            .background(if (me) C.PinkSoft else C.Row)
            .then(if (me) Modifier.outline(C.PinkLine, 33f, 3f) else Modifier)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        T(rank, 33f, if (me) C.Pink else if (faded) C.Faint else C.Text, 700, family = Fredoka, modifier = Modifier.width(40.dp))
        Avatar(m.person.avatar, avatar, alpha = if (faded) 0.55f else 1f)
        Spacer(Modifier.width(22.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                T(m.person.name, 26f, if (faded) C.Faint else C.Text, 700)
                if (crown) { Spacer(Modifier.width(8.dp)); Icon(Icons.Filled.Star, null, tint = C.Gold, modifier = Modifier.size(26.dp)) }
                if (tag != null) { Spacer(Modifier.width(10.dp)); SoftChip(tag, C.Green, Color.White) }
            }
            T(sub, 20f, C.Faint)
        }
        right()
    }
}

fun leagueSub(m: LeagueMember) =
    if (m.ranked) "${m.weeklyKm.toInt()}km · 지킨 길 ${m.roadsHeld}"
    else "50km까지 ${(50 - m.weeklyKm).toInt()}km 남음"

@Composable
fun ScoreText(m: LeagueMember, size: Float = 45f) {
    val me = m.person == data.ME
    T(m.average.one(), size, if (me) C.Pink else if (!m.ranked) C.Faint else C.Text, 700, family = Fredoka)
}

@Composable
fun SoftChip(text: String, bg: Color, fg: Color) {
    Box(Modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 18.dp, vertical = 6.dp)) {
        T(text, 20f, fg, 700)
    }
}

@Composable
fun Clickable(onClick: () -> Unit, content: @Composable () -> Unit) =
    Box(Modifier.clickable(onClick = onClick)) { content() }
