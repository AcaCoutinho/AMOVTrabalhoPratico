package pt.isec.amovtp.touristapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(red = 27, green = 66, blue = 66),
    onPrimary = Color.LightGray,
    secondary =Color.White,
    onSecondary = Color.LightGray,
    tertiary = Color.White,             //texto
    onTertiary = Color.LightGray,       //divider
    onBackground = Color.DarkGray,      //fundo

)

private val LightColorScheme = lightColorScheme(

    primary = Color(0xFFF7F2F9),
    onPrimary = Color.DarkGray,
    secondary = Color(0xFF644F9F),
    onSecondary = Color.DarkGray,
    tertiary = Color.Black,             //texto
    onTertiary = Color.LightGray,       //divider
    onBackground = Color.LightGray,     //fundo


    /*
    primary = Color(0xFF87CEEB),
    onPrimary = Color.DarkGray,
    secondary = Color(0xFFFFFFFF),
    onSecondary = Color.DarkGray,
    tertiary = Color(0xFFD3D3D3),
    onTertiary = Color.DarkGray

     */

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun TouristAPPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}