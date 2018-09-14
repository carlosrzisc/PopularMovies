package com.example.popularmovies.loaders

import android.content.Context
import com.example.popularmovies.model.MovieTrailer

/**
 * AsyncTaskLoader to fetch Movie trailers
 * Created by carlos on 6/6/17.
 */
class TrailersLoader(context: Context, val movieId: String?) : android.support.v4.content.AsyncTaskLoader<List<MovieTrailer>>(context) {
    private val MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/"
    private val API_KEY = com.example.popularmovies.BuildConfig.API_KEY
    private val API_PARAM = "api_key"
    private val VIDEOS_PATH = "videos"

    private val JSON_ROOT = "results"
    private val JSON_VIDEO_KEY = "key"

    val YOUTUBE_IMAGE_URL_PREFIX = "http://img.youtube.com/vi/"
    val YOUTUBE_IMAGE_URL_SUFFIX = "/0.jpg"
    val YOUTUBE_LINK_URL = "https://www.youtube.com/watch?v="

    override fun loadInBackground(): List<com.example.popularmovies.model.MovieTrailer> {
        val builtUri = android.net.Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(VIDEOS_PATH)
                .appendQueryParameter(API_PARAM, API_KEY)
                .build()

        val response = callAPI(java.net.URL(builtUri.toString()))

        return parseJson(response)
    }

    private fun callAPI(url: java.net.URL?): String? {
        val urlConnection = url!!.openConnection() as java.net.HttpURLConnection
        try {
            val movieInputStream = urlConnection.inputStream

            val scanner = java.util.Scanner(movieInputStream)
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

    private fun parseJson(response: String?): List<com.example.popularmovies.model.MovieTrailer> {
        val trailerJson = org.json.JSONObject(response)
        val trailerArray = trailerJson.getJSONArray(JSON_ROOT)
        val trailerArrayList = java.util.ArrayList<MovieTrailer>()

        for (i in 0..trailerArray.length() - 1) {
            // there are some movies that have too much trailers, so I decided to restrict to a
            // macimum of 5 trailers
            if (i == 5) break

            val trailerObject = trailerArray.getJSONObject(i)

            val key = trailerObject.getString(JSON_VIDEO_KEY)

            val image = YOUTUBE_IMAGE_URL_PREFIX + key + YOUTUBE_IMAGE_URL_SUFFIX
            val link = YOUTUBE_LINK_URL + key

            trailerArrayList.add(com.example.popularmovies.model.MovieTrailer(image, link))
        }
        return trailerArrayList
    }
}