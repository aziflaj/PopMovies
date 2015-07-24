package az.aldoziflaj.popmovies.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import az.aldoziflaj.popmovies.Constants;
import az.aldoziflaj.popmovies.R;

public class MovieAdapter extends ArrayAdapter<HashMap<String, String>> {

    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private List<HashMap<String, String>> mPosterUrlList;
    private int mLayoutResource;

    public MovieAdapter(Context context, int resource, List<HashMap<String, String>> data) {
        super(context, resource, data);
        this.mContext = context;
        this.mLayoutResource = resource;
        this.mPosterUrlList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(mLayoutResource, null);
        }

        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.movie_poster);

        HashMap<String, String> data = mPosterUrlList.get(position);

        String moviePoster = data.get(Constants.Movie.MOVIE_POSTER);

        Uri imageUri = Uri.parse(Constants.Api.IMAGE_BASE_URL).buildUpon()
                .appendPath(Constants.Api.IMAGE_SIZE_MEDIUM)
                .appendPath(moviePoster.substring(1))
                .build();

        Log.d(LOG_TAG + " - Image uri:", imageUri.toString());

        Picasso.with(mContext).load(imageUri)
                .placeholder(R.drawable.loading)
                .into(posterImageView);

        return convertView;
    }
}
