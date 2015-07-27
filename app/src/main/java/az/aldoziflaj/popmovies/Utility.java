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
     * pairs of data from the JSON response of the cloud service of TMDB and stores them into the
     * database
     *
     * @param context    The Application Context
     * @param jsonString The string-encoded JSON response
     * @return The {@code ArrayList<HashMap<String, String>>} with key-value pairs
     */
    public static ArrayList<HashMap<String, String>> fetchMovieListFromJSON(Context context, String jsonString) {
        ArrayList<HashMap<String, String>> kvPair = new ArrayList<>();
        ArrayList<ContentValues> cvList = new ArrayList<>();

        try {
            JSONArray jsonMovieList = (new JSONObject(jsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG, movieListLength + " items fetched");

            for (int i = 0; i < movieListLength; i++) {
                JSONObject currentMovie = jsonMovieList.getJSONObject(i);
                HashMap<String, String> kvItem = new HashMap<>();
                ContentValues cValues = new ContentValues();

                //get the movie data from the JSON response
                //get the title
                String title = currentMovie.getString(Constants.Api.ORIGINAL_TITLE_KEY);
                kvItem.put(Constants.Movie.MOVIE_TITLE, title);
                cValues.put(MovieContract.MovieTable.COLUMN_TITLE, title);

                //get the poster url
                String posterPath = currentMovie.getString(Constants.Api.POSTER_PATH_KEY);
                kvItem.put(Constants.Movie.MOVIE_POSTER, posterPath);
                cValues.put(MovieContract.MovieTable.COLUMN_IMAGE_URL, posterPath);

                //get the rating
                double voteAverage = currentMovie.getDouble(Constants.Api.VOTE_AVERAGE_KEY);
                kvItem.put(Constants.Movie.MOVIE_RATING, Double.toString(voteAverage));
                cValues.put(MovieContract.MovieTable.COLUMN_VOTE_AVERAGE, voteAverage);

                //get the total number of votes
                int totalVotes = currentMovie.getInt(Constants.Api.TOTAL_VOTES_KEY);
                kvItem.put(Constants.Movie.MOVIE_TOTAL_VOTES, Integer.toString(totalVotes));
                cValues.put(MovieContract.MovieTable.COLUMN_VOTE_COUNT, totalVotes);

                //get the movie release date
                String releaseDate = Utility.releaseDateFormatter(currentMovie.getString(Constants.Api.RELEASE_DATE_KEY));
                kvItem.put(Constants.Movie.MOVIE_RELEASE_DATE, releaseDate);
                cValues.put(MovieContract.MovieTable.COLUMN_RELEASE_DATE, releaseDate);

                //get the description of the movie
                String description = currentMovie.getString(Constants.Api.OVERVIEW_KEY);
                kvItem.put(Constants.Movie.MOVIE_OVERVIEW, description);
                cValues.put(MovieContract.MovieTable.COLUMN_DESCRIPTION, description);

                kvPair.add(kvItem);
                cvList.add(cValues);
            }

            //insert into the DB
            ContentValues[] values = new ContentValues[cvList.size()];
            cvList.toArray(values);
            int itemsAdded = context.getContentResolver().bulkInsert(MovieContract.MovieTable.CONTENT_URI, values);

            if (itemsAdded != movieListLength) {
                Log.d(LOG, itemsAdded + "/" + movieListLength + " movies inserted");
            } else {
                Log.d(LOG, itemsAdded + " records added into the DB");
            }

        } catch (JSONException e) {
            Log.e(LOG, "Error: " + e.getMessage());
            e.printStackTrace();
        }

        return kvPair;
    }
}
