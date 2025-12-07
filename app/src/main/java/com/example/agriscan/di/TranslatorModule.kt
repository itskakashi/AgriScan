package com.example.agriscan.di

import com.example.agriscan.translator.MLKitTranslator
import com.example.agriscan.translator.Translator
import org.koin.dsl.module

val translatorModule = module {
    single<Translator> { MLKitTranslator() }
}
