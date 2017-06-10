package com.example.popularmovies.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.SQLException
import android.net.Uri
import android.content.ContentUris
import android.provider.BaseColumns
import android.annotation.TargetApi

/**
 * Popular Movies content provider
 * Created by carlos on 6/7/17.
 */
class MoviesProvider: ContentProvider() {

    companion object {
        val MOVIES = 100
        val ITEM_MOVIE = 101
        val uriMatcher = buildUriMatcher()

        fun buildUriMatcher():UriMatcher {
            val matcher = UriMatcher(UriMatcher.NO_MATCH)
            val authority = MoviesContract.CONTENT_AUTHORITY

            matcher.addURI(authority, MoviesContract.PATH_MOVIE, MOVIES)
            matcher.addURI(authority, MoviesContract.PATH_MOVIE + "/#", ITEM_MOVIE)

            return matcher
        }
    }

    private var movieDbHelper:MoviesDbHelper? = null

    override fun onCreate(): Boolean {
        movieDbHelper = MoviesDbHelper(context)
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        val db = movieDbHelper?.writableDatabase
        val match = uriMatcher.match(uri)
        val builtUri:Uri

        when (match) {
            MOVIES -> {
                val _id = db?.insert(MoviesContract.MovieEntry.MOVIES_TABLE, null, values)
                if (_id != null && _id > 0) {
                    builtUri = MoviesContract.MovieEntry.buildMovieUri(_id)
                } else {
                    throw SQLException("Failed to insert object movie into " + uri)
                }
            }
            else -> throw UnsupportedOperationException("Uri undefined: " + uri)
        }
        context.contentResolver.notifyChange(uri, null)
        return builtUri
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val retCursor: Cursor?
        when (uriMatcher.match(uri)) {
            MOVIES -> retCursor = movieDbHelper?.readableDatabase?.query(
                    MoviesContract.MovieEntry.MOVIES_TABLE,
                    projection,
                    selection,
                    selectionArgs,
                    null, null,
                    sortOrder) as Cursor
            ITEM_MOVIE -> retCursor = movieDbHelper?.readableDatabase?.query(
                    MoviesContract.MovieEntry.MOVIES_TABLE,
                    projection,
                    BaseColumns._ID + " = ? ",
                    arrayOf(ContentUris.parseId(uri).toString()), null, null,
                    sortOrder) as Cursor
            else -> throw UnsupportedOperationException("Uri undefined: " + uri)
        }
        retCursor.setNotificationUri(context?.contentResolver, uri)
        return retCursor
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = movieDbHelper?.writableDatabase
        val match = uriMatcher.match(uri)
        val rowsUpdated: Int

        if (values == null) {
            throw IllegalArgumentException("Did not get any values to update...")
        }

        when (match) {
            MOVIES -> rowsUpdated = db?.update(MoviesContract.MovieEntry.MOVIES_TABLE, values, selection, selectionArgs) as Int
            ITEM_MOVIE -> rowsUpdated = db?.update(
                    MoviesContract.MovieEntry.MOVIES_TABLE,
                    values,
                    BaseColumns._ID + " = ?",
                    arrayOf(ContentUris.parseId(uri).toString())) as Int
            else -> throw UnsupportedOperationException("Uri undefined: " + uri)
        }
        if (rowsUpdated != 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }
        return rowsUpdated
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = movieDbHelper?.writableDatabase
        val match = uriMatcher.match(uri)
        val rowsDeleted: Int
        when (match) {
            MOVIES -> rowsDeleted = db?.delete(
                        MoviesContract.MovieEntry.MOVIES_TABLE, selection, selectionArgs) as Int
            ITEM_MOVIE ->
                rowsDeleted = db?.delete(MoviesContract.MovieEntry.MOVIES_TABLE,
                        BaseColumns._ID + " = ?",
                        arrayOf(ContentUris.parseId(uri).toString())) as Int
            else -> throw UnsupportedOperationException("Uri undefined: " + uri)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri?): String {
        val match = uriMatcher.match(uri)
        when (match) {
            MOVIES -> return MoviesContract.MovieEntry.CONTENT_TYPE
            ITEM_MOVIE -> return MoviesContract.MovieEntry.CONTENT_ITEM_TYPE
            else -> throw UnsupportedOperationException("Uri undefined: " + uri)
        }
    }

    @TargetApi(11)
    override fun shutdown() {
        movieDbHelper?.close()
        super.shutdown()
    }

}