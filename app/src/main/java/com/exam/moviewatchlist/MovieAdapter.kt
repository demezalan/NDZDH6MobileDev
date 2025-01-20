package com.exam.moviewatchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    private val movies: MutableList<Movie>,
    private val listener: OnMovieClickListener
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    interface OnMovieClickListener {
        fun onEditClick(position: Int)
        fun onWatchedToggle(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movies[position]
        holder.titleTextView.text = movie.title
        holder.watchedCheckBox.isChecked = movie.watched

        holder.watchedCheckBox.setOnCheckedChangeListener { _, _ ->
            listener.onWatchedToggle(position)
        }

        holder.editButton.setOnClickListener {
            listener.onEditClick(position)
        }
    }

    override fun getItemCount(): Int = movies.size

    class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.text_view_title)
        val watchedCheckBox: CheckBox = itemView.findViewById(R.id.checkbox_watched)
        val editButton: Button = itemView.findViewById(R.id.button_edit)
    }
}
