package app.kreate.android.coil3

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Zemer: second safety layer. The app's HTTP client refuses to talk to any
 * server that only serves pictures (thumbnails, covers, avatars), whatever
 * code asks for it.
 */
object PictureHostBlocker : Interceptor {

    private val BLOCKED_SUFFIXES = listOf(
        "ytimg.com",
        "ggpht.com",
        "googleusercontent.com",
        "gravatar.com",
        "avatars.githubusercontent.com",
        "lastfm.freetls.fastly.net",
        "discordapp.com",
        "discordapp.net",
        "discord.com",
        "discord.gg"
    )

    fun isPictureHost( host: String ): Boolean {
        val h = host.lowercase().trimEnd( '.' )
        return BLOCKED_SUFFIXES.any { h == it || h.endsWith( ".$it" ) }
    }

    override fun intercept( chain: Interceptor.Chain ): Response {
        val host = chain.request().url.host
        if( isPictureHost( host ) )
            throw IOException( "Blocked picture server: $host" )
        return chain.proceed( chain.request() )
    }
}
