package app.kreate.android.themed.rimusic.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import app.kreate.android.Preferences
import app.kreate.android.R
import app.kreate.android.coil3.LetterTile
import app.kreate.android.service.player.StatefulPlayer
import app.kreate.android.utils.MenOnly
import app.kreate.database.models.Song
import it.fast4x.rimusic.Database
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.BuiltInPlaylist
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.utils.asMediaItem
import it.fast4x.rimusic.utils.forcePlayAtIndex
import kotlinx.coroutines.Dispatchers
import org.koin.compose.koinInject
import java.util.Calendar
import java.util.Locale

/**
 * Music+: the home tab. A greeting, one big search box, colored cards for the customer's own
 * music (favorites, downloads, all songs, their lists), their singers as colored circles, and
 * what they played last. Everything comes from the phone's own database: it opens instantly.
 */
@UnstableApi
@Composable
fun MusicPlusHome(
    navController: NavController,
    player: StatefulPlayer = koinInject()
) {
    val he = Locale.getDefault().language.let { it == "iw" || it == "he" }
    fun t( hebrew: String, english: String ) = if( he ) hebrew else english

    val favorites by remember { Database.songTable.allFavorites() }
        .collectAsState( emptyList(), Dispatchers.IO )
    val recentAll by remember { Database.songTable.sortAllByDatePlayed( limit = 30, excludeHidden = true ) }
        .collectAsState( emptyList(), Dispatchers.IO )
    val following by remember { Database.artistTable.allFollowing( 20 ) }
        .collectAsState( emptyList(), Dispatchers.IO )
    val mostPlayed by remember { Database.eventTable.findArtistsMostPlayedBetween( from = 0L, limit = 20 ) }
        .collectAsState( emptyList(), Dispatchers.IO )
    val playlists by remember { Database.playlistTable.sortPreviewsByMostPlayed( 6 ) }
        .collectAsState( emptyList(), Dispatchers.IO )

    val recent = remember( recentAll ) {
        recentAll.filter { !MenOnly.blocked( it.cleanTitle(), it.cleanArtistsText() ) }.take( 8 )
    }
    val singers = remember( following, mostPlayed ) {
        ( following + mostPlayed ).distinctBy { it.id }
            .filter { !it.name.isNullOrBlank() && !MenOnly.blocked( null, it.name ) }
            .take( 12 )
    }

    val greeting = when( Calendar.getInstance().get( Calendar.HOUR_OF_DAY ) ) {
        in 5..11  -> t( "בוקר טוב", "Good morning" )
        in 12..16 -> t( "צהריים טובים", "Good afternoon" )
        in 17..21 -> t( "ערב טוב", "Good evening" )
        else      -> t( "לילה טוב", "Good night" )
    }

    fun openSongs( type: BuiltInPlaylist ) {
        Preferences.HOME_SONGS_TYPE.value = type
        Preferences.HOME_TAB_INDEX.value = 1
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background( colorPalette().background0 ),
        verticalArrangement = Arrangement.spacedBy( 4.dp )
    ) {
        // greeting
        item {
            BasicText(
                text = greeting,
                style = typography().xxl.copy( color = colorPalette().text, fontWeight = FontWeight.Bold ),
                modifier = Modifier.padding( start = 18.dp, end = 18.dp, top = 12.dp, bottom = 8.dp )
            )
        }

        // one big search box
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding( horizontal = 16.dp )
                    .fillMaxWidth()
                    .height( 50.dp )
                    .clip( RoundedCornerShape( 14.dp ) )
                    .background( colorPalette().background1 )
                    .clickable { NavRoutes.search.navigateHere( navController ) }
                    .padding( horizontal = 14.dp )
            ) {
                Icon( painterResource( R.drawable.search ), null, tint = colorPalette().textSecondary,
                      modifier = Modifier.size( 20.dp ) )
                Spacer( Modifier.width( 10.dp ) )
                BasicText(
                    text = t( "חיפוש שיר או זמר", "Search a song or singer" ),
                    style = typography().s.copy( color = colorPalette().textSecondary )
                )
            }
        }

        // the customer's music, as big colored cards
        item { SectionTitle( t( "המוזיקה שלך", "Your music" ) ) }
        item {
            val cards = buildList {
                add( Card( t( "אהובים", "Favorites" ), t( "${favorites.size} שירים", "${favorites.size} songs" ),
                           Color( 0xFF3C3489 ), Color( 0xFFCECBF6 ) ) { openSongs( BuiltInPlaylist.Favorites ) } )
                add( Card( t( "הורדות", "Downloads" ), t( "בלי אינטרנט", "Offline" ),
                           Color( 0xFF0C447C ), Color( 0xFFB5D4F4 ) ) { openSongs( BuiltInPlaylist.Downloaded ) } )
                add( Card( t( "כל השירים", "All songs" ), t( "הספרייה שלך", "Your library" ),
                           Color( 0xFF085041 ), Color( 0xFF9FE1CB ) ) { openSongs( BuiltInPlaylist.All ) } )
                val colors = listOf( Color( 0xFF712B13 ) to Color( 0xFFF5C4B3 ), Color( 0xFF72243E ) to Color( 0xFFF4C0D1 ),
                                     Color( 0xFF633806 ) to Color( 0xFFFAC775 ), Color( 0xFF27500A ) to Color( 0xFFC0DD97 ) )
                playlists.forEachIndexed { i, p ->
                    val (bg, fg) = colors[i % colors.size]
                    add( Card( p.playlist.name, t( "${p.songCount} שירים", "${p.songCount} songs" ), bg, fg ) {
                        NavRoutes.localPlaylist.navigateHere( navController, p.playlist.id.toString() )
                    } )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy( 10.dp ),
                modifier = Modifier.padding( horizontal = 16.dp )
            ) {
                cards.chunked( 2 ).forEach { row ->
                    Row( horizontalArrangement = Arrangement.spacedBy( 10.dp ) ) {
                        row.forEach { c -> CardView( c, Modifier.weight( 1f ) ) }
                        if( row.size == 1 ) Spacer( Modifier.weight( 1f ) )
                    }
                }
            }
        }

        // their singers, as colored circles
        if( singers.isNotEmpty() ) {
            item { SectionTitle( t( "הזמרים שלך", "Your singers" ) ) }
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy( 14.dp ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues( horizontal = 16.dp )
                ) {
                    items( singers, key = { it.id } ) { artist ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width( 76.dp )
                                .clickable { NavRoutes.YT_ARTIST.navigateHere( navController, artist.id ) }
                        ) {
                            LetterTile.Tile( artist.name, Modifier.size( 68.dp ).clip( CircleShape ) )
                            Spacer( Modifier.height( 6.dp ) )
                            BasicText(
                                text = artist.name.orEmpty(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = typography().xs.copy( color = colorPalette().text )
                            )
                        }
                    }
                }
            }
        }

        // what they played last
        if( recent.isNotEmpty() ) {
            item { SectionTitle( t( "השמעת לאחרונה", "Played recently" ) ) }
            itemsIndexed( recent, key = { _, s -> "r" + s.id } ) { index, song ->
                RecentRow( song ) {
                    player.forcePlayAtIndex( recent.map( Song::asMediaItem ), index )
                }
            }
        }

        item { Spacer( Modifier.height( 120.dp ) ) } // room for the mini player
    }
}

