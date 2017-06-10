package com.example.popularmovies

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import android.content.Intent
import android.net.Uri
import android.content.ContentValues
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.example.popularmovies.data.MoviesContract
import com.example.popularmovies.loaders.TrailersLoader
import com.example.popularmovies.model.Movie
import com.example.popularmovies.model.MovieTrailer
import com.example.popularmovies.utilities.Utility

/**
 * Movie details fragment
 * Use the [DetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailsFragment : Fragment() {
    private var paramMovie: Movie? = null
    private var trailersAdapter: TrailersAdapter? = null
    private var loaderCallbacks: LoaderManager.LoaderCallbacks<List<MovieTrailer>>? = null

    var favoriteSwitch:ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            paramMovie = arguments.getParcelable<Movie>(ARG_MOVIE)

            trailersAdapter = TrailersAdapter(context = activity, listener = object: TrailersAdapter.OnTrailerClickListener {
                override fun onClick(trailer: MovieTrailer) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailer.link))
                    activity.startActivity(intent)
                }
            })

            loaderCallbacks = object: LoaderManager.LoaderCallbacks<List<MovieTrailer>> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<MovieTrailer>> {
                    return TrailersLoader(activity, paramMovie?.id)
                }

                override fun onLoaderReset(loader: Loader<List<MovieTrailer>>?) {
                    trailersAdapter?.trailers?.clear()
                }

                override fun onLoadFinished(loader: Loader<List<MovieTrailer>>?, data: List<MovieTrailer>?) {
                    trailersAdapter?.trailers = data as ArrayList<MovieTrailer>
                    trailersAdapter?.notifyDataSetChanged()
                }
            }
            if (Utility.hasInternetConnection(activity)) {
                activity.supportLoaderManager.initLoader(0, null, loaderCallbacks)?.forceLoad()
            } else {
                Toast.makeText(activity, getString(R.string.error_no_connection), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.fragment_detail, container, false)

        val moviePoster = rootView?.findViewById(R.id.details_image_poster) as ImageView
        val movieTitle = rootView.findViewById(R.id.details_movie_title) as TextView
        val movieRelease = rootView.findViewById(R.id.details_movie_release) as TextView
        val movieRating = rootView.findViewById(R.id.details_movie_rating) as TextView
        val movieOverview = rootView.findViewById(R.id.details_movie_overview) as TextView
        val trailers = rootView.findViewById(R.id.list_trailers) as RecyclerView
        favoriteSwitch = rootView.findViewById(R.id.details_favorite_switch) as ImageView

        Picasso.with(activity).load(paramMovie?.posterPath).into(moviePoster)
        movieTitle.text = paramMovie?.title
        movieRelease.text = paramMovie?.releaseDate
        movieRating.text = paramMovie?.voteAverage
        movieOverview.text = paramMovie?.overview

        trailers.setHasFixedSize(true)
        trailers.layoutManager = LinearLayoutManager(activity)
        trailers.adapter = trailersAdapter

        if (isFavoriteMovie(paramMovie?.id as String)) {
            favoriteSwitch?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_black_24dp))
        }
        favoriteSwitch?.setOnClickListener { toggleFavMovie(paramMovie as Movie) }

        return rootView
    }

    companion object {
        val ARG_MOVIE = "param_movie"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @param movie Movie
         * *
         * @return A new instance of fragment DetailFragment.
         */
        fun newInstance(movie: Movie): DetailsFragment {
            val fragment = DetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_MOVIE, movie)
            fragment.arguments = args
            return fragment
        }
    }

    private fun isFavoriteMovie(movieId: String): Boolean {
        var movieExists = false
        val cursor = activity.contentResolver.query(
                MoviesContract.MovieEntry.CONTENT_URI,
                null,
                MoviesContract.MovieEntry.MOVIE_ID + " = ?",
                arrayOf(movieId),
                null )

        if (cursor != null && cursor.count > 0) {
            movieExists = true
        }
        cursor.close()
        return movieExists
    }

    private fun toggleFavMovie(movie: Movie) {
        if (isFavoriteMovie(movie.id)) {
            removeMovie(movie.id)
        } else {
            addMovie(movie)
        }
    }

    private fun removeMovie(id: String) {
        activity.contentResolver.delete(
                MoviesContract.MovieEntry.CONTENT_URI,
                MoviesContract.MovieEntry.MOVIE_ID + " = ?",
                arrayOf(id) )
        favoriteSwitch?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_border_black_24dp))
    }

    private fun addMovie(movie: Movie) {
        val values = ContentValues()
        values.put(MoviesContract.MovieEntry.MOVIE_ID, movie.id)
        values.put(MoviesContract.MovieEntry.TITLE, movie.title)
        values.put(MoviesContract.MovieEntry.POSTER, movie.posterPath)
        values.put(MoviesContract.MovieEntry.BACKDROP, movie.backDropPath)
        values.put(MoviesContract.MovieEntry.OVERVIEW, movie.overview)
        values.put(MoviesContract.MovieEntry.RELEASE_DATE, movie.releaseDate)
        values.put(MoviesContract.MovieEntry.VOTE_AVERAGE, movie.voteAverage)

        activity.contentResolver.insert(MoviesContract.MovieEntry.CONTENT_URI, values)

        favoriteSwitch?.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_favorite_black_24dp))
    }

}// Required empty public constructor
