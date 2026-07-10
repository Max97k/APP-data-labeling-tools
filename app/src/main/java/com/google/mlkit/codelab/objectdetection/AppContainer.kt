package com.google.mlkit.codelab.objectdetection

import android.content.Context

interface AppContainer {
    val imageCaptureManager: ImageCaptureManager
    val objectDetectorHelper: ObjectDetectorHelper
    val csvExporter: CsvExporter
    val firebaseRepository: FirebaseRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val imageCaptureManager: ImageCaptureManager by lazy {
        ImageCaptureManager(context)
    }
    override val objectDetectorHelper: ObjectDetectorHelper by lazy {
        ObjectDetectorHelper(context)
    }
    override val csvExporter: CsvExporter by lazy {
        CsvExporter(context)
    }
    override val firebaseRepository: FirebaseRepository by lazy {
        FirebaseRepository(context)
    }
}