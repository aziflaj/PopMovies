package az.aldoziflaj.popmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by aziflaj on 6/13/15.
 */
class FetchMoviesTask extends AsyncTask<Void, Void, Void> {
    public final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

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

            Log.d(LOG_TAG, moviesJSONString);

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error: " + e.getMessage());
            e.printStackTrace();
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
}