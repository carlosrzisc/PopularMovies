package com.example.popularmovies.loaders

import android.content.Context
import android.net.Uri
import android.support.v4.content.AsyncTaskLoader
import com.example.popularmovies.BuildConfig
import com.example.popularmovies.MoviesFragment
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import com.example.popularmovies.data.MoviesContract
import com.example.popularmovies.model.Movie


/**
 * AsyncTaskLoader to fetch Movies
 * Created by carlos on 5/23/17.
 */
class MoviesLoader(context: Context?, val sortBy: String?) : AsyncTaskLoader<List<Movie>>(context) {
    private val MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/"
    private val POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185"
    private val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w300"
    private val API_PARAM = "api_key"
    private val API_KEY = BuildConfig.API_KEY

    private val JSON_ROOT = "results"
    private val JSON_MOVIE_ID = "id"
    private val JSON_MOVIE_TITLE = "title"
    private val JSON_MOVIE_POSTER = "poster_path"
    private val JSON_MOVIE_RELEASE_DATE = "release_date"
    private val JSON_MOVIE_VOTE_AVERAGE = "vote_average"
    private val JSON_MOVIE_OVERVIEW = "overview"
    private val JSON_MOVIE_BACKDROP = "backdrop_path"


    override fun loadInBackground(): List<Movie> {
        if (sortBy == MoviesFragment.FAVORITES) {
            return getFavoriteMovies()
        } else {
            val builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(sortBy)
                    .appendQueryParameter(API_PARAM, API_KEY)
                    .build()

            val response = callAPI(URL(builtUri.toString()))

            return parseJson(response)
        }
    }

    private fun getFavoriteMovies(): List<Movie> {
        val favoriteMovies = ArrayList<Movie>()
        val cursor = context.contentResolver.query(MoviesContract.MovieEntry.CONTENT_URI, null, null, null, null)
        if (cursor?.moveToFirst() as Boolean) {
            do {
                val title = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.TITLE))
                val movie_id = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.MOVIE_ID))
                val poster = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.POSTER))
                val backdrop = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.BACKDROP))
                val overview = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.OVERVIEW))
                val vote_average = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.VOTE_AVERAGE))
                val release_date = cursor.getString(cursor.getColumnIndex(MoviesContract.MovieEntry.RELEASE_DATE))

                val movie = Movie(
                        id = movie_id,
                        title = title,
                        posterPath =  poster,
                        voteAverage = vote_average,
                        releaseDate = release_date,
                        overview = overview,
                        backDropPath = backdrop)
                favoriteMovies.add(movie)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return favoriteMovies
    }

    private fun callAPI(url: URL?): String? {
        val urlConnection = url!!.openConnection() as HttpURLConnection
        try {
            val movieInputStream = urlConnection.inputStream

            val scanner = Scanner(movieInputStream)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()
            if (hasInput) {
                return scanner.next()
            } else {
                return null
            }
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun parseJson(response:String?): ArrayList<Movie> {
        val moviesArrayList = ArrayList<Movie>()

        val jsonObject = JSONObject(response)

        val movies = jsonObject.getJSONArray(JSON_ROOT)

        for (i in 0..movies.length() - 1) {
            val movieJSON = movies.getJSONObject(i)
            val movieId = movieJSON.getString(JSON_MOVIE_ID)
            val posterPath = movieJSON.getString(JSON_MOVIE_POSTER)
            val movieTitle = movieJSON.getString(JSON_MOVIE_TITLE)
            val releaseDate = movieJSON.getString(JSON_MOVIE_RELEASE_DATE)
            val voteAverage = movieJSON.getString(JSON_MOVIE_VOTE_AVERAGE)
            val overview = movieJSON.getString(JSON_MOVIE_OVERVIEW)
            val backdropPath = movieJSON.getString(JSON_MOVIE_BACKDROP)

            val movieWithPath = Movie(
                    id = movieId,
                    title = movieTitle,
                    posterPath = POSTER_BASE_URL + posterPath,
                    releaseDate = releaseDate,
                    voteAverage = voteAverage,
                    overview = overview,
                    backDropPath = BACKDROP_BASE_URL + backdropPath)

            moviesArrayList.add(movieWithPath)
        }
        return moviesArrayList
    }
}