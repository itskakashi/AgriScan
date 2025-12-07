package com.example.agriscan.translator

interface Translator {
    suspend fun translate(text: String, targetLanguage: String): String
}
