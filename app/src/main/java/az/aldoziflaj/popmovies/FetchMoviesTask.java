package az.aldoziflaj.popmovies;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import az.aldoziflaj.popmovies.adapters.MovieAdapter;

public class FetchMoviesTask extends AsyncTask<String, Void, String> {
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
    protected String doInBackground(String... params) {
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
        String moviesJSONString = null;
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

            moviesJSONString = sb.toString();

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

        return moviesJSONString;
    }

    @Override
    protected void onPostExecute(String moviesJsonString) {
        if (moviesJsonString == null) {
            errorInConnection.show();
            return;
        }

        // Get an ArrayList<HashMap> from JSON
        //movieList = fetchMovieListFromJSON(moviesJsonString);
        Log.d(LOG_TAG, "movieList updated");

        // Get a String[] of poster URLs from JSON
        String[] posterUrlList = Utility.fetchPosterListFromJson(moviesJsonString);

        if (posterUrlList == null) {
            Log.e(LOG_TAG, "Poster list empty (?!)");
            return;
        }

        if (dialog.isShowing()) {
            dialog.dismiss();
        }

        // Update the new movieAdapter
        mAdapter.clear();
        for (String poster : posterUrlList) {
            mAdapter.add(poster);
        }

    }

    private ArrayList<HashMap<String, String>> fetchMovieListFromJSON(String jsonString) {
        ArrayList<HashMap<String, String>> kvPair = new ArrayList<>();

        try {
            JSONArray jsonMovieList = (new JSONObject(jsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG_TAG, movieListLength + " items fetched");

            for (int i = 0; i < movieListLength; i++) {
                JSONObject currentMovie = jsonMovieList.getJSONObject(i);
                HashMap<String, String> item = new HashMap<>();

                //get the movie data from the JSON response
                item.put(Constants.Movie.MOVIE_ID,
                        currentMovie.getString(Constants.Api.ID_KEY));

                item.put(Constants.Movie.MOVIE_TITLE,
                        currentMovie.getString(Constants.Api.ORIGINAL_TITLE_KEY));

                item.put(Constants.Movie.MOVIE_POSTER,
                        currentMovie.getString(Constants.Api.POSTER_PATH_KEY));

                item.put(Constants.Movie.MOVIE_RATING,
                        currentMovie.getString(Constants.Api.VOTE_AVERAGE_KEY));

                item.put(Constants.Movie.MOVIE_TOTAL_VOTES,
                        currentMovie.getString(Constants.Api.TOTAL_VOTES_KEY));

                item.put(Constants.Movie.MOVIE_RELEASE_DATE,
                        Utility.releaseDateFormatter(currentMovie.getString(Constants.Api.RELEASE_DATE_KEY)));

                item.put(Constants.Movie.MOVIE_OVERVIEW,
                        currentMovie.getString(Constants.Api.OVERVIEW_KEY));

                kvPair.add(item);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return kvPair;
    }

}
