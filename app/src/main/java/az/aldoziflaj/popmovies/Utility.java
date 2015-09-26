package az.aldoziflaj.popmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import az.aldoziflaj.popmovies.api.models.AllComments;
import az.aldoziflaj.popmovies.api.models.AllMovies;
import az.aldoziflaj.popmovies.api.models.AllTrailers;
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
     * Fetches the release year of a movie from its release date
     *
     * @param releaseDate The stringly-typed release date
     * @return The release year
     */
    public static String getReleaseYear(String releaseDate) {
        String[] explodedDate = releaseDate.split("/");
        return explodedDate[2];
    }

    /**
     * Update the runtime of a given movie, after fetching the runtime from the cloud service
     *
     * @param context The application context
     * @param movieId The MOVIE_ID column of the movie to update with the runtime
     * @param runtime The runtime of the movie
     * @return The number of updated rows
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

    /**
     * Store a list of comments of a given movie into the DB
     *
     * @param context     The application context
     * @param movieId     The MOVIE_ID column of the movie owning the comments
     * @param commentList A {@code List<AllComments.Comment>} fetched from the cloud service
     */
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

    /**
     * Store a list of trailers belonging to a given movie
     *
     * @param context     The application context
     * @param movieId     The MOVIE_ID of the movie having the trailers
     * @param trailerList The list of {@code AllTrailers.MovieTrailer} that is being stored
     */
    public static void storeTrailerList(Context context, int movieId, List<AllTrailers.MovieTrailer> trailerList) {
        ArrayList<ContentValues> cvList = new ArrayList<>();
        int trailerListLength = trailerList.size();
        Log.d(LOG, trailerListLength + " comments for movie with id " + movieId);

        for (int i = 0; i < trailerListLength; i++) {
            AllTrailers.MovieTrailer trailer = trailerList.get(i);
            ContentValues cv = new ContentValues();

            cv.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            cv.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailer.getId());
            cv.put(MovieContract.TrailerEntry.COLUMN_TITLE, trailer.getTrailerTitle());
            cv.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, trailer.getKey());

            cvList.add(cv);
        }

        ContentValues[] values = new ContentValues[cvList.size()];
        cvList.toArray(values);

        int trailersAdded = context.getContentResolver().bulkInsert(
                MovieContract.TrailerEntry.CONTENT_URI,
                values);

        if (trailersAdded != trailerListLength) {
            Log.d(LOG, String.format("%d/%d trailers inserted", trailersAdded, trailerListLength));
        } else {
            Log.d(LOG, trailersAdded + " trailers added");
        }
    }


    /**
     * Checks if one day has passed since the last timestamp checked
     *
     * @param lastTimestamp The last timestamp checked
     * @return True if one day has passed, false otherwise
     */
    public static boolean isOneDayLater(long lastTimestamp) {
        // 1000 milliseconds/second *
        // 60 seconds/minute *
        // 60 minutes/hour *
        // 24 hours/day
        final long ONE_DAY = 1000 * 60 * 60 * 24;

        long now = System.currentTimeMillis();

        long timePassed = now - lastTimestamp;
        return (timePassed > ONE_DAY);
    }

    /**
     * Fetches the movie id from the database, as fetched from the cloud service
     *
     * @param context  The application context
     * @param movieUri The URI of the movie, pointing in the movie table
     * @return The movie id, or -1 if something goes wrong
     */
    public static int fetchMovieIdFromUri(Context context, Uri movieUri) {
        long _id = MovieContract.MovieEntry.getIdFromUri(movieUri);

        Cursor c = context.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_MOVIE_ID},
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(_id)},
                null);

        if (c.moveToFirst()) {
            int movieIdIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            return c.getInt(movieIdIndex);
        } else {
            return -1;
        }
    }
}
