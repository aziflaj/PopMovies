package az.aldoziflaj.popmovies.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.commonsware.cwac.merge.MergeAdapter;
import com.squareup.picasso.Picasso;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.adapters.CommentsAdapter;
import az.aldoziflaj.popmovies.adapters.TrailersAdapter;
import az.aldoziflaj.popmovies.data.MovieContract;


public class MovieDetailsFragment extends Fragment {
    public static final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    public MovieDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        MergeAdapter mergeAdapter = new MergeAdapter();

        Uri movieUri = intent.getData();
        int movieId = Utility.fetchMovieIdFromUri(getActivity(), movieUri);

        Cursor detailsCursor = getActivity().getContentResolver()
                .query(movieUri, null, null, null, null);

        View detailsView = populateDetailsView(detailsCursor);
        mergeAdapter.addView(detailsView);

        Cursor trailersCursor = getActivity().getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        TrailersAdapter trailersAdapter = new TrailersAdapter(getActivity(), trailersCursor, 0);
        mergeAdapter.addAdapter(trailersAdapter);

        Cursor commentsCursor = getActivity().getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null, // all columns
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                new String[]{String.valueOf(movieId)},
                null);

        CommentsAdapter commentsAdapter = new CommentsAdapter(getActivity(), commentsCursor, 0);
        mergeAdapter.addAdapter(commentsAdapter);

        ListView detailsListView = (ListView) rootView.findViewById(R.id.details_listview);
        detailsListView.setAdapter(mergeAdapter);

        return rootView;
    }


    private View populateDetailsView(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return null;
        }

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_movie_details, null, false);

        if (view == null) {
            return null;
        }

        TextView detailsReleaseYear = (TextView) view.findViewById(R.id.movie_year);
        ImageView detailsPoster = (ImageView) view.findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) view.findViewById(R.id.movie_rating);
        TextView detailsRuntime = (TextView) view.findViewById(R.id.movie_length);
        final TextView detailsOverview = (TextView) view.findViewById(R.id.movie_description);
        Button markAsFavoriteBtn = (Button) view.findViewById(R.id.favorite_btn);

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

        return view;
    }
}
