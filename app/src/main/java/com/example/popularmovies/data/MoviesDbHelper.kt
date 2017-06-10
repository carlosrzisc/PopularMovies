package com.example.popularmovies.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

/**
 * Database helper to build the popular movies database
 * Created by carlos on 6/7/17.
 */
class MoviesDbHelper(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private val DATABASE_VERSION = 1
        private val DATABASE_NAME = "popmovies.db"
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase?) {
        val SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MoviesContract.MovieEntry.MOVIES_TABLE + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MoviesContract.MovieEntry.MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MoviesContract.MovieEntry.TITLE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.POSTER + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.BACKDROP + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.OVERVIEW + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.VOTE_AVERAGE + " TEXT NOT NULL, " +
                MoviesContract.MovieEntry.RELEASE_DATE + " TEXT NOT NULL" + " );"

        sqLiteDatabase?.execSQL(SQL_CREATE_MOVIE_TABLE)
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase?, p1: Int, p2: Int) {
        sqLiteDatabase?.execSQL("drop table if exists " + MoviesContract.MovieEntry.MOVIES_TABLE)
        onCreate(sqLiteDatabase)
    }
}