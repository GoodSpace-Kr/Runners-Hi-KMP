package good.space.runnershi.util

import android.content.Context
import android.speech.tts.TextToSpeech as NativeTTS

class AndroidTTS(
    context: Context
) : TextToSpeech {
    private var tts: NativeTTS? = null
    private var isInitialized = false

    init {
        tts = NativeTTS(context) { status ->
            if (status == NativeTTS.SUCCESS) isInitialized = true
        }
    }

    override fun speak(text: String) {
        if (isInitialized) {
            tts?.speak(text, NativeTTS.QUEUE_FLUSH, null, null)
        }
    }

    override fun stop() {
        tts?.stop()
    }
}
