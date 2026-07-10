package com.google.mlkit.codelab.objectdetection

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import models.PostFeature
import java.io.File

class FirebaseRepository(private val context: Context) {
    private val storage: FirebaseStorage = Firebase.storage
    private val database: DatabaseReference = Firebase.database.reference

    fun uploadImage(
        currentPhotoPath: String,
        imageName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference
        val file = Uri.fromFile(File(currentPhotoPath))
        val imagesRef = storageRef.child("$imageName.jpg")

        val uploadTask = imagesRef.putFile(file)
        uploadTask.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun savePost(
        imageName: String,
        boxMap: Map<String, Int>,
        listDataArray: Array<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val boxNumber = boxMap.size / 4
        var successCount = 0
        var failureOccurred = false

        if (boxNumber == 0) {
            onSuccess()
            return
        }

        for (i in 0 until boxNumber) {
            val left = boxMap["left$i"].toString()
            val top = boxMap["top$i"].toString()
            val right = boxMap["right$i"].toString()
            val bottom = boxMap["bottom$i"].toString()

            val key = database.child("posts").push().key
            if (key == null) {
                if (!failureOccurred) {
                    failureOccurred = true
                    onFailure(Exception("Couldn't get push key for posts"))
                }
                continue
            }

            val post = PostFeature(
                "$imageName.jpg",
                left, top, right, bottom,
                listDataArray[i * 3],
                listDataArray[i * 3 + 1],
                listDataArray[i * 3 + 2]
            )
            val postValues = post.toMap()
            val childUpdates = hashMapOf<String, Any>("/posts/$key" to postValues)

            database.updateChildren(childUpdates)
                .addOnSuccessListener {
                    successCount++
                    if (successCount == boxNumber && !failureOccurred) {
                        onSuccess()
                    }
                }
                .addOnFailureListener { exception ->
                    if (!failureOccurred) {
                        failureOccurred = true
                        onFailure(exception)
                    }
                }
        }
    }
}
