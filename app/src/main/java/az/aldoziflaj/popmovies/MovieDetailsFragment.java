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

        String title = getActivity().getIntent().getStringExtra("movie_title");
        String image = getActivity().getIntent().getStringExtra("movie_poster");

        Uri imageUri = Uri.parse(ApiHelper.IMAGE_BASE_URL).buildUpon()
                .appendPath(ApiHelper.IMAGE_DEFAULT_SIZE)
                .appendPath(image.substring(1))
                .build();

        detailsTitle.setText(title);
        Picasso.with(getActivity()).load(imageUri).into(detailsPoster);

        Log.d("MovieDetailsFragment", image);

        return rootView;
    }
}
