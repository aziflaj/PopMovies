package az.aldoziflaj.popmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    //increment this if the DB changes
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createMoviesTable = "CREATE TABLE " + MovieContract.MovieTable.TABLE_NAME + " ( "
                + MovieContract.MovieTable._ID + " INTEGER PRIMARY KEY, "
                + MovieContract.MovieTable.COLUMN_TITLE + " STRING UNIQUE NOT NULL, "
                + MovieContract.MovieTable.COLUMN_RELEASE_DATE + " STRING NOT NULL, "
                + MovieContract.MovieTable.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, "
                + MovieContract.MovieTable.COLUMN_VOTE_COUNT + " INTEGER NOT NULL, "
                + MovieContract.MovieTable.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + MovieContract.MovieTable.COLUMN_IMAGE_URL + " STRING NOT NULL "
                + ");";

        db.execSQL(createMoviesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + MovieContract.MovieTable.TABLE_NAME);
        onCreate(db);
    }
}
