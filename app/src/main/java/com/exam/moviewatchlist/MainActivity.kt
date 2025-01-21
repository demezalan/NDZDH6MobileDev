package com.exam.moviewatchlist

import android.app.Activity
import android.content.Intent
import android.net.Uri
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
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback.makeMovementFlags
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.exam.moviewatchlist.databinding.ActivityMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() {

    private val movies = mutableListOf<Movie>()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerMovies)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MovieAdapter(movies, object : MovieAdapter.OnMovieClickListener {
            // Film szerkesztése
            override fun onEditClick(position: Int) {
                showEditMovieDialog(position)
            }

            // Film törlése
            override fun onDeleteClick(position: Int) {
                deleteMovie(position)
            }

            // Film megnézve checkbox
            override fun onWatchedToggle(position: Int) {
                val movie = movies[position]
                movie.watched = !movie.watched
                adapter.notifyItemChanged(position)
            }


        })
        recyclerView.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // drag&drop mozgatás
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                return makeMovementFlags(dragFlags, 0)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // elemek áthelyezése
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                Collections.swap(movies, fromPosition, toPosition)
                adapter.notifyItemMoved(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

            override fun isLongPressDragEnabled(): Boolean {
                // hosszan nyomásra drag&drop engedélyezése
                return true
            }
        })


        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showAddMovieDialog()
        }

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun openImagePicker(callback: (Uri?) -> Unit) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*" // csak képek jöhetnek
        }

        imagePickerLauncher.launch(intent)

        imagePickerCallback = callback
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageUri: Uri? = data?.data

            if (imageUri != null) {
                imagePickerCallback?.invoke(imageUri)
            } else {
                imagePickerCallback?.invoke(null)
            }
        } else {
            imagePickerCallback?.invoke(null)
        }
    }

    private var imagePickerCallback: ((Uri?) -> Unit)? = null

    private lateinit var chosenImageUri: Uri

    private fun showAddMovieDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_movie, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.edit_text_title)
        val lengthInput = dialogView.findViewById<EditText>(R.id.edit_text_length)
        val genreInput = dialogView.findViewById<EditText>(R.id.edit_text_genre)
        val chooseImageButton = dialogView.findViewById<Button>(R.id.button_choose_image)

        var chosenImageUri: Uri? = null

        chooseImageButton.setOnClickListener {
            openImagePicker { uri ->
                chosenImageUri = uri
                if (uri != null) {
                    Toast.makeText(this, "Kép kiválasztva!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Nem lett kép kiválasztva", Toast.LENGTH_SHORT).show()
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Film hozzáadása")
            .setView(dialogView)
            .setPositiveButton("Hozzáad") { _, _ ->
                val title = titleInput.text.toString()
                val length = lengthInput.text.toString().toIntOrNull() ?: 0
                val genre = genreInput.text.toString()
                val coverImage = chosenImageUri?.toString() ?: ""

                movies.add(Movie(title, length, genre, coverImage, false))
                adapter.notifyItemInserted(movies.size - 1)
            }
            .setNegativeButton("Mégsem", null)
            .show()
    }



    private fun showEditMovieDialog(position: Int) {
        val movie = movies[position]
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_movie, null)
        val titleInput = dialogView.findViewById<EditText>(R.id.edit_text_title)
        val lengthInput = dialogView.findViewById<EditText>(R.id.edit_text_length)
        val genreInput = dialogView.findViewById<EditText>(R.id.edit_text_genre)
        val chooseImageButton = dialogView.findViewById<Button>(R.id.button_choose_image)

        var updatedCoverImageUri: Uri? = null

        titleInput.setText(movie.title)
        lengthInput.setText(movie.length.toString())
        genreInput.setText(movie.genre)

        chooseImageButton.setOnClickListener {
            openImagePicker { uri ->
                updatedCoverImageUri = uri
                if (uri != null) {
                    Toast.makeText(this, "Kép kiválasztva!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Nem lett kép kiválasztva", Toast.LENGTH_SHORT).show()
                }
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Film szerkesztése")
            .setView(dialogView)
            .setPositiveButton("Mentés") { _, _ ->
                movie.title = titleInput.text.toString()
                movie.length = lengthInput.text.toString().toIntOrNull() ?: 0
                movie.genre = genreInput.text.toString()
                movie.coverImage = updatedCoverImageUri?.toString() ?: movie.coverImage
                adapter.notifyItemChanged(position)
            }
            .setNegativeButton("Mégsem", null)
            .show()
    }

    private fun deleteMovie(position: Int) {
        movies.removeAt(position)
        adapter.notifyItemRemoved(position)
    }

}
