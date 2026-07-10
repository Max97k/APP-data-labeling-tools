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
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val TAG = "MLKit-ODT"
        const val REQUEST_IMAGE_CAPTURE: Int = 1
    }

    private lateinit var captureImageFab: Button
    private lateinit var toLabelFab: Button

    private lateinit var imageCaptureManager: ImageCaptureManager
    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageCaptureManager = (application as MyApplication).appContainer.imageCaptureManager
        firebaseRepository = (application as MyApplication).appContainer.firebaseRepository

        captureImageFab = findViewById(R.id.captureImageFab)
        toLabelFab = findViewById(R.id.toLabelFab)

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
                    dispatchTakePictureIntent()
                } catch (e: ActivityNotFoundException) {
                    Log.e(TAG, e.message.toString())
                }
            }
            R.id.toLabelFab -> {
                imgUploadFiles()
            }
        }
    }

    private fun setViewAndDetect(bitmap: Bitmap?) {
        // Display the captured image or run detection
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    imageCaptureManager.createImageFile()
                } catch (e: IOException) {
                    Log.e(TAG, e.message.toString())
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.google.mlkit.codelab.objectdetection.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun imgUploadFiles(){
        val currentPhotoPath = imageCaptureManager.currentPhotoPath
        val imageName = imageCaptureManager.imageName
        val ot = imageCaptureManager.ot

        if (currentPhotoPath == null || imageName == null) return

        firebaseRepository.uploadImage(
            currentPhotoPath = currentPhotoPath,
            imageName = imageName,
            onSuccess = {
                Toast.makeText(applicationContext, imageName + ".jpg上傳成功", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LabelActivity::class.java)
                intent.putExtra("image", currentPhotoPath)
                intent.putExtra("imageName", imageName)
                intent.putExtra("ot", ot)
                startActivity(intent)
            },
            onFailure = {
                Toast.makeText(applicationContext, "保存失敗Q", Toast.LENGTH_LONG).show()
            }
        )
    }
}

/**
 * A general-purpose data class to store detection result for visualization
 */
data class BoxWithText(val box: Rect, val text: String)
