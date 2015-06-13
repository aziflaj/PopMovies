package az.aldoziflaj.popmovies.apiclient;

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

/**
 * Created by aziflaj on 6/13/15.
 */
public class FetchMoviesTask extends AsyncTask<Void, Void, Void> {
    public final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    Context appContext;
    Toast errorInConnection;

    public FetchMoviesTask(Context context) {
        this.appContext = context;
    }

    @Override
    protected void onPreExecute() {
        errorInConnection = Toast.makeText(appContext,
                "Can't connect to the server",
                Toast.LENGTH_LONG);
    }

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        String moviesJSONString;
        BufferedReader reader = null;

        try {
            Uri moviesUri = Uri.parse(ApiHelper.API_BASE_URL).buildUpon()
                    .appendQueryParameter(ApiHelper.SORT_KEY, ApiHelper.SORT_BY_POPULARITY)
                    .appendQueryParameter(ApiHelper.API_KEY_QUERY, ApiHelper.API_KEY)
                    .build();
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
                Log.d(LOG_TAG, "length of message 0");
                return null;
            }

            moviesJSONString = sb.toString();
            fetchMovieListFromJSON(moviesJSONString);
            //Log.d(LOG_TAG, moviesJSONString);

        } catch (MalformedURLException e) {
            errorInConnection.show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            return null;

        } catch (IOException e) {
            errorInConnection.show();
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            return null;

        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error: "+ e.getMessage());

                }
            }
        }

        return null;
    }

    private String[] fetchMovieListFromJSON(String jsonString) {
        ArrayList<String> movieList = new ArrayList<>();

        try {
            JSONObject jsonResponse = new JSONObject(jsonString);
            JSONArray jsonMovieList = jsonResponse.getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG_TAG, movieListLength + " items fetched");

            for (int i=0; i<movieListLength; i++) {
                JSONObject currentMovie = jsonMovieList.getJSONObject(i);
                movieList.add(currentMovie.getString(ApiHelper.ORIGINAL_TITLE_KEY));
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        if (!movieList.isEmpty()) {
            Log.d(LOG_TAG, "movieList is not empty");
            for (String item : movieList) {
                Log.d(LOG_TAG, item);
            }
        } else {
            Log.d(LOG_TAG, "movieList is empty");
        }

        //return (String[]) movieList.toArray();
        return null;
    }
}