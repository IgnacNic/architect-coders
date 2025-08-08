package com.ignacnic.architectcoders.common.filemanager.usecases

import android.content.ContentResolver
import android.net.Uri
import java.io.FileNotFoundException
import java.io.FileOutputStream

class WriteToFileUseCase(
    private val contentResolver: ContentResolver,
) {
    operator fun invoke(filePath: Uri, content: ByteArray): Result<Unit> {
        try {
            contentResolver.openFileDescriptor(filePath, "w").use {
                FileOutputStream(it?.fileDescriptor).use { outputStream ->
                    outputStream.write(
                        content
                    )
                }
            }
            return Result.success(Unit)
        } catch (e: FileNotFoundException) {
            return Result.failure(e)
        }
    }
}
