package com.example.popularmovies

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.popularmovies.model.MovieTrailer
import com.squareup.picasso.Picasso

/**
 * Recycler View trailers adapter
 * Created by carlos on 6/6/17.
 */
class TrailersAdapter(private val context: Context,
                      var trailers:ArrayList<MovieTrailer> = ArrayList(),
                      private val listener: (MovieTrailer) -> Unit) :
        RecyclerView.Adapter<TrailersAdapter.TrailerHolder>() {

    override fun getItemCount(): Int = trailers.size

    override fun onBindViewHolder(holder: TrailerHolder, position: Int) =
        holder.bind(trailers[position], listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailerHolder =
        TrailerHolder(LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false))

    inner class TrailerHolder(private val trailerView: View): RecyclerView.ViewHolder(trailerView) {
        fun bind(trailer: MovieTrailer, listener: (MovieTrailer) -> Unit) = with(trailerView) {
            Picasso.get().load(trailer.image).into(trailerView.findViewById(R.id.image_trailer) as ImageView)
            setOnClickListener { listener(trailer) }
        }
    }
}


