package app.kreate.android.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics

/**
 * Zemer: phones with the system font size or display zoom turned all the way
 * up cut tab labels and titles. Text still grows with the user's setting,
 * but only up to a size the layouts were designed for.
 */
object ZemerScale {

    const val MAX_FONT_SCALE = 1.15f
    const val MAX_DENSITY_FACTOR = 1.1f

    fun wrap( base: Context ): Context {
        val current = base.resources.configuration
        val cfg = Configuration( current )
        var changed = false

        if( cfg.fontScale > MAX_FONT_SCALE ) {
            cfg.fontScale = MAX_FONT_SCALE
            changed = true
        }

        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ) {
            val maxDpi = (DisplayMetrics.DENSITY_DEVICE_STABLE * MAX_DENSITY_FACTOR).toInt()
            if( cfg.densityDpi > maxDpi ) {
                val ratio = cfg.densityDpi.toFloat() / maxDpi
                cfg.densityDpi = maxDpi
                cfg.screenWidthDp = (cfg.screenWidthDp * ratio).toInt()
                cfg.screenHeightDp = (cfg.screenHeightDp * ratio).toInt()
                cfg.smallestScreenWidthDp = (cfg.smallestScreenWidthDp * ratio).toInt()
                changed = true
            }
        }

        return if( changed ) base.createConfigurationContext( cfg ) else base
    }
}
