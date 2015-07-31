package az.aldoziflaj.popmovies.fragments;

import android.database.Cursor;
import android.os.Bundle;
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

import az.aldoziflaj.popmovies.FetchMoviesTask;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.adapters.MovieAdapter;
import az.aldoziflaj.popmovies.data.MovieContract;

public class AllMoviesFragment extends Fragment {
    public static final String LOG_TAG = AllMoviesFragment.class.getSimpleName();

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

        String sortOrderSetting = Utility.getDefaultSortOrder(getActivity());
        String sortOrder;

        if (sortOrderSetting.equals(getString(R.string.movie_sort_default))) {
            //sort by popularity
            sortOrder = MovieContract.MovieTable.COLUMN_VOTE_COUNT + " DESC";
        } else {
            //sort by rating
            sortOrder = MovieContract.MovieTable.COLUMN_VOTE_AVERAGE + " DESC";
        }

        Cursor cursor = getActivity().getContentResolver().query(
                MovieContract.MovieTable.CONTENT_URI, null, null, null, sortOrder);

        movieAdapter = new MovieAdapter(getActivity(), cursor, 0);

        moviesGridView.setAdapter(movieAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Movie itemClicked = movieAdapter.getItem(position);
//                String movieTitle = itemClicked.getTitle();
//                String moviePoster = itemClicked.getPosterPath();
//                String movieReleaseDate = itemClicked.getReleaseDate();
//                double movieRating = itemClicked.getRating();
//                int movieTotalVotes = itemClicked.getVoteCount();
//                String movieOverview = itemClicked.getDescription();
//
//                Intent detailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
//                detailsIntent.putExtra(Constants.Movie.MOVIE_TITLE, movieTitle);
//                detailsIntent.putExtra(Constants.Movie.MOVIE_POSTER, moviePoster);
//                detailsIntent.putExtra(Constants.Movie.MOVIE_RELEASE_DATE, movieReleaseDate);
//                detailsIntent.putExtra(Constants.Movie.MOVIE_RATING, movieRating);
//                detailsIntent.putExtra(Constants.Movie.MOVIE_TOTAL_VOTES, movieTotalVotes);
//                detailsIntent.putExtra(Constants.Movie.MOVIE_OVERVIEW, movieOverview);
//                startActivity(detailsIntent);
            }
        });

        //cursor.close();
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
        String sortOrder = Utility.getDefaultSortOrder(getActivity());

        Log.d(LOG_TAG, sortOrder);

        FetchMoviesTask task = new FetchMoviesTask(getActivity());
        task.execute(sortOrder);
    }
}
