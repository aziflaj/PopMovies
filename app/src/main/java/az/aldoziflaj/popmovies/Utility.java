package az.aldoziflaj.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import az.aldoziflaj.popmovies.api.models.AllComments;
import az.aldoziflaj.popmovies.api.models.AllMovies;
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
     * Get the default sort order as shared preference.
     *
     * @param context The application context
     * @return The default sort order
     */
    public static String getPreferredSortOrder(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(
                context.getString(R.string.prefs_sort_key),
                context.getString(R.string.prefs_sort_default_value));
    }

    /**
     * Store a list of {@code MovieModel} instances into the database
     *
     * @param context   The Application Context
     * @param movieList The {@code MovieModel} list fetched from the Cloud Service
     */
    public static void storeMovieList(Context context, List<AllMovies.MovieModel> movieList) {
        ArrayList<ContentValues> cvList = new ArrayList<>();
        int movieListLength = movieList.size();
        Log.d(LOG, movieListLength + " items fetched");

        for (int i = 0; i < movieListLength; i++) {
            AllMovies.MovieModel movie = movieList.get(i);
            ContentValues cValues = new ContentValues();

            //get the movie data from the JSON response
            //get the title
            String title = movie.getTitle();
            cValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);

            int movieId = movie.getMovieId();
            cValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);

            //get the poster url
            String posterPath = movie.getPosterPath();
            cValues.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, posterPath);

            //get the rating
            double voteAverage = movie.getRating();
            cValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, voteAverage);

            //get the total number of votes
            int totalVotes = movie.getVoteCount();
            cValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, totalVotes);

            //get the movie release date
            String releaseDate = Utility.releaseDateFormatter(movie.getReleaseDate());
            cValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

            //get the description of the movie
            String description = movie.getDescription();
            cValues.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, description);

            double popularity = movie.getPopularity();
            cValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);

            cvList.add(cValues);
        }

        //insert into the DB
        ContentValues[] values = new ContentValues[cvList.size()];
        cvList.toArray(values);
        int itemsAdded = context.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, values);

        if (itemsAdded != movieListLength) {
            Log.d(LOG, itemsAdded + "/" + movieListLength + " movies inserted");
        } else {
            Log.d(LOG, itemsAdded + " records added into the DB");
        }
    }

    /**
     * @param releaseDate
     * @return
     */
    public static String getReleaseYear(String releaseDate) {
        String[] explodedDate = releaseDate.split("/");
        return explodedDate[2];
    }

    /**
     * TODO: JavaDoc
     *
     * @param context
     * @param movieId
     * @param runtime
     * @return
     */
    public static int updateMovieWithRuntime(Context context, int movieId, int runtime) {
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_RUNTIME, runtime);

        return context.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                values,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{Integer.toString(movieId)}
        );
    }

    public static void storeCommentList(Context context, int movieId, List<AllComments.Comment> commentList) {
        ArrayList<ContentValues> cvList = new ArrayList<>();
        int commentListLength = commentList.size();
        Log.d(LOG, commentListLength + " comments for movie with id " + movieId);

        for (int i = 0; i < commentListLength; i++) {
            AllComments.Comment c = commentList.get(i);
            ContentValues cv = new ContentValues();

            cv.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, c.getId());
            cv.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, c.getAuthor());
            cv.put(MovieContract.ReviewEntry.COLUMN_CONTENT, c.getContent());
            cv.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);

            cvList.add(cv);
        }

        ContentValues[] values = new ContentValues[cvList.size()];
        cvList.toArray(values);

        int commentsAdded = context.getContentResolver().bulkInsert(
                MovieContract.ReviewEntry.CONTENT_URI,
                values);

        if (commentsAdded != commentListLength) {
            Log.d(LOG, String.format("%d/%d comments inserted", commentsAdded, commentListLength));
        } else {
            Log.d(LOG, commentsAdded + " comments added");
        }
    }
}
