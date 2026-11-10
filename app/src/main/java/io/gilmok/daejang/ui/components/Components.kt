package io.gilmok.daejang.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import io.gilmok.daejang.R
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.data.DemoVehicleDataSource
import io.gilmok.daejang.ui.theme.C
import io.gilmok.daejang.ui.theme.Fredoka
import io.gilmok.daejang.ui.theme.L
import io.gilmok.daejang.ui.theme.Noto

// ───────── 글자 ─────────

@Composable
fun T(
    text: String,
    size: Float,
    color: Color = C.Text,
    weight: Int = 500,
    modifier: Modifier = Modifier,
    family: FontFamily = Noto,
    align: TextAlign? = null,
    lineHeight: Float? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        maxLines = maxLines,
        style = TextStyle(
            fontFamily = if (family === Fredoka && weight >= io.gilmok.daejang.ui.theme.BOLD_NUM) io.gilmok.daejang.ui.theme.Fonts.fredokaBold else family,
            fontWeight = if (family === Fredoka) FontWeight.Normal else FontWeight(weight),
            fontSynthesis = androidx.compose.ui.text.font.FontSynthesis.None,
            fontSize = size.sp,
            color = color,
            lineHeight = (lineHeight ?: (size * 1.4f)).sp,
            textAlign = align ?: TextAlign.Unspecified,
        ),
    )
}

/** 숫자(Fredoka) + 단위(Noto) */
@Composable
fun NumUnit(num: String, unit: String, size: Float, color: Color, unitSize: Float = 21f, unitColor: Color = C.Sub, modifier: Modifier = Modifier, bold: Boolean = false) {
    Text(
        modifier = modifier,
        text = buildAnnotatedString {
            withStyle(SpanStyle(fontFamily = if (bold) io.gilmok.daejang.ui.theme.Fonts.fredokaBold else Fredoka,fontWeight = FontWeight.Normal, fontSynthesis = androidx.compose.ui.text.font.FontSynthesis.None, fontSize = size.sp, color = color)) { append(num) }
            withStyle(SpanStyle(fontFamily = Noto, fontWeight = FontWeight(700), fontSize = unitSize.sp, color = unitColor)) { append(" $unit") }
        },
    )
}

// ───────── 상자 ─────────

@Composable
fun Card(
    modifier: Modifier = Modifier,
    background: Brush = SolidColor(C.Card),
    radius: Float = L.RADIUS,
    padding: Float = L.PAD,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(radius.dp)
    Box(
        modifier
            .shadow(10.dp, shape, ambientColor = Color(0x14334C80), spotColor = Color(0x14334C80))
            .clip(shape)
            .background(background)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(padding.dp),
    ) { content() }
}

@Composable
fun Pill(
    text: String,
    bg: Color,
    fg: Color,
    icon: ImageVector? = null,
    size: Float = 20f,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier.clip(RoundedCornerShape(50)).background(bg).padding(horizontal = 18.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = fg, modifier = Modifier.size((size + 2).dp))
            Spacer(Modifier.width(8.dp))
        }
        T(text, size, fg, 700)
    }
}

@Composable
fun Avatar(@DrawableRes res: Int, size: Float, modifier: Modifier = Modifier, alpha: Float = 1f) {
    Image(
        painterResource(res), null,
        modifier = modifier.size(size.dp).clip(RoundedCornerShape((size * 0.3f).dp)),
        contentScale = ContentScale.Crop,
        alpha = alpha,
    )
}

@Composable
fun Picture(@DrawableRes res: Int, modifier: Modifier = Modifier, scale: ContentScale = ContentScale.Fit) {
    Image(painterResource(res), null, modifier = modifier, contentScale = scale)
}

@Composable
fun Bar(fraction: Float, color: Color, modifier: Modifier = Modifier, height: Float = 12f) {
    Box(modifier.height(height.dp).clip(RoundedCornerShape(50)).background(C.Row)) {
        Box(Modifier.fillMaxHeight().fillMaxWidth(fraction.coerceIn(0f, 1f)).clip(RoundedCornerShape(50)).background(color))
    }
}

@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, height: Float = 73f, onClick: () -> Unit) {
    Row(
        modifier.height(height.dp).clip(RoundedCornerShape(24.dp)).background(C.PurpleButton).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) { Icon(icon, null, tint = Color.White, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(10.dp)) }
        T(text, 24f, Color.White, 700)
    }
}