private class Card( val title: String, val subtitle: String, val bg: Color, val fg: Color, val onClick: () -> Unit )

@Composable
private fun SectionTitle( text: String ) =
    BasicText(
        text = text,
        style = typography().m.copy( color = colorPalette().text, fontWeight = FontWeight.Bold ),
        modifier = Modifier.padding( start = 18.dp, end = 18.dp, top = 18.dp, bottom = 8.dp )
    )

@Composable
private fun CardView( c: Card, modifier: Modifier ) =
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.height( 76.dp )
            .clip( RoundedCornerShape( 14.dp ) )
            .background( c.bg )
            .clickable( onClick = c.onClick )
            .padding( 12.dp )
    ) {
        BasicText(
            text = c.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = typography().s.copy( color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp )
        )
        BasicText(
            text = c.subtitle,
            maxLines = 1,
            style = typography().xs.copy( color = c.fg )
        )
    }

@Composable
private fun RecentRow( song: Song, onClick: () -> Unit ) =
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .clickable( onClick = onClick )
            .padding( horizontal = 16.dp, vertical = 6.dp )
    ) {
        LetterTile.Tile(
            song.cleanArtistsText().ifBlank { song.cleanTitle() },
            Modifier.size( 48.dp ).clip( RoundedCornerShape( 10.dp ) )
        )
        Spacer( Modifier.width( 12.dp ) )
        Column( Modifier.weight( 1f ) ) {
            BasicText(
                text = song.cleanTitle(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = typography().s.copy( color = colorPalette().text, fontWeight = FontWeight.Bold )
            )
            BasicText(
                text = song.cleanArtistsText(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = typography().xs.copy( color = colorPalette().textSecondary )
            )
        }
    }
