package com.google.mlkit.codelab.objectdetection

import android.content.Context
import android.os.Environment
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CsvExporter(private val context: Context) {

    @Throws(IOException::class)
    private fun createCsvFile(imageName: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        return File.createTempFile(
            imageName, /* prefix */
            ".csv", /* suffix */
            storageDir /* directory */
        )
    }

    fun exportToCsv(
        imageName: String,
        dataBoxArray: Array<String>,
        listDataArray: Array<String>
    ) {
        val data: MutableList<Array<String>> = mutableListOf()
        for (i in 0..2) {
            data.add(
                arrayOf(
                    dataBoxArray[i],
                    listDataArray[i * 3],
                    listDataArray[i * 3 + 1],
                    listDataArray[i * 3 + 2]
                )
            )
        }

        FileWriter(createCsvFile(imageName)).use { writer ->
            val gson = GsonBuilder().create()
            gson.toJson(data, writer)
        }
    }
}
