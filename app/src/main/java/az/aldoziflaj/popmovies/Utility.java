package az.aldoziflaj.popmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A list of utility methods used through the application
 */
public class Utility {

    public static final String LOG = "Log";

    /**
     * The method formats a date string from yyyy-MM-dd to dd/MM/yyyy
     * @param unformattedDate Unformated date from the cloud service
     * @return The formatted date
     */
    public static String releaseDateFormatter(String unformattedDate) {
        StringBuilder sb = new StringBuilder();
        String[] explodedDate = unformattedDate.split("-");

        sb.append(explodedDate[2])                      //day of month
                .append("/").append(explodedDate[1])    //month
                .append("/").append(explodedDate[0]);   //year

        return sb.toString();
    }

    /**
     * Th method fetches the list of poster URLs from the JSON response from the cloud
     * @param moviesJsonString The string-encoded JSON response
     * @return A String array of poster URLs
     */
    public static String[] fetchPosterListFromJson(String moviesJsonString) {
        ArrayList<String> posterList = new ArrayList<>();
        try {
            JSONArray jsonMovieList = (new JSONObject(moviesJsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG, movieListLength + " items fetched");

            for (int i = 0; i < movieListLength; i++) {
                JSONObject currentMovie = jsonMovieList.getJSONObject(i);
                posterList.add(currentMovie.getString(Constants.Api.POSTER_PATH_KEY));
            }

        } catch (JSONException e) {
            Log.e(LOG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        String[] result = new String[posterList.size()];
        posterList.toArray(result);

        return result;
    }
}
