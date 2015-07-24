package az.aldoziflaj.popmovies.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import az.aldoziflaj.popmovies.Constants;
import az.aldoziflaj.popmovies.R;


public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        TextView detailsTitle = (TextView) rootView.findViewById(R.id.details_movie_title);
        ImageView detailsPoster = (ImageView) rootView.findViewById(R.id.details_movie_poster);
        TextView detailsRating = (TextView) rootView.findViewById(R.id.movie_rating);
        TextView detailsReleaseDate = (TextView) rootView.findViewById(R.id.movie_release_date);
        TextView detailsOverview = (TextView) rootView.findViewById(R.id.movie_description);

        String title = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_TITLE);
        String poster = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_POSTER);
        double rating = Double.parseDouble(getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_RATING));
        String releaseDate = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_RELEASE_DATE);
        int totalVotes = Integer.parseInt(getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_TOTAL_VOTES));
        String overview = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_OVERVIEW);

        Uri posterUri = Uri.parse(Constants.Api.IMAGE_BASE_URL).buildUpon()
                .appendPath(Constants.Api.IMAGE_DEFAULT_SIZE)
                .appendPath(poster.substring(1))
                .build();

        Picasso.with(getActivity()).load(posterUri)
                .placeholder(R.drawable.loading)
                .into(detailsPoster);

        detailsOverview.setText(overview);
        detailsTitle.setText(title);

        detailsRating.setText(
                String.format(getActivity().getString(R.string.format_ratings), rating, totalVotes));

        detailsReleaseDate.setText(
                String.format(getActivity().getString(R.string.format_release_date), releaseDate));

        return rootView;
    }
}
