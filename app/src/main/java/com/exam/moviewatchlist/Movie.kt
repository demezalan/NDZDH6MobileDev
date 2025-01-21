package com.exam.moviewatchlist

data class Movie(
    var title: String,
    var length: Int,
    var genre: String,
    var coverImage: String = "placeholder_image", // Alapértelmezett borítókép, ha nem választ ki a felhasználó
    var watched: Boolean
)

