package it.fast4x.rimusic.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import app.kreate.android.coil3.ImageFactory
import coil3.imageLoader
import coil3.request.allowHardware
import coil3.request.bitmapConfig
import coil3.toBitmap
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@UnstableApi
class CoilBitmapLoader(
    private val scope: CoroutineScope
) : BitmapLoader, KoinComponent {

    private val context: Context by inject()

    // Coil3 supports most formats from YT
    override fun supportsMimeType(mimeType: String): Boolean = true

    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> =
        scope.future(Dispatchers.IO) {
            error("Pictures are disabled")
        }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> =
        scope.future( Dispatchers.IO ) {
            error( "Pictures are disabled" )
        }

    // Zemer: no pictures. Never load or decode artwork for the
    // notification, lock screen, Bluetooth or Android Auto.
    override fun loadBitmapFromMetadata( metadata: MediaMetadata ): ListenableFuture<Bitmap>? = null
}
