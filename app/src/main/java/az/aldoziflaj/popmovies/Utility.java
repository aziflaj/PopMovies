package az.aldoziflaj.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    public static ArrayList<Movie> fetchMovieListFromJSON(Context context, String jsonString) {
        ArrayList<Movie> movieList = new ArrayList<>();
        ArrayList<ContentValues> cvList = new ArrayList<>();

        try {
            JSONArray jsonMovieList = (new JSONObject(jsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG, movieListLength + " items fetched");

            for (int i = 0; i < movieListLength; i++) {
                JSONObject currentJsonMovie = jsonMovieList.getJSONObject(i);
                ContentValues cValues = new ContentValues();
                Gson gson = new Gson();
                Movie singleMovie = gson.fromJson(currentJsonMovie.toString(), Movie.class);

                //get the movie data from the JSON response
                //get the title
                String title = currentJsonMovie.getString(Constants.Api.ORIGINAL_TITLE_KEY);
                cValues.put(MovieContract.MovieTable.COLUMN_TITLE, title);

                //get the poster url
                String posterPath = currentJsonMovie.getString(Constants.Api.POSTER_PATH_KEY);
                cValues.put(MovieContract.MovieTable.COLUMN_IMAGE_URL, posterPath);

                //get the rating
                double voteAverage = currentJsonMovie.getDouble(Constants.Api.VOTE_AVERAGE_KEY);
                cValues.put(MovieContract.MovieTable.COLUMN_VOTE_AVERAGE, voteAverage);

                //get the total number of votes
                int totalVotes = currentJsonMovie.getInt(Constants.Api.TOTAL_VOTES_KEY);
                cValues.put(MovieContract.MovieTable.COLUMN_VOTE_COUNT, totalVotes);

                //get the movie release date
                String releaseDate = Utility.releaseDateFormatter(currentJsonMovie.getString(Constants.Api.RELEASE_DATE_KEY));
                cValues.put(MovieContract.MovieTable.COLUMN_RELEASE_DATE, releaseDate);

                //get the description of the movie
                String description = currentJsonMovie.getString(Constants.Api.OVERVIEW_KEY);
                cValues.put(MovieContract.MovieTable.COLUMN_DESCRIPTION, description);

                movieList.add(singleMovie);
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

        return movieList;
    }
}
