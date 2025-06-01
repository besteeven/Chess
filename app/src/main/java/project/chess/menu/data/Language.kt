package project.chess.menu.data

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import project.chess.R
import java.util.Locale

data class Language(
    val locale: Locale,
    @StringRes val nameRes: Int,
    @DrawableRes val flagRes: Int
)

object SupportedLanguages {
    val list = listOf(
        Language(Locale.FRENCH, R.string.french, R.drawable.flag_fr),
        Language(Locale.ENGLISH, R.string.english, R.drawable.flag_en)
    )
}

public fun applyLocale(context: Context, locale: Locale): Context {
    Locale.setDefault(locale)
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)

    return context.createConfigurationContext(config)
}

object LanguageManager {
    private const val PREFS_NAME = "app_prefs"
    private const val LANG_KEY = "selected_language"

    var currentIndex by mutableStateOf(0)
        private set

    val currentLanguage: Language
        get() = SupportedLanguages.list[currentIndex]

    fun cycleLanguage(context: Context) {
        currentIndex = (currentIndex + 1) % SupportedLanguages.list.size

        // Sauvegarde
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(LANG_KEY, currentIndex)
            .apply()
    }

    fun loadLanguage(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        currentIndex = prefs.getInt(LANG_KEY, 0)
    }

    fun applyLocale(context: Context): Context {
        val config = Configuration(context.resources.configuration)
        config.setLocale(currentLanguage.locale)
        return context.createConfigurationContext(config)
    }
}


