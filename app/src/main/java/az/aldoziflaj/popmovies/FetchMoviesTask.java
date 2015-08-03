package az.aldoziflaj.popmovies;


import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchMoviesTask extends AsyncTask<String, Void, Void> {
    public final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    Context mContext;

    public FetchMoviesTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {
        if (params.length == 0) {
            Log.e(LOG_TAG, "Why is this called without params?!");
            return null;
        }

        String sortOrder;
        if (params[0].equals(mContext.getString(R.string.movie_sort_default))) {
            sortOrder = mContext.getString(R.string.api_sort_popularity);
        } else {
            sortOrder = mContext.getString(R.string.api_sort_votes);
        }

        HttpURLConnection urlConnection = null;
        String moviesJsonString;
        BufferedReader reader = null;

        final String SORT_KEY = mContext.getString(R.string.api_param_sort);
        final String API_KEY_QUERY = mContext.getString(R.string.api_param_api_key);

        try {
            Uri moviesUri = Uri.parse(Config.API_BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_KEY, sortOrder)
                    .appendQueryParameter(API_KEY_QUERY, Config.API_KEY)
                    .build();

            Log.d(LOG_TAG, moviesUri.toString());

            URL url = new URL(moviesUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            if (sb.length() == 0) {
                Log.d(LOG_TAG, "No response from the server");
                return null;
            }

            moviesJsonString = sb.toString();
            Utility.storeJsonResponseMovies(mContext, moviesJsonString);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            return null;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            return null;

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error: " + e.getMessage());

                }
            }
        }
        return null;
    }
}
