package com.google.mlkit.codelab.objectdetection

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.database.DatabaseReference
import com.google.mlkit.codelab.objectdetection.databinding.ActivityLabelBinding
import com.google.mlkit.vision.objects.DetectedObject


class LabelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLabelBinding
    private lateinit var currentPhotoPath: String
    lateinit var listDataArray: Array<String>
    private lateinit var dataBoxArray: Array<String>
    private lateinit var imageName: String

    private lateinit var objectDetectorHelper: ObjectDetectorHelper
    private lateinit var csvExporter: CsvExporter
    private lateinit var firebaseRepository: FirebaseRepository
    private lateinit var imageCaptureManager: ImageCaptureManager

    val boxMap = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLabelBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        objectDetectorHelper = (application as MyApplication).appContainer.objectDetectorHelper
        csvExporter = (application as MyApplication).appContainer.csvExporter
        firebaseRepository = (application as MyApplication).appContainer.firebaseRepository
        imageCaptureManager = (application as MyApplication).appContainer.imageCaptureManager

        binding.checkButton.show()
        binding.checkButton.setOnClickListener {
            binding.checkButton.hide()
            Log.e("finish", "finish")
            saveDataToCSV()
            up_load()
            Log.e("finish", "finish")
        }

        currentPhotoPath = intent.getStringExtra("image")!!
        imageName = intent.getStringExtra("imageName")!!
        val ot = intent.getIntExtra("ot", 0)

        val bitmap = imageCaptureManager.getProcessedBitmap(currentPhotoPath, ot)

        runObjectDetection(bitmap, binding.image1)
    }

    /**
     * ML Kit Object Detection function. We'll add ML Kit code here in the codelab.
     */
    private fun runObjectDetection(bitmap: Bitmap, imageRun: ImageView) {
        objectDetectorHelper.processImage(
            bitmap = bitmap,
            onSuccess = { results ->
                debugPrint(results, bitmap)

                // Parse ML Kit's DetectedObject and create corresponding visualization data
                val detectedObjects = objectDetectorHelper.mapToBoxWithText(results)

                // Draw the detection result on the input bitmap
                val visualizedResult = objectDetectorHelper.drawDetectionResult(bitmap, detectedObjects)

                // Show the detection result on the app screen
                // imageRun.setImageBitmap(visualizedResult)
            },
            onFailure = {
                Log.e(MainActivity.TAG, it.message.toString())
            }
        )
    }

    /**
     * Print out the object detection result to Logcat.
     */
    private fun debugPrint(detectedObjects: List<DetectedObject>, bitmap: Bitmap) {
        var use_f = 0
        dataBoxArray = Array(3) { "" }
        detectedObjects.forEachIndexed { index, detectedObject ->
            val box = detectedObject.boundingBox
            var imageS = binding.image1
            when (use_f) {
                0 -> imageS = binding.image1
                1 -> imageS = binding.image2
                2 -> imageS = binding.image3
            }
            imageS.setImageBitmap(Bitmap.createBitmap(bitmap,
                box.left,
                box.top,
                box.right - box.left,
                box.bottom - box.top))
            if (use_f < 3) {
                dataBoxArray[use_f] = box.toString()
                boxMap.put("left"+ use_f.toString(), box.left)
                boxMap.put("top"+ use_f.toString(), box.top)
                boxMap.put("right"+ use_f.toString(), box.right)
                boxMap.put("bottom"+ use_f.toString(), box.bottom)
            }
            use_f += 1

//                Log.e("FUCK", my_box.toString())
//            binding.image1.setImageBitmap(Bitmap.createBitmap(bitmap,my_box.left,my_box.top,my_box.right-my_box.left,my_box.bottom-my_box.top))
//                Log.d(MainActivity.TAG, "Detected object: $index")
//                Log.d(MainActivity.TAG, " trackingId: ${detectedObject.trackingId}")
//                Log.d(MainActivity.TAG, " boundingBox: (${box.left}, ${box.top}) - (${box.right},${box.bottom})")
//                detectedObject.labels.forEach {
//                    Log.d(MainActivity.TAG, " categories: ${it.text}")
//                    Log.d(MainActivity.TAG, " confidence: ${it.confidence}")


        }
    }

    private fun saveDataToCSV() {
        listDataArray = Array(3 * 3) { "" }
        listDataArray[0] = binding.label11.editableText.toString()
        listDataArray[1] = binding.label12.editableText.toString()
        listDataArray[2] = binding.label13.editableText.toString()
        listDataArray[3] = binding.label21.editableText.toString()
        listDataArray[4] = binding.label22.editableText.toString()
        listDataArray[5] = binding.label23.editableText.toString()
        listDataArray[6] = binding.label31.editableText.toString()
        listDataArray[7] = binding.label32.editableText.toString()
        listDataArray[8] = binding.label33.editableText.toString()

        csvExporter.exportToCsv(imageName, dataBoxArray, listDataArray)
    }

    private fun up_load() {
        listDataArray = Array(3 * 3) { "" }
        listDataArray[0] = binding.label11.editableText.toString()
        listDataArray[1] = binding.label12.editableText.toString()
        listDataArray[2] = binding.label13.editableText.toString()
        listDataArray[3] = binding.label21.editableText.toString()
        listDataArray[4] = binding.label22.editableText.toString()
        listDataArray[5] = binding.label23.editableText.toString()
        listDataArray[6] = binding.label31.editableText.toString()
        listDataArray[7] = binding.label32.editableText.toString()
        listDataArray[8] = binding.label33.editableText.toString()

        firebaseRepository.savePost(
            imageName = imageName,
            boxMap = boxMap,
            listDataArray = listDataArray,
            onSuccess = {
                Toast.makeText(this, "Upload successful!", Toast.LENGTH_LONG).show()
                val intent = Intent(this, MainActivity::class.java).apply {}
                startActivity(intent)
            },
            onFailure = {
                Toast.makeText(this, "Upload failed", Toast.LENGTH_LONG).show()
            }
        )
    }

}





