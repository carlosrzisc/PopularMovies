package com.example.popularmovies

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.NavUtils
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import com.example.popularmovies.model.Movie

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val collapsingToolbar = findViewById(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val movie = intent.getParcelableExtra<Movie>(DetailsFragment.ARG_MOVIE)

        collapsingToolbar.setExpandedTitleColor(ContextCompat.getColor(this, R.color.transparent))
        collapsingToolbar.setCollapsedTitleTextColor(Color.rgb(255, 255, 255));
        collapsingToolbar.title = movie.title

        val backdropImageView = findViewById(R.id.expandedImage) as ImageView
        Picasso.with(this).load(movie.backDropPath).into(backdropImageView)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.details_container, DetailsFragment.newInstance(movie)).commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?):Boolean {
        when(item?.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
