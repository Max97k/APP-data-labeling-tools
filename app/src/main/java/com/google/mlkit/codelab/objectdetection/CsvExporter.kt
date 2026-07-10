package com.google.mlkit.codelab.objectdetection

import com.google.gson.GsonBuilder
import java.io.Writer

class CsvExporter {
    fun export(data: MutableList<Array<String>>, writer: Writer) {
        val gson = GsonBuilder().create()
        gson.toJson(data, writer)
    }

    fun exportToString(data: MutableList<Array<String>>): String {
        val gson = GsonBuilder().create()
        return gson.toJson(data)
    }
}
