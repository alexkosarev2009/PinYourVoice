package com.example.shareyourvoicemapbox.domain.report

import com.example.shareyourvoicemapbox.data.dto.ReportMarkerDTO
import com.example.shareyourvoicemapbox.data.source.report.ReportDataSource
import javax.inject.Inject

class ReportMarkerUseCase @Inject constructor(
    private val reportDataSource: ReportDataSource
) {
    suspend operator fun invoke(dto: ReportMarkerDTO): Result<Boolean> {
        return reportDataSource.reportMarker(dto)
    }
}