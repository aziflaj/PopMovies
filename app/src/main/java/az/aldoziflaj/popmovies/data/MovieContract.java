package az.aldoziflaj.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * TODO: prepare to store other data: trailers, favorite movies, etc.
 * TODO: Store movie id and popularity
 * TODO: Create a bulkUpdate method. It should update (insert if missing) the data in the DB based on a ContentValues[]
 */
public class MovieContract {

    // Content Authority => The name of the Content Provider
    public static final String CONTENT_AUTHORITY = "az.aldoziflaj.popmovies";

    // Base content URI to access the data from the Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the table pointed by MovieTable
    // content://az.aldoziflaj.popmovies/movies
    public static final String PATH_MOVIE = "movies";

    public static final class MovieTable implements BaseColumns {

        // Content URI for the MovieTable
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // Constant strings to tell the difference between a list of items (CONTENT_TYPE)
        // and a singe item (CONTENT_ITEM_TYPE)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        // columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_DESCRIPTION = "desc";
        public static final String COLUMN_IMAGE_URL = "image_url";


        /**
         * This method creates a URI for addressing a movie according to its poster URL
         *
         * @param posterUrl The stringly-typed URL fetched from the cloud service
         * @return The URI with the given {@code posterUrl} appended
         */
        public static Uri buildMovieWithPoster(String posterUrl) {
            return CONTENT_URI.buildUpon()
                    .appendPath(posterUrl.substring(1)) //remove the heading slash
                    .build();
        }

        /**
         * Build a Uri for a record of the table, using the ID
         *
         * @param id The ID of the record
         * @return A new Uri with the given ID appended to the end of the path
         */
        public static Uri buildMovieWithId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        /**
         * This method does the opposite of {@code buildMovieWithPoster}, hence returns the
         * stringly-typed URL
         *
         * @param uri The URI of the movie
         * @return The poster URL fetched from the URI
         */
        public static String getPosterUrlFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        /**
         * Parse the ID of a record, or return -1 instead
         *
         * @param uri The Uri of the record
         * @return The Id of the record or -1 if this doesn't apply
         */
        public static long getIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }
    }
}
