package com.example.agriscan.domain.usecase

import com.example.agriscan.data.local.entities.Scan
import com.example.agriscan.domain.repository.ScanRepository

class SaveScansUseCase(private val scanRepository: ScanRepository) {
    suspend operator fun invoke(scans: List<Scan>) {
        scans.forEach { scan ->
            scanRepository.insertScan(scan)
        }
    }
}
