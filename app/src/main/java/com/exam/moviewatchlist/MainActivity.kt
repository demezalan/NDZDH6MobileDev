package com.exam.moviewatchlist

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exam.moviewatchlist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val movies = mutableListOf<Movie>()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_movies)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MovieAdapter(movies, object : MovieAdapter.OnMovieClickListener {
            override fun onEditClick(position: Int) {
                showEditMovieDialog(position)
            }

            override fun onWatchedToggle(position: Int) {
                val movie = movies[position]
                movie.watched = !movie.watched
                adapter.notifyItemChanged(position)
            }
        })
        recyclerView.adapter = adapter

        val addButton = findViewById<Button>(R.id.button_add_movie)
        addButton.setOnClickListener {
            showAddMovieDialog()
        }
    }

    private fun showAddMovieDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Movie")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ ->
            val title = input.text.toString()
            movies.add(Movie(title, false))
            adapter.notifyItemInserted(movies.size - 1)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun showEditMovieDialog(position: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Movie")

        val input = EditText(this)
        input.setText(movies[position].title)
        builder.setView(input)

        builder.setPositiveButton("Save") { _, _ ->
            val title = input.text.toString()
            movies[position].title = title
            adapter.notifyItemChanged(position)
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
