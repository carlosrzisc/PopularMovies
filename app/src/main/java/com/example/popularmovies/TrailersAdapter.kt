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
class TrailersAdapter(private val context: Context, var trailers:ArrayList<MovieTrailer> = ArrayList(), val listener:OnTrailerClickListener) : RecyclerView.Adapter<TrailersAdapter.TrailerHolder>() {

    override fun getItemCount(): Int {
        return trailers.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TrailerHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_trailer, parent, false)
        return TrailerHolder(v)
    }

    override fun onBindViewHolder(holder: TrailerHolder?, position: Int) {
        (holder as TrailerHolder).bindData(trailers.get(position))
    }

    inner class TrailerHolder(private val trailerView: View): RecyclerView.ViewHolder(trailerView), View.OnClickListener {
        init {
            trailerView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            listener.onClick(trailers[adapterPosition])
        }

        fun bindData(trailer: MovieTrailer) {
            Picasso.with(context).load(trailer.image).into(trailerView.findViewById(R.id.image_trailer) as ImageView)
        }
    }

    interface OnTrailerClickListener {
        fun onClick(trailer: MovieTrailer)
    }
}


