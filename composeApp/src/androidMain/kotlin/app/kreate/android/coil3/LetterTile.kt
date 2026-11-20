package app.kreate.android.coil3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

/**
 * Music+: instead of a picture, every artist gets a colored tile with the first letter of
 * their name. The same name always gets the same color, so an artist is recognised at a
 * glance across the whole app. Computed on the phone, nothing is loaded.
 */
object LetterTile {

    /** Pairs of (tile, letter): bright fills with a dark letter of the same family. */
    private val PALETTE = listOf(
        0xFFEF9F27 to 0xFF412402, // amber
        0xFF7F77DD to 0xFF26215C, // purple
        0xFF5DCAA5 to 0xFF04342C, // teal
        0xFFED93B1 to 0xFF4B1528, // pink
        0xFF85B7EB to 0xFF042C53, // blue
        0xFFF0997B to 0xFF4A1B0C, // coral
        0xFF97C459 to 0xFF173404, // green
        0xFFAFA9EC to 0xFF26215C, // lavender
        0xFFFAC775 to 0xFF412402, // sand
        0xFF5DB5E0 to 0xFF04263A, // sky
    )

    /** The first artist of "A, B & C" / "A feat. B", trimmed. */
    fun mainName( text: String? ): String {
        if( text.isNullOrBlank() ) return ""
        return text.split( ",", "&", " feat", " ft.", " x ", "•", "·", "|" )
                   .first()
                   .trim()
    }

    fun colors( name: String ): Pair<Color, Color> {
        val key = mainName( name ).lowercase()
        if( key.isEmpty() ) return Color( 0xFF3A3D45 ) to Color( 0xFFD3D1C7 )
        val (bg, fg) = PALETTE[Math.floorMod( key.hashCode(), PALETTE.size )]
        return Color( bg ) to Color( fg )
    }

    /** The first letter (Hebrew or any script), or a note when the name has none. */
    fun letter( name: String ): String =
        mainName( name ).replace( Regex( "^(?i)the\\s+(?=\\S)" ), "" ) // "The Double Album" -> D, not T
            .firstOrNull { it.isLetterOrDigit() }?.uppercaseChar()?.toString() ?: "♪"

    @Composable
    fun Tile( name: String?, modifier: Modifier = Modifier ) {
        val label = name.orEmpty()
        val (bg, fg) = remember( label ) { colors( label ) }
        val ch = remember( label ) { letter( label ) }
        BoxWithConstraints(
            contentAlignment = Alignment.Center,
            modifier = modifier.background( bg )
        ) {
            val size = with( LocalDensity.current ) { ( minOf( maxWidth, maxHeight ) * 0.46f ).toSp() }
            Text(
                text = ch,
                color = fg,
                fontSize = size,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
