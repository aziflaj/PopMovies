package az.aldoziflaj.popmovies.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class MovieProvider extends ContentProvider {
    public static final int MOVIE = 100;
    public static final int MOVIE_WITH_POSTER = 101;
    private static final UriMatcher sUriMatcher = createUriMatcher();
    private SQLiteOpenHelper mOpenHelper;

    /**
     * Create the {@code UriMatcher} for pointing movies into the DB
     *
     * @return A {@code UriMatcher} instance
     */
    public static UriMatcher createUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_POSTER);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    /**
     * Queries the DB for the data requested by the caller of the method
     *
     * @param uri           The Uri of the data queried, containing information about where to search
     * @param projection    An array of required columns from the table ({@code null} for all columns)
     * @param selection     The where clause
     * @param selectionArgs The arguments of the where clause
     * @param sortOrder     The order of sorting the movies
     * @return A {@code Cursor} instance with the data
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE: {
                Cursor cursor = db.query(
                        MovieContract.MovieTable.TABLE_NAME,
                        projection, //columns
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                return cursor;
            }

            case MOVIE_WITH_POSTER: {
                String posterUrl = MovieContract.MovieTable.getPosterUrlFromUri(uri);

                Cursor cursor = db.query(
                        MovieContract.MovieTable.TABLE_NAME,
                        projection, //columns
                        MovieContract.MovieTable.COLUMN_IMAGE_URL + " = ?",
                        new String[]{posterUrl}, //todo fix
                        null,
                        null,
                        sortOrder);

                return cursor;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Returns the type of the item pointed by a given URI
     *
     * @param uri A given content URI pointing to data in the sqlite database
     * @return The type ({@code MovieContract.MovieTable.CONTENT_TYPE} or
     * {@code MovieContract.MovieTable.CONTENT_ITEM_TYPE}) of the pointed data
     */
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieTable.CONTENT_TYPE;

            case MOVIE_WITH_POSTER:
                return MovieContract.MovieTable.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
