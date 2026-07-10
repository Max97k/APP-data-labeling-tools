package com.google.mlkit.codelab.objectdetection

import android.content.Context
import android.os.Environment
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.concurrent.thread

object CsvExporter {

    fun saveDataToCSV(context: Context, imageName: String, data: List<Array<String>>) {
        thread {
            try {
                val file = createCsvFile(context, imageName)
                FileWriter(file).use { writer ->
                    val gson = GsonBuilder().create()
                    gson.toJson(data, writer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    fun createCsvFile(context: Context, imageName: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File.createTempFile(
            imageName, /* prefix */
            ".csv", /* suffix */
            storageDir /* directory */
        )
    }
}
