package az.aldoziflaj.popmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.data.MovieContract;


public class MovieDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int DETAILS_LOADER = 0;

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
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();

        if (intent == null) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
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
        TextView detailsOverview = (TextView) getView().findViewById(R.id.movie_description);

        String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
        String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL));
        double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
        int totalVotes = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
        int movieRuntime = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME));
        String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCRIPTION));

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
