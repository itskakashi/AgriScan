package com.example.agriscan.util

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

class LanguageManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val _language = MutableStateFlow(getLanguage())
    val language = _language.asStateFlow()

    private val _languageChanged = Channel<Unit>(Channel.BUFFERED)
    val languageChanged = _languageChanged.receiveAsFlow()

    companion object {
        private const val LANGUAGE_KEY = "language"
    }

    fun setLanguage(language: String) {
        val currentLanguage = getLanguage()
        if (language == currentLanguage) return

        prefs.edit().putString(LANGUAGE_KEY, language).commit()
        _language.value = language
        _languageChanged.trySend(Unit)
    }

    fun getLanguage(): String {
        return prefs.getString(LANGUAGE_KEY, "en") ?: "en"
    }
}
