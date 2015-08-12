package az.aldoziflaj.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.data.MovieContract;

public class MovieAdapter extends CursorAdapter {

    public static final String LOG_TAG = MovieAdapter.class.getSimpleName();

    public MovieAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.movie_poster, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView posterImageView = (ImageView) view.findViewById(R.id.movie_poster);

        int moviePosterColumn = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IMAGE_URL);
        String moviePoster = cursor.getString(moviePosterColumn);

        Uri imageUri = Uri.parse(Config.IMAGE_BASE_URL).buildUpon()
                .appendPath(context.getString(R.string.api_image_size_medium))
                .appendPath(moviePoster.substring(1))
                .build();

        Log.d(LOG_TAG + " - Image uri:", imageUri.toString());

        Picasso.with(context).load(imageUri)
                .placeholder(R.drawable.loading)
                .into(posterImageView);
    }
}
