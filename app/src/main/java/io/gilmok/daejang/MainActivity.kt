package io.gilmok.daejang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.gilmok.daejang.coach.CoachVoice
import io.gilmok.daejang.ui.GilmokApp

class MainActivity : ComponentActivity() {

    private lateinit var voice: CoachVoice

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        io.gilmok.daejang.ui.theme.Fonts.init(this)
        voice = CoachVoice(this)
        setContent { GilmokApp(voice) }
    }

    override fun onDestroy() {
        voice.release()
        super.onDestroy()
    }
}