@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, icon: ImageVector? = null, bg: Color = C.Row, fg: Color = C.Text, height: Float = 73f, onClick: () -> Unit) {
    Row(
        modifier.height(height.dp).clip(RoundedCornerShape(24.dp)).background(bg).clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) { Icon(icon, null, tint = fg, modifier = Modifier.size(28.dp)); Spacer(Modifier.width(10.dp)) }
        T(text, 24f, fg, 700)
    }
}

@Composable
fun Segmented(left: String, right: String, rightSelected: Boolean, onLeft: () -> Unit, onRight: () -> Unit) {
    Row(
        Modifier.height(61.dp).clip(RoundedCornerShape(50)).background(Color(0xFFF1F3F8)).padding(6.dp),
    ) {
        listOf(left to !rightSelected, right to rightSelected).forEachIndexed { i, (label, on) ->
            Box(
                Modifier.width(140.dp).fillMaxHeight().clip(RoundedCornerShape(50))
                    .background(if (on) Color.White else Color.Transparent)
                    .clickable { if (i == 0) onLeft() else onRight() },
                contentAlignment = Alignment.Center,
            ) { T(label, 20f, if (on) C.Text else C.Faint, if (on) 700 else 500) }
        }
    }
}

// ───────── 머리줄 ─────────

enum class Tab(val label: String) { HOME("홈"), MAP("친구 지도"), LOG("운전 기록"), LEAGUE("리그") }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTitle(onLongPressLogo: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painterResource(R.drawable.ic_logo), "길목대장",
            modifier = Modifier.size(73.dp).clip(RoundedCornerShape(16.dp))
                .combinedClickable(onClick = {}, onLongClick = onLongPressLogo),
        )
        Spacer(Modifier.width(18.dp))
        Text(buildAnnotatedString {
            withStyle(SpanStyle(fontFamily = Noto, fontWeight = FontWeight(900), fontSize = 40.sp, color = C.Text)) { append("길목") }
            withStyle(SpanStyle(fontFamily = Noto, fontWeight = FontWeight(900), fontSize = 40.sp, color = C.Purple)) { append("대장") }
        })
    }
}

@Composable
fun TabHeader(onDemo: () -> Unit, status: @Composable () -> Unit = { StatusChip() }) {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        AppTitle(onDemo)
        Spacer(Modifier.weight(1f))
        status()
    }
}

/** 하단 탭 (홈 · 친구 지도 · 가운데 마이크 · 운전 기록 · 리그) */
@Composable
fun BottomTabs(current: Tab, onTab: (Tab) -> Unit, onMic: () -> Unit) {
    val icons = mapOf(Tab.HOME to R.drawable.tab_home, Tab.MAP to R.drawable.tab_map, Tab.LOG to R.drawable.tab_log, Tab.LEAGUE to R.drawable.tab_league)
    @Composable
    fun RowScope.Slot(tab: Tab) {
        val on = tab == current
        Box(
            Modifier.weight(1f).fillMaxHeight()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onTab(tab) },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                Modifier.size(208.dp, 97.dp).clip(RoundedCornerShape(31.dp)).background(if (on) C.PurpleSoft else Color.Transparent),
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    painterResource(icons.getValue(tab)), tab.label, modifier = Modifier.size(37.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(if (on) C.Purple else C.Sub),
                )
                Spacer(Modifier.height(4.dp))
                T(tab.label, 20f, if (on) C.Purple else C.Sub, if (on) 700 else 500)
            }
        }
    }
    Row(Modifier.fillMaxSize().clip(RoundedCornerShape(42.dp)).background(Color.White), verticalAlignment = Alignment.CenterVertically) {
        Slot(Tab.HOME)
        Slot(Tab.MAP)
        Box(Modifier.width(110.dp), contentAlignment = Alignment.Center) {
            Box(
                Modifier.size(110.dp).shadow(16.dp, CircleShape, ambientColor = C.Purple, spotColor = C.Purple)
                    .clip(CircleShape).background(C.PurpleButton).clickable(onClick = onMic),
                contentAlignment = Alignment.Center,
            ) { MicGlyph(Color.White, 46f) }
        }
        Slot(Tab.LOG)
        Slot(Tab.LEAGUE)
    }
}

