package az.aldoziflaj.popmovies;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieAdapter extends SimpleAdapter {
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_POSTER = "poster";

    private Context appContext;
    private LayoutInflater inflater;

    public MovieAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.appContext = context;
        this.inflater = (LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.movie_info, null);
        }

        HashMap<String, String> data = (HashMap<String, String>) getItem(position);

        TextView titleTextView = (TextView) convertView.findViewById(R.id.movie_title_textview);
        ImageView posterImageView = (ImageView) convertView.findViewById(R.id.movie_poster);

        String movieTitle = data.get(MOVIE_TITLE);
        String moviePoster = data.get(MOVIE_POSTER);

        titleTextView.setText(movieTitle);

        Uri imageUri = Uri.parse(ApiHelper.IMAGE_BASE_URL).buildUpon()
                .appendPath(ApiHelper.IMAGE_DEFAULT_SIZE)
                .appendPath(moviePoster).build();

        Log.d("MovieAdapter", imageUri.toString());

        Picasso.with(appContext).load(imageUri.toString()).into(posterImageView);

        return convertView;
    }
}
