package com.example.agriscan.presentation.scan

sealed interface ScanAction {
    object OnCaptureClick : ScanAction
    object OnGalleryClick : ScanAction
    object OnConfirmClick : ScanAction
    object OnCancelClick : ScanAction
}
