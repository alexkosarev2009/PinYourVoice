package com.example.shareyourvoicemapbox.domain

import com.example.shareyourvoicemapbox.data.source.presign.PresignDataSource
import javax.inject.Inject

class UploadFileUseCase @Inject constructor(
    private val presignDataSource: PresignDataSource
) {
    suspend operator fun invoke(
        fileName: String,
        contentType: String,
        filePath: String
    ): Result<String> {
        return presignDataSource.uploadFile(
            fileName = fileName,
            contentType = contentType,
            filePath = filePath
        )
    }
}