package app.kreate.android.utils

import android.content.Context
import android.net.Uri
import java.util.Locale

/**
 * Music+: "only male singers", when the customer's store chose it in the KosherSafe portal.
 *
 * KosherSafe on the same phone answers whether it is on, plus extra names from the server
 * (content://com.kosherphone.app.musicrules/rules). Songs whose artist (or title, e.g.
 * "feat. ...") names a female singer from the list, or that carry a word such as "זמרת",
 * are not shown in lists and are skipped by the player.
 *
 * It is a list, not voice recognition: a singer who isn't on the list yet passes until she is
 * added (on the server, no app update needed). Without KosherSafe it is simply off.
 */
object MenOnly {

    private val URI: Uri = Uri.parse( "content://com.kosherphone.app.musicrules/rules" )
    private const val REFRESH_MS = 3 * 60 * 1000L

    /** Well-known female singers (both spellings). The server list adds to this. */
    private val BUILT_IN = listOf(
        "נועה קירל", "Noa Kirel", "עדן בן זקן", "Eden Ben Zaken", "שרית חדד", "Sarit Hadad",
        "ריטה", "Rita", "עפרה חזה", "Ofra Haza", "נטע ברזילי", "Netta", "מירי מסיקה", "Miri Mesika",
        "שירי מימון", "Shiri Maimon", "נינט טייב", "Ninet Tayeb", "קרן פלס", "Keren Peles",
        "דנה אינטרנשיונל", "Dana International", "אחינועם ניני", "Achinoam Nini", "ירדנה ארזי",
        "זהבה בן", "Zehava Ben", "אגם בוחבוט", "Agam Buhbut", "אנה זק", "Anna Zak", "נסרין קדרי",
        "מאיה בוסקילה", "Maya Buskila", "רוני דלומי", "Roni Dalumi", "יהודית רביץ", "חוה אלברשטיין",
        "Taylor Swift", "Adele", "Beyoncé", "Beyonce", "Rihanna", "Dua Lipa", "Ariana Grande",
        "Billie Eilish", "Shakira", "Lady Gaga", "Katy Perry", "Selena Gomez", "Celine Dion",
        "Whitney Houston", "Miley Cyrus", "Olivia Rodrigo", "Sabrina Carpenter", "Nicki Minaj",
    )
    /** Words in an artist name or title that mark a female voice. */
    private val WORDS = listOf(
        "זמרת", "מקהלת בנות", "מקהלת נשים", "female vocal", "girls choir", "women's choir", "womens choir",
    )

    @Volatile private var on = false
    @Volatile private var names: List<String> = emptyList()
    @Volatile private var loadedAt = 0L
    @Volatile private var app: Context? = null

    fun init( context: Context ) {
        app = context.applicationContext
        load() // a quick local question to KosherSafe
    }

    private fun load() {
        val c = app ?: return
        loadedAt = System.currentTimeMillis()
        try {
            c.contentResolver.query( URI, null, null, null, null )?.use { cur ->
                if( cur.moveToFirst() ) {
                    on = cur.getInt( 0 ) == 1
                    val extra = cur.getString( 1 ).orEmpty()
                        .split( ',' ).map { it.trim() }.filter { it.isNotEmpty() }
                    names = ( BUILT_IN + extra ).map { norm( it ) }.distinct()
                    return
                }
            }
            on = false
        } catch( e: Exception ) {
            on = false // no KosherSafe (or an older one): off
        }
    }

    private fun refreshIfOld() {
        if( System.currentTimeMillis() - loadedAt > REFRESH_MS ) {
            loadedAt = System.currentTimeMillis()
            Thread( { load() }, "menonly" ).start()
        }
    }

    private fun norm( s: String ) = s.lowercase( Locale.ROOT ).replace( Regex( "\\s+" ), " " ).trim()

    /** [w] appears in [s] as a whole word (no letter or digit glued to it). */
    private fun hasWord( s: String, w: String ): Boolean {
        var i = s.indexOf( w )
        while( i >= 0 ) {
            val end = i + w.length
            val before = i == 0 || !s[i - 1].isLetterOrDigit()
            val after = end >= s.length || !s[end].isLetterOrDigit()
            if( before && after ) return true
            i = s.indexOf( w, i + 1 )
        }
        return false
    }

    /** True when this song should not be shown or played on this phone. */
    fun blocked( title: String?, artists: String? ): Boolean {
        refreshIfOld()
        if( !on ) return false
        val a = norm( artists.orEmpty() )
        val t = norm( title.orEmpty() )
        for( n in names ) if( hasWord( a, n ) || hasWord( t, n ) ) return true
        for( w in WORDS ) if( a.contains( w ) || t.contains( w ) ) return true
        return false
    }
}
