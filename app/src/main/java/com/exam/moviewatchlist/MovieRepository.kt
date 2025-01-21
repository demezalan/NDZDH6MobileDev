package com.exam.moviewatchlist

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MovieRepository(context: Context) {

    private val sharedPreferences = context.getSharedPreferences("movies_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val moviesKey = "movies_list"

    // Adatok betöltése shared preferences-ből
    fun loadMovies(): MutableList<Movie> {
        val json = sharedPreferences.getString(moviesKey, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Movie>>() {}.type
        return gson.fromJson(json, type)
    }

    // Adatok mentése shared preferences-be
    fun saveMovies(movies: List<Movie>) {
        val json = gson.toJson(movies)
        sharedPreferences.edit().putString(moviesKey, json).apply()
    }
}
