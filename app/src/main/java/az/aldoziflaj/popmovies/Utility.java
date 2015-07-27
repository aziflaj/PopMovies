package az.aldoziflaj.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import az.aldoziflaj.popmovies.data.MovieContract;

/**
 * A list of utility methods used through the application
 */
public class Utility {
    /*
    TODO: merge fetchMovieListFromJSON(String jsonString) and insertMoviesIntoDatabase(Context context, String moviesJsonString)
     */

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

    /**
     * Fetch a movie {@code ArrayList} from a JSON-encoded String and store it in the database
     *
     * @param context          The Application Context
     * @param moviesJsonString The JSON-encoded string response from the Cloud service
     */
    public static void insertMoviesIntoDatabase(Context context, String moviesJsonString) {
        ArrayList<ContentValues> cvList = new ArrayList<>();

        try {
            JSONArray jsonMovieList = (new JSONObject(moviesJsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG, movieListLength + " items fetched");

            for (int i = 0; i < movieListLength; i++) {
                JSONObject currentMovie = jsonMovieList.getJSONObject(i);
                ContentValues item = new ContentValues();

                //get the movie data from the JSON response
                item.put(MovieContract.MovieTable.COLUMN_TITLE,
                        currentMovie.getString(Constants.Api.ORIGINAL_TITLE_KEY));

                item.put(MovieContract.MovieTable.COLUMN_IMAGE_URL,
                        currentMovie.getString(Constants.Api.POSTER_PATH_KEY));

                item.put(MovieContract.MovieTable.COLUMN_VOTE_AVERAGE,
                        currentMovie.getString(Constants.Api.VOTE_AVERAGE_KEY));

                item.put(MovieContract.MovieTable.COLUMN_VOTE_COUNT,
                        currentMovie.getString(Constants.Api.TOTAL_VOTES_KEY));

                item.put(MovieContract.MovieTable.COLUMN_RELEASE_DATE,
                        Utility.releaseDateFormatter(currentMovie.getString(Constants.Api.RELEASE_DATE_KEY)));

                item.put(MovieContract.MovieTable.COLUMN_DESCRIPTION,
                        currentMovie.getString(Constants.Api.OVERVIEW_KEY));

                cvList.add(item);
            }

            ContentValues[] values = new ContentValues[cvList.size()];
            cvList.toArray(values);
            int itemsAdded = context.getContentResolver().bulkInsert(MovieContract.MovieTable.CONTENT_URI, values);

            if (itemsAdded != movieListLength) {
                Log.d(LOG, itemsAdded + " of " + movieListLength + " inserted");
            } else {
                Log.d(LOG, itemsAdded + " records added into the DB");
            }

        } catch (JSONException e) {
            Log.e(LOG, "Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
