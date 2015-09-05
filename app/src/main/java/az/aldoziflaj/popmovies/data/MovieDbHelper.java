package az.aldoziflaj.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    //increment this if the DB changes
    static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createMoviesTable = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " ( "
                + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_RUNTIME + " INTEGER, "
                + " UNIQUE (" + MovieContract.MovieEntry.COLUMN_TITLE + ") ON CONFLICT REPLACE);";

        final String createTrailersTable = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " ( "
                + MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.TrailerEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

        final String createReviewsTable = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " ( "
                + MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, "
                + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + "));";

        db.execSQL(createMoviesTable);
        db.execSQL(createTrailersTable);
        db.execSQL(createReviewsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        onCreate(db);
    }
}
