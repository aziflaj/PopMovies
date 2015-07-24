package az.aldoziflaj.popmovies;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A list of utility methods used through the application
 */
public class Utility {

    public static final String LOG = "Log";

    /**
     * The method formats a date string from yyyy-MM-dd to dd/MM/yyyy
     *
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
     * The method fetches the list of poster URLs from the JSON response from the cloud
     *
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

    /**
     * This method fetches an {@code ArrayList<HashMap<String, String>>} with key-value
     * pairs of data from the JSON response of the cloud service of TMDB
     *
     * @param jsonString The string-encoded JSON response
     * @return The {@code ArrayList<HashMap<String, String>>} with key-value pairs
     */
    public static ArrayList<HashMap<String, String>> fetchMovieListFromJSON(String jsonString) {
        ArrayList<HashMap<String, String>> kvPair = new ArrayList<>();

        try {
            JSONArray jsonMovieList = (new JSONObject(jsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG, movieListLength + " items fetched");

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
            Log.e(LOG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return kvPair;
    }
}
