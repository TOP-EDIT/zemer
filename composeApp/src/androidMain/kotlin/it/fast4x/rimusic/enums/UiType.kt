package it.fast4x.rimusic.enums

import androidx.compose.runtime.Composable
import app.kreate.android.Preferences
import me.knighthat.enums.TextView

enum class UiType: TextView {
    RiMusic,
    ViMusic;

    companion object {

        fun current(): UiType = Preferences.MAIN_THEME.value
    }

    override val text: String
        @Composable
        get() = when( this ) { RiMusic -> "סגנון א׳ / Style A"; ViMusic -> "סגנון ב׳ / Style B" }

    fun isCurrent(): Boolean = current() == this

    fun isNotCurrent(): Boolean = !isCurrent()
}