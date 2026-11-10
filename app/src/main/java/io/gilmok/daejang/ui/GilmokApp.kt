package io.gilmok.daejang.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.ui.components.Picture
import io.gilmok.daejang.ui.components.T
import io.gilmok.daejang.ui.components.Tab
import io.gilmok.daejang.ui.screens.*
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.L
import kotlinx.coroutines.delay
import kotlin.math.min

enum class Route {
    SPLASH,
    HOME, DRIVER, GOAL,                       // 홈 탭
    MAP, ROAD,                                // 친구 지도 탭
    LOG, DRIVE, PARKING, SHORT, OFFLINE,      // 운전 기록 탭
    LEAGUE, GROWTH, INVITE, WEEKLY, NO_LEAGUE,// 리그 탭
    VOICE,                                    // 가운데 음성 버튼
}

class Nav {
    val stack = mutableStateListOf(Route.SPLASH)
    val current get() = stack.last()
    var demoOpen by mutableStateOf(false)

    fun go(r: Route) { stack.add(r) }
    fun tab(t: Tab) {
        stack.clear(); stack.add(
            when (t) { Tab.HOME -> Route.HOME; Tab.MAP -> Route.MAP; Tab.LOG -> Route.LOG; Tab.LEAGUE -> Route.LEAGUE }
        )
    }
    fun replace(r: Route) { stack.removeAt(stack.lastIndex); stack.add(r) }
    fun back() { if (stack.size > 1) stack.removeAt(stack.lastIndex) else tab(Tab.HOME) }
    fun close() = back()
}

@Composable
fun GilmokApp(voice: CoachVoice) {
    val nav = remember { Nav() }

    LaunchedEffect(Unit) { delay(1600); if (nav.current == Route.SPLASH) nav.replace(Route.HOME) }
    LaunchedEffect(nav.current) { voice.stop() }
    BackHandler(enabled = nav.stack.size > 1) { nav.back() }

    // 1700×1184 설계 크기를 실제 화면에 맞춰 같은 비율로 보여 줘요.
    BoxWithConstraints(Modifier.fillMaxSize().background(C.Bg), contentAlignment = Alignment.Center) {
        val scale = min(constraints.maxWidth / L.W, constraints.maxHeight / L.H)
        CompositionLocalProvider(LocalDensity provides Density(scale, 1f)) {
            Box(Modifier.size(L.W.dp, L.H.dp).clip(RoundedCornerShape(40.dp))) {
                AnimatedContent(nav.current, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "route") { r ->
                    RouteScreen(r, nav, voice)
                }
                if (nav.demoOpen) DemoMenu(nav)
            }
        }
    }
}

@Composable
internal fun RouteScreen(r: Route, nav: Nav, voice: CoachVoice) {
    when (r) {
        Route.SPLASH -> Splash()
        Route.HOME -> HomeScreen(nav, voice)
        Route.DRIVER -> DriverScreen(nav, voice)
        Route.GOAL -> GoalScreen(nav, voice)
        Route.MAP -> MapScreen(nav, voice)
        Route.ROAD -> RoadScreen(nav, voice)
        Route.LOG -> LogScreen(nav, voice)
        Route.DRIVE -> DriveDetailScreen(nav, voice)
        Route.PARKING -> ParkingScreen(nav, voice)
        Route.SHORT -> ShortDriveScreen(nav, voice)
        Route.OFFLINE -> OfflineScreen(nav, voice)
        Route.LEAGUE -> LeagueScreen(nav, voice, growth = false)
        Route.GROWTH -> LeagueScreen(nav, voice, growth = true)
        Route.INVITE -> InviteScreen(nav, voice)
        Route.WEEKLY -> WeeklyScreen(nav, voice)
        Route.NO_LEAGUE -> NoLeagueScreen(nav, voice)
        Route.VOICE -> VoiceScreen(nav, voice)
    }
}

@Composable
private fun Splash() {
    Box(Modifier.fillMaxSize().background(Color(0xFF8DB8F8)), contentAlignment = Alignment.Center) {
        Picture(R.drawable.brand_card, Modifier.size(1184.dp))
    }
}

/**
 * 시연 메뉴: 앱 아이콘을 길게 누르면 열려요.
 * 차에서는 시동, 주차, 일요일 밤 같은 신호로 저절로 뜨는 화면을 에뮬레이터에서 바로 볼 수 있게 해요.
 */
@Composable
private fun DemoMenu(nav: Nav) {
    val items = listOf(
        "시동 켜기 · 운전자 확인" to Route.DRIVER,
        "출발 전 · 오늘의 목표" to Route.GOAL,
        "주차 · 주차 코칭" to Route.PARKING,
        "2km보다 짧게 달리고 주차" to Route.SHORT,
        "인터넷 끊긴 채 주차" to Route.OFFLINE,
        "일요일 밤 · 주간 리포트" to Route.WEEKLY,
        "리그에 친구가 없을 때" to Route.NO_LEAGUE,
    )
    Box(
        Modifier.fillMaxSize().background(Color(0x99131417)).clickable { nav.demoOpen = false },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.width(720.dp).clip(RoundedCornerShape(40.dp)).background(Color.White).padding(40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            T("시연 메뉴", 40f, C.Text, 900)
            T("차에서 저절로 뜨는 화면을 바로 열어 봐요", 22f, C.Sub)
            Spacer(Modifier.height(12.dp))
            items.forEach { (label, route) ->
                Box(
                    Modifier.clip(RoundedCornerShape(20.dp)).background(C.Row)
                        .clickable { nav.demoOpen = false; nav.go(route) }
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                ) { T(label, 26f, C.Text, 700) }
            }
        }
    }
}
