package az.aldoziflaj.popmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.activities.MovieDetailsActivity;
import az.aldoziflaj.popmovies.adapters.MovieAdapter;
import az.aldoziflaj.popmovies.data.MovieContract;
import az.aldoziflaj.popmovies.sync.MovieSyncAdapter;

public class AllMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = AllMoviesFragment.class.getSimpleName();
    public static final int MOVIE_LOADER = 0;

    MovieAdapter movieAdapter;
    GridView moviesGridView;

    public AllMoviesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesGridView = (GridView) rootView.findViewById(R.id.movies_gridview);

        movieAdapter = new MovieAdapter(getActivity(), null, 0);

        moviesGridView.setAdapter(movieAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor currentData = (Cursor) parent.getItemAtPosition(position);
                if (currentData != null) {
                    Intent detailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                    final int MOVIE_ID_COL = currentData.getColumnIndex(MovieContract.MovieEntry._ID);
                    Uri movieUri = MovieContract.MovieEntry.buildMovieWithId(currentData.getInt(MOVIE_ID_COL));

                    detailsIntent.setData(movieUri);
                    startActivity(detailsIntent);
                }
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
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrderSetting = Utility.getPreferredSortOrder(getActivity());
        String sortOrder;
        final int NUMBER_OF_MOVIES = 20;

        if (sortOrderSetting.equals(getString(R.string.prefs_sort_default_value))) {
            //TODO:sort by popularity
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        } else {
            //sort by rating
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_IMAGE_URL},
                null,
                null,
                sortOrder + " LIMIT " + NUMBER_OF_MOVIES);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(LOG_TAG, "Cursor loaded, " + cursor.getCount() + " rows fetched");
        movieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }
}
