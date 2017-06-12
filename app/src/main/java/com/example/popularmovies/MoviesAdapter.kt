package com.example.popularmovies

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.popularmovies.model.Movie
import com.squareup.picasso.Picasso

/**
 * Recycler View movies adapter
 * Created by carlos on 5/23/17.
 */
class MoviesAdapter(private val context: Context, var movies:ArrayList<Movie> = ArrayList<Movie>(), val listener: (Movie) -> Unit) : RecyclerView.Adapter<MoviesAdapter.MovieHolder>() {

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieHolder?, position: Int) =
            (holder as MovieHolder).bind(movies[position], listener)

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MovieHolder =
            MovieHolder(LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false))

    inner class MovieHolder(private val movieView: View): RecyclerView.ViewHolder(movieView) {
        fun bind(movie: Movie, listener: (Movie) -> Unit) = with(movieView) {
            Picasso.with(context).load(movie.posterPath).into(movieView.findViewById(R.id.item_poster) as ImageView)
            setOnClickListener { listener(movie) }
        }
    }
}