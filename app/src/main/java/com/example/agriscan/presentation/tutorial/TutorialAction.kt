package com.example.agriscan.presentation.tutorial

sealed class TutorialAction {
    object PlayVideo : TutorialAction()
    object PauseVideo : TutorialAction()
    data class SetVideo(val videoId: String) : TutorialAction()
    object LanguageChangeTapped : TutorialAction()
}
