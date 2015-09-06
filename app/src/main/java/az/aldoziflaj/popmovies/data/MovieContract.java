package az.aldoziflaj.popmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    // Content Authority => The name of the Content Provider
    public static final String CONTENT_AUTHORITY = "az.aldoziflaj.popmovies";

    // Base content URI to access the data from the Content Provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Path to the table pointed by MovieEntry
    // content://az.aldoziflaj.popmovies/movies
    public static final String PATH_MOVIE = "movies";
    public static final String PATH_TRAILER = "trailers";
    public static final String PATH_REVIEW = "reviews";

    public static final class MovieEntry implements BaseColumns {

        // Content URI for the MovieEntry
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        // Constant strings to tell the difference between a list of items (CONTENT_TYPE)
        // and a singe item (CONTENT_ITEM_TYPE)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movies";

        // columns
        public static final String COLUMN_MOVIE_ID = "movie_id"; // the movie id from the backend
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_DESCRIPTION = "desc";
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_FAVORITE = "favorite"; // pseudo-boolean for favorite movie


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

    public static final class TrailerEntry implements BaseColumns {
        // Content URI for the TrailerEntry
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        // Constant strings to tell the difference between a list of items (CONTENT_TYPE)
        // and a singe item (CONTENT_ITEM_TYPE)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailers";

        // columns
        public static final String COLUMN_TITLE = "title"; //trailer title
        public static final String COLUMN_YOUTUBE_KEY = "youtube_key";
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_MOVIE_ID = "movie_id"; // the movie id from the backend (used for joins)

        /**
         * Get the movie ID in the URI (the ID from the Backend)
         *
         * @param uri The trailer's URI with the movie ID
         * @return The movie ID or -1 if doesn't exist
         */
        public static long getMovieIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        /**
         * Creates a trailer uri with the movie id (from the backend) appended
         *
         * @param movieId The movie ID
         * @return the URI of the trailer
         */
        public static Uri buildTrailerWithId(long movieId) {
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }
    }

    public static final class ReviewEntry implements BaseColumns {
        // Content URI for the ReviewEntry
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        // Constant strings to tell the difference between a list of items (CONTENT_TYPE)
        // and a singe item (CONTENT_ITEM_TYPE)
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "reviews";

        // columns
        public static final String COLUMN_AUTHOR = "author"; //trailer title
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_MOVIE_ID = "movie_id"; // the movie id from the backend (used for joins)

        /**
         * Get the movie ID in the URI (the ID from the Backend)
         *
         * @param uri The Uri of the review with the movie id appended
         * @return The ID of the movie, or -1 if doesn't exist
         */
        public static long getMovieIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }

        /**
         * Creates a trailer uri with the movie id (from the backend) appended
         *
         * @param insertedId The ID of the movie
         * @return The uri of the review
         */
        public static Uri buildTrailerWithId(long insertedId) {
            return ContentUris.withAppendedId(CONTENT_URI, insertedId);
        }
    }
}