@Composable
fun BackHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(73.dp).clip(CircleShape).background(Color.White).clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로 가기", tint = C.Text, modifier = Modifier.size(34.dp)) }
        Spacer(Modifier.width(18.dp))
        T(title, 36f, C.Text, 900)
        Spacer(Modifier.weight(1f))
        StatusChip()
    }
}

@Composable
fun CloseHeader(onClose: () -> Unit, onDemo: () -> Unit) {
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
        AppTitle(onDemo)
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.height(73.dp).clip(RoundedCornerShape(50)).background(Color.White).clickable(onClick = onClose).padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            T("닫기", 23f, C.Text, 700)
            Spacer(Modifier.width(10.dp))
            Icon(Icons.Filled.Close, "닫기", tint = C.Text, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun MicButton(onClick: () -> Unit) {
    Box(
        Modifier.size(76.dp).shadow(12.dp, CircleShape, ambientColor = C.Purple, spotColor = C.Purple)
            .clip(CircleShape).background(C.PurpleButton).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { MicGlyph(Color.White, 34f) }
}

/** 마이크 모양 (기본 아이콘 묶음에 없어서 직접 그려요) */
@Composable
fun MicGlyph(color: Color, size: Float) {
    androidx.compose.foundation.Canvas(Modifier.size(size.dp)) {
        val w = this.size.width; val h = this.size.height
        val sw = w * 0.09f
        drawRoundRect(color, topLeft = androidx.compose.ui.geometry.Offset(w * 0.34f, h * 0.06f), size = androidx.compose.ui.geometry.Size(w * 0.32f, h * 0.56f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(w * 0.16f))
        drawArc(color, 0f, 180f, false, topLeft = androidx.compose.ui.geometry.Offset(w * 0.2f, h * 0.3f), size = androidx.compose.ui.geometry.Size(w * 0.6f, h * 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(sw, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        drawLine(color, androidx.compose.ui.geometry.Offset(w / 2, h * 0.8f), androidx.compose.ui.geometry.Offset(w / 2, h * 0.94f), sw, androidx.compose.ui.graphics.StrokeCap.Round)
    }
}

@Composable
fun StatusChip(
    rank: String = "2위",
    middle: String = "1위까지 1.5점",
    rankColor: Color = C.Pink,
) {
    Row(
        Modifier.height(73.dp).clip(RoundedCornerShape(50)).background(Color.White).padding(start = 12.dp, end = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Avatar(R.drawable.av_me, 49f)
        Spacer(Modifier.width(14.dp))
        T(rank, 24f, rankColor, 900)
        Divider()
        T(middle, 22f, C.Sub)
        Divider()
        Icon(Icons.Filled.DateRange, null, tint = C.Faint, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(8.dp))
        T(DemoVehicleDataSource.TIME_LEFT, 22f, C.Sub)
    }
}

@Composable
private fun Divider() {
    Spacer(Modifier.width(16.dp))
    Box(Modifier.width(2.dp).height(24.dp).background(C.Line))
    Spacer(Modifier.width(16.dp))
}

// ───────── 대장 코치 바 ─────────

@Composable
fun CoachBar(
    text: String,
    voice: CoachVoice,
    label: String = "대장 코치",
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    val speaking = voice.speaking.value == text
    Row(
        Modifier.fillMaxSize().clip(RoundedCornerShape(36.dp)).background(Color.White).padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(R.drawable.coach_profile), null, modifier = Modifier.size(73.dp))
        Spacer(Modifier.width(18.dp))
        Column(Modifier.weight(1f)) {
            T(if (speaking) "$label · 말하는 중" else label, 20f, C.Purple, 700)
            T(text, 26f, C.Text, 700, maxLines = 1)
        }
        Wave(active = speaking)
        Spacer(Modifier.width(24.dp))
        if (trailing != null) trailing() else {
            Box(
                Modifier.size(73.dp).clip(CircleShape).background(C.Purple).clickable { voice.toggle(text) },
                contentAlignment = Alignment.Center,
            ) {
                if (speaking) Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(2) { Box(Modifier.width(7.dp).height(26.dp).clip(RoundedCornerShape(3.dp)).background(Color.White)) }
                } else Icon(Icons.Filled.PlayArrow, "코치 말 듣기", tint = Color.White, modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
private fun Wave(active: Boolean) {
    val heights = listOf(15, 27, 39, 21, 33, 45, 24, 36, 18, 30, 12)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        heights.forEachIndexed { i, h ->
            Box(
                Modifier.width(6.dp).height(h.dp).clip(RoundedCornerShape(3.dp))
                    .background(if (i < 6) C.Purple else C.PurpleChar)
                    .then(if (active) Modifier else Modifier),
            )
        }
    }
}

// ───────── 화면 틀 ─────────

/** 본문 칸 나누기 값. 하단 탭이 있는 화면은 전체를 줄여 그리므로 설계 단위 폭이 달라요. */
data class Lay(val contentW: Float, val leftW: Float, val rightW: Float, val gap: Float, val equalGap: Float)

private val PopupLay = Lay(L.CONTENT_W, L.LEFT_W, L.RIGHT_W, L.GAP, L.GAP)

/** 하단 탭 화면 배율: Figma 탭 화면(본문 732)과 저절로 뜨는 화면(본문 838)의 비율 */
private const val TAB_SCALE = 732f / 838f
private val TabLay = Lay(1456f / TAB_SCALE, 943f / TAB_SCALE, 487f / TAB_SCALE, 26f / TAB_SCALE, 32f / TAB_SCALE)

val LocalLay = androidx.compose.runtime.staticCompositionLocalOf { PopupLay }

/**
 * 1700×1184 앱 창 안에 머리줄, 본문, 코치 바(, 하단 탭)를 Figma 위치에 놓아요.
 * - 저절로 뜨는 화면(tabBar = null): 머리줄 52 · 본문 167~1005 · 코치 바 1035
 * - 탭·상세 화면: 머리줄 43 · 본문 144~876 · 코치 바 903 · 하단 탭 1025 (전체를 0.87배로 그려요)
 */
@Composable
fun Screen(
    header: @Composable () -> Unit,
    coach: (@Composable () -> Unit)?,
    tabBar: (@Composable () -> Unit)? = null,
    body: @Composable () -> Unit,
) {
    if (tabBar == null) {
        androidx.compose.runtime.CompositionLocalProvider(LocalLay provides PopupLay) {
            Box(Modifier.size(L.W.dp, L.H.dp).background(C.Bg)) {
                Box(Modifier.offset(L.X.dp, L.HEADER_Y.dp).size(L.CONTENT_W.dp, L.HEADER_H.dp)) { header() }
                val bodyH = if (coach == null) L.BODY_H + L.COACH_H + 30f else L.BODY_H
                Box(Modifier.offset(L.X.dp, L.BODY_Y.dp).size(L.CONTENT_W.dp, bodyH.dp)) { body() }
                if (coach != null) Box(Modifier.offset(L.X.dp, L.COACH_Y.dp).size(L.CONTENT_W.dp, L.COACH_H.dp)) { coach() }
            }
        }
        return
    }
    val d = androidx.compose.ui.platform.LocalDensity.current
    val s = TAB_SCALE
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.compose.ui.platform.LocalDensity provides androidx.compose.ui.unit.Density(d.density * s, d.fontScale),
        LocalLay provides TabLay,
    ) {
        val w = TabLay.contentW
        Box(Modifier.size((L.W / s).dp, (L.H / s).dp).background(C.Bg)) {
            Box(Modifier.offset((122f / s).dp, (43f / s).dp).size(w.dp, (64f / s).dp)) { header() }
            Box(Modifier.offset((122f / s).dp, (144f / s).dp).size(w.dp, (732f / s).dp)) { body() }
            if (coach != null) Box(Modifier.offset((122f / s).dp, (903f / s).dp).size(w.dp, (96f / s).dp)) { coach() }
            Box(Modifier.offset((122f / s).dp, (1025f / s).dp).size(w.dp, (117f / s).dp)) { tabBar() }
        }
    }
}

/** 왼쪽 큰 카드 + 오른쪽 좁은 카드 (equal = 같은 너비 두 칸) */
@Composable
fun TwoColumns(equal: Boolean = false, left: @Composable () -> Unit, right: @Composable () -> Unit) {
    val lay = LocalLay.current
    val gap = if (equal) lay.equalGap else lay.gap
    val half = (lay.contentW - gap) / 2f
    Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(gap.dp)) {
        Box(Modifier.width((if (equal) half else lay.leftW).dp).fillMaxHeight()) { left() }
        Box(Modifier.width((if (equal) half else lay.rightW).dp).fillMaxHeight()) { right() }
    }
}

fun Modifier.outline(color: Color, radius: Float, width: Float = 2f) =
    this.border(width.dp, color, RoundedCornerShape(radius.dp))
