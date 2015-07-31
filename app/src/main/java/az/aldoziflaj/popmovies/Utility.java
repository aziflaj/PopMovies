package az.aldoziflaj.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

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
     * This method fetches data from the JSON response of the cloud service of TMDB and stores them
     * into the database
     *
     * @param context    The Application Context
     * @param jsonString The string-encoded JSON response
     */
    public static void storeJsonResponseMovies(Context context, String jsonString) {
        ArrayList<ContentValues> cvList = new ArrayList<>();

        try {
            JSONArray jsonMovieList = (new JSONObject(jsonString)).getJSONArray("results");
            int movieListLength = jsonMovieList.length();
            Log.d(LOG, movieListLength + " items fetched");

            for (int i = 0; i < movieListLength; i++) {
                JSONObject currentJsonMovie = jsonMovieList.getJSONObject(i);
                ContentValues cValues = new ContentValues();

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
    }

    /**
     * Get the default sort order as shared preference.
     *
     * @param context The application context
     * @return The default sort order
     */
    public static String getDefaultSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String sortOrder = prefs.getString(
                context.getString(R.string.movie_sort_key),
                context.getString(R.string.movie_sort_default));

        return sortOrder;
    }
}
