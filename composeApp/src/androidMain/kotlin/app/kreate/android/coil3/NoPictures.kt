package app.kreate.android.coil3

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import app.kreate.android.R
import coil3.Image
import coil3.asImage
import coil3.intercept.Interceptor
import coil3.request.ErrorResult
import coil3.request.ImageResult

/**
 * Zemer: the app never shows pictures.
 *
 * Every image request that is not one of the app's own built-in drawables
 * (network URLs, content:// album art, files, anything else) is answered
 * immediately with the neutral [R.drawable.no_picture] placeholder and is
 * never fetched. The image loader also has no network fetcher at all, so
 * even a request that slipped past this interceptor could not reach the
 * network.
 */
class NoPictures( context: Context ) : Interceptor {

    private val placeholder: Image? =
        ContextCompat.getDrawable( context, R.drawable.no_picture )?.asImage()

    override suspend fun intercept( chain: Interceptor.Chain ): ImageResult {
        val data = chain.request.data
        val builtIn = data is Int || data is Drawable || data is Bitmap
        return if( builtIn )
            chain.proceed()
        else
            ErrorResult( placeholder, chain.request, BlockedPicture )
    }

    object BlockedPicture : RuntimeException( "Pictures are disabled in Zemer" ) {
        private fun readResolve(): Any = BlockedPicture
    }
}
