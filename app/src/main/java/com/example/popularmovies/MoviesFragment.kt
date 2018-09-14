package com.example.popularmovies

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.example.popularmovies.model.Movie
import android.widget.Toast
import com.example.popularmovies.loaders.MoviesLoader
import com.example.popularmovies.utilities.Utility


/**
 * A simple [Fragment] subclass.
 */
class MoviesFragment : Fragment() {

    companion object {
        val FAVORITES = "favorites"
    }

    private val COLUMNS: Int = 3
    private val URL_POPULARITY = "popular"
    private val URL_RATING = "top_rated"
    private val PREF = "sort"

    private var loaderCallbacks: LoaderManager.LoaderCallbacks<List<Movie>>? = null
    private var moviesAdapter: MoviesAdapter? = null
    private var preferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        preferences = PreferenceManager.getDefaultSharedPreferences(activity)

        moviesAdapter = activity?.let {
            MoviesAdapter(context = it) { movie ->
                val intent = Intent(activity, DetailsActivity::class.java)
                intent.putExtra(DetailsFragment.ARG_MOVIE, movie)
                startActivity(intent)
            }
        }

        loaderCallbacks = object : LoaderManager.LoaderCallbacks<List<Movie>> {
            override fun onLoaderReset(loader: Loader<List<Movie>>) {
                moviesAdapter?.movies?.clear()
            }

            override fun onLoadFinished(loader: Loader<List<Movie>>, data: List<Movie>) {
                moviesAdapter?.movies = data as ArrayList<Movie>
                moviesAdapter?.notifyDataSetChanged()
            }

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<Movie>> {
                val sortBy = preferences!!.getString(PREF, URL_POPULARITY)
                return activity?.let { MoviesLoader(it, sortBy) } as Loader<List<Movie>>
            }
        }
        activity?.let { context ->
            if (Utility.hasInternetConnection(context)) {
                context.supportLoaderManager.initLoader(
                        0,
                        null,
                        loaderCallbacks as LoaderManager.LoaderCallbacks<List<Movie>>).forceLoad()
            } else {
                Toast.makeText(context, getString(R.string.error_no_connection), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_movies, container, false)

        val recyclerView = rootView?.findViewById(R.id.recyclerview_movies) as RecyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(activity, COLUMNS)
        recyclerView.adapter = moviesAdapter

        return rootView
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movies_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val editor = preferences?.edit()
        editor?.apply()
        when (item.itemId) {
            R.id.popularity -> editor?.putString(PREF, URL_POPULARITY)
            R.id.rating -> editor?.putString(PREF, URL_RATING)
            R.id.favorites -> editor?.putString(PREF, FAVORITES)
        }
        editor?.apply()
        fetchMovies()
        item.isChecked = true
        return super.onOptionsItemSelected(item)
    }

    private fun fetchMovies() {
        activity?.let { context ->
            if (Utility.hasInternetConnection(context)) {
                context.supportLoaderManager.restartLoader(
                        0,
                        null,
                        loaderCallbacks as LoaderManager.LoaderCallbacks<List<Movie>>)
                            .forceLoad()
            } else {
                Toast.makeText(context, getString(R.string.error_no_connection), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        val sortBy = preferences?.getString(PREF, URL_POPULARITY)
        when (sortBy) {
            URL_POPULARITY -> menu.findItem(R.id.popularity).isChecked = true
            URL_RATING -> menu.findItem(R.id.rating).isChecked = true
            FAVORITES -> menu.findItem(R.id.favorites).isChecked = true
        }
    }
}
