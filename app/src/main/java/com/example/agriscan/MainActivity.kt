package com.example.agriscan

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.agriscan.presentation.navigation.RootNavGraph
import com.example.agriscan.ui.theme.AgriScanTheme
import com.example.agriscan.util.LanguageManager
import com.example.agriscan.util.LocaleHelper
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.android.ext.android.inject

class MainActivity : BaseActivity() {

    private val languageManager: LanguageManager by inject()

    override fun attachBaseContext(newBase: Context) {
        val lm = LanguageManager(newBase)
        val language = lm.getLanguage()
        val context = LocaleHelper.setLocale(newBase, language)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        languageManager.languageChanged
            .onEach { recreate() }
            .launchIn(lifecycleScope)

        setContent {
            AgriScanTheme(darkTheme = false, dynamicColor = false) {
                RootNavGraph()
            }
        }
    }
}
