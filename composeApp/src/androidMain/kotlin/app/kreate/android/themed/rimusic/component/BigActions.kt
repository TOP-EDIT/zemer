package app.kreate.android.themed.rimusic.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.service.player.StatefulPlayer
import app.kreate.database.models.Song
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.forcePlayFromBeginning
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.knighthat.utils.Toaster
import org.koin.java.KoinJavaComponent.inject

/**
 * Music+: the two big buttons on top of every song list ("play all" / "shuffle"), instead of
 * a row of small icons nobody can tell apart.
 */
object BigActions {

    /** The logo's gradient, for the main button. */
    val BRAND = Brush.linearGradient( listOf( Color( 0xFF7B5CFF ), Color( 0xFFD63FA8 ), Color( 0xFFFF8A3D ) ) )

    @Composable
    fun Pill(
        text: String,
        iconId: Int,
        filled: Boolean,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        val shape = RoundedCornerShape( 50 )
        val base = modifier.height( 44.dp ).clip( shape )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = ( if( filled ) base.background( BRAND )
                         else base.border( 1.dp, colorPalette().textDisabled, shape ) )
                .clickable( onClick = onClick )
                .padding( horizontal = 14.dp )
        ) {
            val color = if( filled ) Color.White else colorPalette().text
            Icon( painterResource( iconId ), null, tint = color, modifier = Modifier.size( 18.dp ) )
            Spacer( Modifier.width( 8.dp ) )
            BasicText(
                text = text,
                maxLines = 1,
                style = typography().s.copy( color = color, fontWeight = FontWeight.Bold )
            )
        }
    }

    /** Plays the list in its order, from the first song. */
    @UnstableApi
    fun playAll( songs: List<Song> ) {
        if( songs.isEmpty() ) {
            Toaster.i( R.string.no_song_to_shuffle )
            return
        }
        val max = Preferences.MAX_NUMBER_OF_SONG_IN_QUEUE.value.toInt()
        val items = songs.take( max ).map( Song::asMediaItem )
        CoroutineScope( Dispatchers.Main ).launch {
            val player: StatefulPlayer by inject( StatefulPlayer::class.java )
            player.stopRadio()
            player.forcePlayFromBeginning( items )
        }
    }
}
