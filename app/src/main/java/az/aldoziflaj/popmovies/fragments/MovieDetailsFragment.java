package az.aldoziflaj.popmovies.fragments;

import android.content.ContentValues;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.adapters.TrailersAdapter;
import az.aldoziflaj.popmovies.data.MovieContract;


public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();
    public static final int DETAILS_LOADER = 0;
    public static final int TRAILERS_LOADER = 1;
    public static final int REVIEWS_LOADER = 2;

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }

        Uri movieUri = intent.getData();
        int movieId = Utility.fetchMovieIdFromUri(getActivity(), movieUri);

        switch (id) {
            case DETAILS_LOADER:
                Log.d(LOG_TAG, "Details loader");
                return new CursorLoader(
                        getActivity(),
                        movieUri,
                        null, null, null, null);

            case TRAILERS_LOADER:
                Log.d(LOG_TAG, "Trailers loader");
                return new CursorLoader(
                        getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        null, // all columns
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movieId)},
                        null);

            case REVIEWS_LOADER:
                Log.d(LOG_TAG, "Reviews loader");
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        null, // all columns
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movieId)},
                        null);

            default:
                throw new UnsupportedOperationException("Unknown loader");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case DETAILS_LOADER:
                loadMovieDetails(cursor);
                break;

            case TRAILERS_LOADER:
                loadMovieTrailers(cursor);
                break;

            case REVIEWS_LOADER:
                Log.d(LOG_TAG, "Loading Reviews");
                break;

            default:
                Log.e(LOG_TAG, "Loading something miscellaneous?!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadMovieDetails(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return;
        }

        if (getView() == null) {
            //wtf?
            return;
        }

        TextView detailsReleaseYear = (TextView) getView().findViewById(R.id.movie_year);
        ImageView detailsPoster = (ImageView) getView().findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) getView().findViewById(R.id.movie_rating);
        TextView detailsRuntime = (TextView) getView().findViewById(R.id.movie_length);
        final TextView detailsOverview = (TextView) getView().findViewById(R.id.movie_description);
        Button markAsFavoriteBtn = (Button) getView().findViewById(R.id.favorite_btn);

        final int _ID = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
        String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL));
        double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        int totalVotes = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
        int movieRuntime = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME));
        String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION));
        final int IS_FAVORITE = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));

        if (IS_FAVORITE == 1) {
            String unmark = getActivity().getString(R.string.details_button_favorite_remove);
            markAsFavoriteBtn.setText(unmark);
        } else {
            String mark = getActivity().getString(R.string.details_button_favorite_add);
            markAsFavoriteBtn.setText(mark);
        }

        Uri posterUri = Uri.parse(Config.IMAGE_BASE_URL).buildUpon()
                .appendPath(getActivity().getString(R.string.api_image_size_default))
                .appendPath(poster.substring(1)) //remove the heading slash
                .build();

        Picasso.with(getActivity()).load(posterUri)
                .placeholder(R.drawable.loading)
                .into(detailsPoster);

        detailsReleaseYear.setText(Utility.getReleaseYear(releaseDate));
        detailsRuntime.setText(movieRuntime + "min");

        detailsOverview.setText(overview);
        getActivity().setTitle(title);

        detailsRating.setText(
                String.format(getActivity().getString(R.string.format_ratings), rating, totalVotes));

        markAsFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (IS_FAVORITE) {
                    case 0: {
                        // movie is not favorited
                        // mark it
                        ContentValues addFavorite = new ContentValues();
                        addFavorite.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1); //mark as favorite

                        int updatedRows = getActivity().getContentResolver().update(
                                MovieContract.MovieEntry.CONTENT_URI,
                                addFavorite,
                                MovieContract.MovieEntry._ID + " = ?",
//                            new String[]{Integer.toString(movieId)}
                                new String[]{String.valueOf(_ID)}
                        );

                        if (updatedRows <= 0) {
                            Log.d(LOG_TAG, "Movie not marked as favorite");
                        } else {
                            Log.d(LOG_TAG, "Movie marked as favorite");
                        }
                    }
                    break;

                    case 1: {
                        // movie is favorited
                        // unmark it
                        ContentValues removeFavorite = new ContentValues();
                        removeFavorite.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0); //unmark as favorite

                        int updatedRows = getActivity().getContentResolver().update(
                                MovieContract.MovieEntry.CONTENT_URI,
                                removeFavorite,
                                MovieContract.MovieEntry._ID + " = ?",
                                new String[]{String.valueOf(_ID)}
//                            new String[]{Integer.toString(movieId)}
                        );

                        if (updatedRows < 0) {
                            Log.d(LOG_TAG, "Movie not unmarked as favorite");
                        } else {
                            Log.d(LOG_TAG, "Movie unmarked as favorite");
                        }
                    }
                    break;

                    default:
                        Log.e(LOG_TAG, "What is this?!");

                }
            }
        });
    }

    private void loadMovieTrailers(Cursor cursor) {
        if (getView() == null) {
            return; //error
        }

        ListView trailerListView = (ListView) getView().findViewById(R.id.trailer_listview);
        TrailersAdapter adapter = new TrailersAdapter(getActivity(), cursor, 0);
        trailerListView.setAdapter(adapter);

        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    int youtubeKeyColumn = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
                    String youtubeKey = cursor.getString(youtubeKeyColumn);
                    Uri videoUri = Uri.parse(Config.YOUTUBE_TRAILER_URL + youtubeKey);

                    Intent playTrailer = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(playTrailer);
                }
            }
        });
    }
}
