package com.example.popularmovies.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

/**
 * Popular Movies db contract
 * Created by carlos on 6/7/17.
 */
object MoviesContract {
    val CONTENT_AUTHORITY: String = "com.example.popularmovies"
    val BASE_CONTENT_URI: Uri = Uri.parse("content://" + CONTENT_AUTHORITY)
    val PATH_MOVIE: String = "movie"

    /* Inner class that setups up movie table structure */
    class MovieEntry: BaseColumns {
        companion object {
            val MOVIES_TABLE = "movie"
            val MOVIE_ID = "movie_id"
            val TITLE = "title"
            val POSTER = "poster"
            val BACKDROP = "backdrop"
            val OVERVIEW = "overview"
            val VOTE_AVERAGE = "vote_average"
            val RELEASE_DATE = "release_date"

            val CONTENT_URI: Uri =
                    BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build()
            val CONTENT_TYPE =
                    ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE
            val CONTENT_ITEM_TYPE =
                    ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE

            fun buildMovieUri(id:Long):Uri {
                return ContentUris.withAppendedId(CONTENT_URI, id)
            }
        }
    }
}