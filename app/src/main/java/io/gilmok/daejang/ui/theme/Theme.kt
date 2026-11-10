package io.gilmok.daejang.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import io.gilmok.daejang.R

/** Figma "길목대장" 디자인 값. 단위는 Pleos Connect-S 17 앱 창(1700×1184 dp) 기준이에요. */
object C {
    val Bg = Color(0xFFEDF2FA)
    val Card = Color.White
    val Text = Color(0xFF1B2233)
    val Sub = Color(0xFF5E687A)
    val Faint = Color(0xFF7D8799)
    val Line = Color(0xFFE3E9F2)
    val Row = Color(0xFFF5F7FB)
    val Purple = Color(0xFF7C4DFF)
    val PurpleSoft = Color(0xFFF1E9FF)
    val PurpleChar = Color(0xFFB07CFF)
    val Pink = Color(0xFFE0457E)
    val PinkBar = Color(0xFFFF78A5)
    val PinkSoft = Color(0xFFFFF0F5)
    val PinkLine = Color(0xFFFFB3CB)
    val Green = Color(0xFF12A37A)
    val GreenBar = Color(0xFF37CFA1)
    val GreenSoft = Color(0xFFE2F8F0)
    val Red = Color(0xFFF25454)
    val RedSoft = Color(0xFFFFECEC)
    val Gold = Color(0xFFFFC23D)
    val GoldSoft = Color(0xFFFFF4D6)
    val GoldText = Color(0xFFB7791F)
    val Orange = Color(0xFFE2770E)
    val OrangeBar = Color(0xFFFF9F43)
    val Blue = Color(0xFF4795E8)

    val PurpleButton = Brush.horizontalGradient(listOf(Color(0xFF9B6BFF), Color(0xFF6A3DF5)))
    val PurpleCard = Brush.linearGradient(listOf(Color(0xFF9466FF), Color(0xFF6A3DF5)))
    val SoftPurpleCard = Brush.verticalGradient(listOf(Color(0xFFF1E9FF), Color(0xFFE6EEFF)))
    val SoftBlueCard = Brush.verticalGradient(listOf(Color(0xFFDDE9FF), Color(0xFFF1E9FF)))
}

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private fun noto(weight: Int) = Font(
    R.font.noto_sans_kr,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

@OptIn(androidx.compose.ui.text.ExperimentalTextApi::class)
private fun fredoka(weight: Int) = Font(
    R.font.fredoka,
    weight = FontWeight(weight),
    variationSettings = FontVariation.Settings(FontVariation.weight(weight)),
)

val Noto = FontFamily(noto(500), noto(700), noto(900))
val Fredoka = FontFamily(fredoka(600), fredoka(700))

/** 화면 배치 기준 (설계 단위) */
object L {
    const val W = 1700f
    const val H = 1184f
    const val X = 122f
    const val CONTENT_W = 1456f
    const val HEADER_Y = 52f
    const val HEADER_H = 73f
    const val BODY_Y = 167f
    const val BODY_H = 838f
    const val COACH_Y = 1035f
    const val COACH_H = 109f
    const val LEFT_W = 940f
    const val RIGHT_W = 485f
    const val GAP = 31f
    const val RADIUS = 42f
    const val PAD = 36f
}
