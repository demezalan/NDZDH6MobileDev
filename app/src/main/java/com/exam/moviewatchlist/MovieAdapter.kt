package com.exam.moviewatchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(
    private val movies: MutableList<Movie>,
    private val listener: OnMovieClickListener
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    interface OnMovieClickListener {
        fun onEditClick(position: Int)
        fun onWatchedToggle(position: Int)
        fun onDeleteClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        holder.lengthTextView.text = "${movie.length} perc"
        holder.genreTextView.text = movie.genre

        // Glide segítségével kép/alapértelmezett kép betöltése
        Glide.with(holder.itemView.context)
            .load(if (movie.coverImage.isNotEmpty()) movie.coverImage else R.drawable.placeholder_image)
            .placeholder(R.drawable.placeholder_image)
            .into(holder.coverImageView)

        holder.watchedCheckBox.isChecked = movie.watched
        holder.watchedCheckBox.setOnClickListener {
            listener.onWatchedToggle(position)
        }
        holder.editButton.setOnClickListener {
            listener.onEditClick(position)
        }
        holder.deleteButton.setOnClickListener {
            listener.onDeleteClick(position)
        }
    }


    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImageView: ImageView = itemView.findViewById(R.id.image_view_cover)
        val titleTextView: TextView = itemView.findViewById(R.id.text_view_title)
        val genreTextView: TextView = itemView.findViewById(R.id.text_view_genre)
        val lengthTextView: TextView = itemView.findViewById(R.id.text_view_length)
        val watchedCheckBox: CheckBox = itemView.findViewById(R.id.checkbox_watched)
        val editButton: Button = itemView.findViewById(R.id.button_edit)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
    }
}
