package com.example.movierecommendingapp

import java.io.Serializable
import java.util.ArrayList

class Movie (val title: String, val body: String, val name: String, val uid: String, val questionUid: String, val genre: Int, bytes: ByteArray, val comments: ArrayList<Comment>, val total: Int) : Serializable {
    val imageBytes: ByteArray

    init {
        imageBytes = bytes.clone()
    }
}