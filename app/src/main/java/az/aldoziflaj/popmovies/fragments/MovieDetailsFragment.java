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

        //TODO: Populate with the new data
        //TextView detailsTitle = (TextView) getView().findViewById(R.id.details_movie_title);
        ImageView detailsPoster = (ImageView) getView().findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) getView().findViewById(R.id.movie_rating);
//        TextView detailsReleaseDate = (TextView) getView().findViewById(R.id.movie_release_date);
        TextView detailsOverview = (TextView) getView().findViewById(R.id.movie_description);

        String title = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTable.COLUMN_TITLE));
        String poster = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTable.COLUMN_IMAGE_URL));
        double rating = cursor.getDouble(cursor.getColumnIndex(MovieContract.MovieTable.COLUMN_VOTE_AVERAGE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTable.COLUMN_RELEASE_DATE));
        int totalVotes = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieTable.COLUMN_VOTE_COUNT));
        String overview = cursor.getString(cursor.getColumnIndex(MovieContract.MovieTable.COLUMN_DESCRIPTION));

        Uri posterUri = Uri.parse(Config.IMAGE_BASE_URL).buildUpon()
                .appendPath(getActivity().getString(R.string.api_image_size_default))
                .appendPath(poster.substring(1)) //remove the heading slash
                .build();

        Picasso.with(getActivity()).load(posterUri)
                .placeholder(R.drawable.loading)
                .into(detailsPoster);

        detailsOverview.setText(overview);
//        detailsTitle.setText(title);
        getActivity().setTitle(title);

        detailsRating.setText(
                String.format(getActivity().getString(R.string.format_ratings), rating, totalVotes));

//        detailsReleaseDate.setText(
//                String.format(getActivity().getString(R.string.format_release_date), releaseDate));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
