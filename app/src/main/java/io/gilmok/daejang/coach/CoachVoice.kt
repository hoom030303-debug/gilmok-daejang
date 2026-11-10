package io.gilmok.daejang.coach

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

/**
 * 대장 코치 목소리.
 * 차에서는 Pleos TextToSpeech SDK로 바꿔 끼우고, 에뮬레이터와 휴대폰에서는 안드로이드 기본 TTS를 써요.
 */
class CoachVoice(context: Context) {

    /** 지금 말하고 있는 문장. 코치 바의 재생/일시정지 표시에 써요. */
    val speaking = mutableStateOf<String?>(null)

    private var ready = false
    private val tts: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            tts.language = Locale.KOREAN
            tts.setSpeechRate(1.0f)
        }
    }

    init {
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { speaking.value = null }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) { speaking.value = null }
        })
    }

    fun toggle(text: String) {
        if (speaking.value == text) stop() else speak(text)
    }

    fun speak(text: String) {
        speaking.value = text
        if (ready) tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, text.hashCode().toString())
    }

    fun stop() {
        speaking.value = null
        if (ready) tts.stop()
    }

    fun release() {
        tts.stop()
        tts.shutdown()
    }
}
