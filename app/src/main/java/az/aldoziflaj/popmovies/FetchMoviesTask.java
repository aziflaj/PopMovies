package az.aldoziflaj.popmovies;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import az.aldoziflaj.popmovies.adapters.MovieAdapter;

public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<Movie>> {
    public final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    Toast errorInConnection;
    ProgressDialog dialog;
    Context mContext;
    MovieAdapter mAdapter;

    public FetchMoviesTask(Context context, MovieAdapter adapter) {
        this.mContext = context;
        this.mAdapter = adapter;
    }

    @Override
    protected void onPreExecute() {
        errorInConnection = Toast.makeText(mContext,
                "Can't connect to the server",
                Toast.LENGTH_LONG);
        dialog = ProgressDialog.show(mContext, "Please wait", "Updating the movies");
    }

    @Override
    protected ArrayList<Movie> doInBackground(String... params) {
        if (params.length == 0) {
            Log.e(LOG_TAG, "Why is this called without params?!");
            return null;
        }

        String sortOrder;
        if (params[0].equals(mContext.getString(R.string.movie_sort_default))) {
            sortOrder = Constants.Api.SORT_BY_POPULARITY;
        } else {
            sortOrder = Constants.Api.SORT_BY_VOTES;
        }

        HttpURLConnection urlConnection = null;
        String moviesJsonString = null;
        BufferedReader reader = null;

        try {
            Uri moviesUri = Uri.parse(Constants.Api.API_BASE_URL).buildUpon()
                    .appendQueryParameter(Constants.Api.SORT_KEY, sortOrder)
                    .appendQueryParameter(Constants.Api.API_KEY_QUERY, Constants.Api.API_KEY)
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

        return Utility.fetchMovieListFromJSON(mContext, moviesJsonString);
    }

    @Override
    protected void onPostExecute(ArrayList<Movie> movieList) {
        if (movieList == null) {
            errorInConnection.show();
            return;
        }

        Log.d(LOG_TAG, "movieList updated");

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Update the movieAdapter
        mAdapter.clear();
        for (Movie item : movieList) {
            mAdapter.add(item);
        }
    }
}
