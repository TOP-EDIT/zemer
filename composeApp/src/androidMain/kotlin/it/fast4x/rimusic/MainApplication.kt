package it.fast4x.rimusic

import android.app.Application
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import androidx.lifecycle.ProcessLifecycleOwner
import app.kreate.android.Preferences
import app.kreate.android.drawable.AppIcon
import app.kreate.android.service.innertube.InnertubeProvider
import app.kreate.android.utils.ConnectivityUtils
import app.kreate.android.utils.CrashHandler
import app.kreate.di.THUMBNAIL_SIZE
import app.kreate.di.initKoin
import app.kreate.logging.CoilLogger
import app.kreate.logging.KoinBufferedLogger
import app.kreate.logging.setupLogging
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.asImage
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.request.crossfade
import app.kreate.android.R
import app.kreate.android.coil3.NoPictures
import it.fast4x.rimusic.utils.AppLifecycleTracker
import kotlinx.coroutines.Dispatchers
import me.knighthat.innertube.Innertube
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext


class MainApplication : Application(), SingletonImageLoader.Factory {

    override fun onCreate() {
        super.onCreate()

        Thread.setDefaultUncaughtExceptionHandler( CrashHandler(this) )

        // Music+: the customer's "only male singers" choice, from KosherSafe on this phone
        app.kreate.android.utils.MenOnly.init( this )

        val koinLogger = KoinBufferedLogger()
        initKoin {
            logger( koinLogger )

            androidContext( this@MainApplication )
        }

        setupLogging( koinLogger )

        // Zemer: these settings make MainActivity restart when they change.
        // Reading them writes their default the first time, which used to
        // restart the screen (and stop the song) on the first play. Touch
        // them here, before any screen listens.
        Preferences.MAIN_THEME.value
        Preferences.NAVIGATION_BAR_POSITION.value
        Preferences.NAVIGATION_BAR_TYPE.value
        Preferences.MINI_PLAYER_TYPE.value

        Innertube.setProvider( InnertubeProvider() )

        // Register network callback
        getSystemService<ConnectivityManager>()?.run {
            val networkRequest: NetworkRequest = NetworkRequest.Builder()
                                                               .addCapability( NetworkCapabilities.NET_CAPABILITY_INTERNET )
                                                               .build()
            registerNetworkCallback( networkRequest, ConnectivityUtils )
        }
        // Register app lifecycle tracker
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleTracker)
    }

    override fun onTerminate() {
        Preferences.unload()

        super.onTerminate()
    }

    override fun newImageLoader( context: PlatformContext ): ImageLoader {
        // Zemer: no pictures at all. No network fetcher, no disk cache,
        // and every non built-in request is refused by [NoPictures].
        val noPicture = androidx.core.content.ContextCompat
            .getDrawable( context, R.drawable.no_picture )
            ?.asImage()
        return ImageLoader.Builder(context)
                          .logger( CoilLogger() )
                          .coroutineContext( Dispatchers.IO )
                          .decoderCoroutineContext( Dispatchers.Default )
                          .crossfade( false )
                          .error( noPicture )
                          .placeholder( noPicture )
                          .fallback( noPicture )
                          .memoryCache( null )
                          .diskCache( null )
                          .components {
                              add( NoPictures( context ) )
                          }
                          .build()
    }
}
