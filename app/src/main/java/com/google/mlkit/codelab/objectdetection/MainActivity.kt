/**
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.mlkit.codelab.objectdetection

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "MLKit-ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
        private const val MAX_FONT_SIZE = 96F
    }

    private lateinit var captureImageFab: Button
    private lateinit var toLabelFab: Button

    private lateinit var storage: FirebaseStorage
    private lateinit var imageCaptureManager: ImageCaptureManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        captureImageFab = findViewById(R.id.captureImageFab)
        toLabelFab = findViewById(R.id.toLabelFab)
        storage = Firebase.storage
        imageCaptureManager = ImageCaptureManager(this, REQUEST_IMAGE_CAPTURE)
        captureImageFab.setOnClickListener(this)
        toLabelFab.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE &&
            resultCode == RESULT_OK
        ) {
            setViewAndDetect(imageCaptureManager.getCapturedImage())
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.captureImageFab -> {
                try {
                    imageCaptureManager.dispatchTakePictureIntent()
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message.toString())
                }
            }
            R.id.toLabelFab -> {
                imgUploadFiles()
//                val stream = ByteArrayOutputStream()
//                imageResult.compress(Bitmap.CompressFormat.JPEG, 90, stream)
//                val byteArray: ByteArray = stream.toByteArray()
//                val intent = Intent(this, LabelActivity::class.java)
//
//                intent.putExtra("image", currentPhotoPath)
//                intent.putExtra("imageName", imageName )
//                intent.putExtra("ot", ot)
//                startActivity(intent)
            }

        }
    }

    /**
     * Set image to view and call object detection
     */
    private fun setViewAndDetect(bitmap: Bitmap) {
        // Display the captured image
//        inputImageView.setImageBitmap(bitmap)
//        tvPlaceholder.visibility = View.INVISIBLE

        // Run object detection and display the result
//        runObjectDetection(bitmap)
    }

    /**
     * Get image form drawable and convert to bitmap.
     */
    private fun getSampleImage(drawable: Int): Bitmap {
        return BitmapFactory.decodeResource(resources, drawable, BitmapFactory.Options().apply {
            inMutable = true
        })
    }

    private fun imgUploadFiles(){
        val storage = Firebase.storage
        val storageRef = storage.reference
//        val zipFile: File = File(currentPhotoPath)
        var file = Uri.fromFile(File(imageCaptureManager.currentPhotoPath))
//        val imagesRef = storageRef.child("images/${file.lastPathSegment}")
        val imagesRef = storageRef.child(imageCaptureManager.imageName+".jpg")

        var uploadTask = imagesRef.putFile(file)
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
            Toast.makeText(getApplicationContext(), "保存失敗Q", Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            Toast.makeText(getApplicationContext(), imageCaptureManager.imageName + ".jpg上傳成功", Toast.LENGTH_LONG).show();
            val intent = Intent(this, LabelActivity::class.java)
            intent.putExtra("image", imageCaptureManager.currentPhotoPath)
            intent.putExtra("imageName", imageCaptureManager.imageName )
            intent.putExtra("ot", imageCaptureManager.ot)
            startActivity(intent)
            // ...
        }
    }
}

/**
 * A general-purpose data class to store detection result for visualization
 */
data class BoxWithText(val box: Rect, val text: String)
