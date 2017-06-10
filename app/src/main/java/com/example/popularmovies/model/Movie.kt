package com.example.popularmovies.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Movie model object
 * Created by carlos on 5/23/17.
 */
data class Movie(
        var id:String="",
        var title: String= "",
        var posterPath: String="",
        var releaseDate: String="",
        var voteAverage: String="",
        var overview: String="",
        var backDropPath: String=""): Parcelable {

    constructor(source: Parcel): this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString(),
            source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(this.id)
        dest?.writeString(this.title)
        dest?.writeString(this.posterPath)
        dest?.writeString(this.releaseDate)
        dest?.writeString(this.voteAverage)
        dest?.writeString(this.overview)
        dest?.writeString(this.backDropPath)
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<Movie> = object : Parcelable.Creator<Movie> {
            override fun createFromParcel(source: Parcel): Movie {
                return Movie(source)
            }

            override fun newArray(size: Int): Array<Movie?> {
                return arrayOfNulls(size)
            }
        }
    }
}

