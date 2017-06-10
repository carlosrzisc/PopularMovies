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
class MoviesAdapter(private var context: Context, var movies:ArrayList<Movie> = ArrayList<Movie>(), var listener:OnItemClickListener) : RecyclerView.Adapter<MoviesAdapter.MovieHolder>() {

    override fun getItemCount(): Int {
        return movies.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MovieHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false)
        return MovieHolder(v)
    }

    override fun onBindViewHolder(holder: MovieHolder?, position: Int) {
        (holder as MovieHolder).bindData(movies.get(position))
    }

    inner class MovieHolder(private val movieView: View): RecyclerView.ViewHolder(movieView), View.OnClickListener {
        init {
            movieView.setOnClickListener(this)
        }

        override fun onClick(item: View?) {
            listener.onClick(movies[adapterPosition])
        }

        fun bindData(movie: Movie) {
            Picasso.with(context).load(movie.posterPath).into(movieView.findViewById(R.id.item_poster) as ImageView)
        }
    }

    interface OnItemClickListener {
        fun onClick(movie: Movie)
    }
}


