package com.ignacnic.architectcoders.common.filemanager.usecases

import android.content.ContentResolver
import android.net.Uri
import java.io.FileInputStream
import java.io.FileNotFoundException

class ReadFromFileUseCase(
    private val contentResolver: ContentResolver,
) {
    operator fun invoke(filePath: Uri): Result<ByteArray> {
        try {
            contentResolver.openFileDescriptor(filePath, "r").use {
                FileInputStream(it?.fileDescriptor).use { inputStream ->
                    return Result.success(inputStream.readBytes())

                }
            }
        } catch (e: FileNotFoundException) {
            return Result.failure(e)
        }
    }
}
