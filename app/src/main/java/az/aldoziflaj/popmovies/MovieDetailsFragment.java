package az.aldoziflaj.popmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailsFragment extends Fragment {

    public MovieDetailsFragment() {}

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
        String rating = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_RATING);
        String releaseDate = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_RELEASE_DATE);
        String overview = getActivity().getIntent().getStringExtra(Constants.Movie.MOVIE_OVERVIEW);

        Uri posterUri = Uri.parse(Constants.Api.IMAGE_BASE_URL).buildUpon()
                .appendPath(Constants.Api.IMAGE_DEFAULT_SIZE)
                .appendPath(poster.substring(1))
                .build();

        Picasso.with(getActivity()).load(posterUri).into(detailsPoster);
        detailsOverview.setText(overview);
        detailsTitle.setText(title);
        detailsRating.setText(rating);
        detailsReleaseDate.setText(releaseDate);

        Log.d("MovieDetailsFragment", overview);

        return rootView;
    }
}
