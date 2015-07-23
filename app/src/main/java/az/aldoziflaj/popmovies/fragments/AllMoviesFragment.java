package az.aldoziflaj.popmovies.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.HashMap;

import az.aldoziflaj.popmovies.Constants;
import az.aldoziflaj.popmovies.FetchMoviesTask;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.activities.MovieDetailsActivity;
import az.aldoziflaj.popmovies.adapters.MovieAdapter;

public class AllMoviesFragment extends Fragment {
    public static final String LOG_TAG = AllMoviesFragment.class.getSimpleName();
    ArrayList<HashMap<String, String>> movieList;
    MovieAdapter movieAdapter;
    GridView moviesGridView;

    public AllMoviesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesGridView = (GridView) rootView.findViewById(R.id.movies_gridview);

        movieList = new ArrayList<>();

        // initialize an empty adapter
        movieAdapter = new MovieAdapter(
                getActivity(),
                R.layout.movie_poster,
                new ArrayList<String>());

        moviesGridView.setAdapter(movieAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> itemClicked = movieList.get(position);
                String movieTitle = itemClicked.get(Constants.Movie.MOVIE_TITLE);
                String moviePoster = itemClicked.get(Constants.Movie.MOVIE_POSTER);
                String movieReleaseDate = itemClicked.get(Constants.Movie.MOVIE_RELEASE_DATE);
                String movieRating = itemClicked.get(Constants.Movie.MOVIE_RATING);
                String movieTotalVotes = itemClicked.get(Constants.Movie.MOVIE_TOTAL_VOTES);
                String movieOverview = itemClicked.get(Constants.Movie.MOVIE_OVERVIEW);

                Intent detailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                detailsIntent.putExtra(Constants.Movie.MOVIE_TITLE, movieTitle);
                detailsIntent.putExtra(Constants.Movie.MOVIE_POSTER, moviePoster);
                detailsIntent.putExtra(Constants.Movie.MOVIE_RELEASE_DATE, movieReleaseDate);
                detailsIntent.putExtra(Constants.Movie.MOVIE_RATING, movieRating);
                detailsIntent.putExtra(Constants.Movie.MOVIE_TOTAL_VOTES, movieTotalVotes);
                detailsIntent.putExtra(Constants.Movie.MOVIE_OVERVIEW, movieOverview);

                startActivity(detailsIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_allmovies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            updateMovies();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(
                getString(R.string.movie_sort_key),
                getString(R.string.movie_sort_default));

        Log.d(LOG_TAG, sortOrder);

        FetchMoviesTask task = new FetchMoviesTask(getActivity(), movieAdapter);
        task.execute(sortOrder);
    }
}
