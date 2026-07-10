package com.google.mlkit.codelab.objectdetection

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import models.PostFeature
import java.lang.Exception

class UploadRepository {
    private val storage: FirebaseStorage = Firebase.storage
    private val database: DatabaseReference = Firebase.database.reference

    fun uploadImage(
        fileUri: Uri,
        imageName: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("$imageName.jpg")
        val uploadTask = imagesRef.putFile(fileUri)

        uploadTask.addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun writeNewPost(
        post: PostFeature,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val key = database.child("posts").push().key
        if (key == null) {
            onFailure(Exception("Couldn't get push key for posts"))
            return
        }

        val postValues = post.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/posts/$key" to postValues
        )

        database.updateChildren(childUpdates)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}
