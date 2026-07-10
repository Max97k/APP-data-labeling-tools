package com.google.mlkit.codelab.objectdetection

import android.util.Log
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import models.PostFeature

class PostRepository {
    private val database: DatabaseReference = Firebase.database.reference

    fun writeNewPost(
        uid: String,
        left: String,
        top: String,
        right: String,
        bottom: String,
        label1: String,
        label2: String,
        label3: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val key = database.child("posts").push().key
        if (key == null) {
            Log.w(MainActivity.TAG, "Couldn't get push key for posts")
            onFailure(Exception("Couldn't get push key for posts"))
            return
        }
        val post = PostFeature(uid, left, top, right, bottom, label1, label2, label3)
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
