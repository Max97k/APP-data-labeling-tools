package com.google.mlkit.codelab.objectdetection

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.StringWriter

class CsvExporterTest {

    @Test
    fun testExport() {
        val exporter = CsvExporter()
        val data: MutableList<Array<String>> = mutableListOf(
            arrayOf("box1", "label1", "label2", "label3"),
            arrayOf("box2", "label4", "label5", "label6")
        )

        val writer = StringWriter()
        exporter.export(data, writer)

        val expected = "[[\"box1\",\"label1\",\"label2\",\"label3\"],[\"box2\",\"label4\",\"label5\",\"label6\"]]"
        assertEquals(expected, writer.toString())
    }

    @Test
    fun testExportToString() {
        val exporter = CsvExporter()
        val data: MutableList<Array<String>> = mutableListOf(
            arrayOf("box1", "label1", "label2", "label3"),
            arrayOf("box2", "label4", "label5", "label6")
        )

        val expected = "[[\"box1\",\"label1\",\"label2\",\"label3\"],[\"box2\",\"label4\",\"label5\",\"label6\"]]"
        assertEquals(expected, exporter.exportToString(data))
    }
}
