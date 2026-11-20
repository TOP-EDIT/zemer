package it.fast4x.rimusic.ui.components.navigation.header

import androidx.compose.ui.text.withStyle
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import app.kreate.android.R
import app.kreate.android.drawable.AppIcon
import it.fast4x.rimusic.colorPalette
import it.fast4x.rimusic.enums.NavRoutes
import it.fast4x.rimusic.typography
import it.fast4x.rimusic.ui.components.themed.Button
import it.fast4x.rimusic.utils.semiBold
import me.knighthat.utils.Toaster

@Composable
private fun AppLogo(
    navController: NavController,
    context: Context
) {
    Image(
        bitmap = AppIcon.Round.imageBitmap( LocalContext.current ),
        contentDescription = null,
        modifier = Modifier.size( 34.dp )
    )
}

// Music+: the brand name as text, never cut: "Music" in the theme color and a "+" in the logo's
// purple-pink-orange gradient. Always left-to-right (in Hebrew it would otherwise read "+Music").
@Composable
private fun AppLogoText( navController: NavController ) {
    BasicText(
        text = androidx.compose.ui.text.buildAnnotatedString {
            append( "Music" )
            withStyle(
                androidx.compose.ui.text.SpanStyle(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(
                            androidx.compose.ui.graphics.Color( 0xFF7B5CFF ),
                            androidx.compose.ui.graphics.Color( 0xFFD63FA8 ),
                            androidx.compose.ui.graphics.Color( 0xFFFF8A3D )
                        )
                    ),
                    baselineShift = androidx.compose.ui.text.style.BaselineShift( 0.25f )
                )
            ) { append( "+" ) }
        },
        maxLines = 1,
        softWrap = false,
        style = TextStyle(
            fontSize = typography().l.fontSize,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontFamily = typography().l.fontFamily,
            color = AppBar.contentColor(),
            textDirection = androidx.compose.ui.text.style.TextDirection.Ltr
        ),
        modifier = Modifier.clickable {
            if ( NavRoutes.home.isHere( navController ) ) return@clickable

            navController.navigate( NavRoutes.home.name ) {
                popUpTo( navController.graph.findStartDestination().id ) {
                    inclusive = true
                }

                launchSingleTop = true
            }
        }
    )
}

// START
@Composable
fun AppTitle(
    navController: NavController,
    context: Context
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy( 5.dp ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AppLogo( navController, context )
        AppLogoText( navController )

        if(Preference.parentalControl())
            Button(
                iconId = R.drawable.shield_checkmark,
                color = AppBar.contentColor(),
                padding = 0.dp,
                size = 20.dp
            ).Draw()

        if (Preference.debugLog())
            BasicText(
                text = stringResource(R.string.info_debug_mode_enabled),
                style = TextStyle(
                    fontSize = typography().xxs.semiBold.fontSize,
                    fontWeight = typography().xxs.semiBold.fontWeight,
                    fontFamily = typography().xxs.semiBold.fontFamily,
                    color = colorPalette().red
                ),
                modifier = Modifier
                    .clickable {
                        Toaster.s( R.string.info_debug_mode_is_enabled )

                        NavRoutes.settings.navigateHere( navController )
                    }
            )
    }
// END
}