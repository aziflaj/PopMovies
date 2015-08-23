package az.aldoziflaj.popmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Set;

import az.aldoziflaj.popmovies.TestUtils;

import static az.aldoziflaj.popmovies.data.MovieContract.MovieEntry;
import static az.aldoziflaj.popmovies.data.MovieContract.ReviewEntry;
import static az.aldoziflaj.popmovies.data.MovieContract.TrailerEntry;

/**
 * Test the database created by {@code MovieDbHelper} class.
 * Test CRUD (Create, Read, Update, Delete)
 */
public class TestDb extends AndroidTestCase {

    private MovieDbHelper helper;

    void dropDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    public void setUp() throws Exception {
        dropDatabase();
        helper = new MovieDbHelper(mContext);
    }

    /**
     * Test if the database is created correctly and the table(s) have the necessary columns
     */
    public void testAllTablesCreated() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Set<String> tableNames = new HashSet<>();
        tableNames.add(MovieEntry.TABLE_NAME); //the table to check if created
        tableNames.add(ReviewEntry.TABLE_NAME); //the table to check if created
        tableNames.add(TrailerEntry.TABLE_NAME); //the table to check if created

        dropDatabase();

        assertTrue("Database not opened at all!", db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
        assertTrue("Database not created correctly", cursor.moveToFirst());

        do {
            tableNames.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        //all removed from tableNames
        assertTrue("Some tables not created!", tableNames.isEmpty());

        cursor.close();
        db.close();
    }

    public void testMovieTableColumns() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.", cursor.moveToFirst());

        Set<String> movieTableCols = new HashSet<>();
        movieTableCols.add(MovieEntry.COLUMN_TITLE);
        movieTableCols.add(MovieEntry.COLUMN_RELEASE_DATE);
        movieTableCols.add(MovieEntry.COLUMN_VOTE_AVERAGE);
        movieTableCols.add(MovieEntry.COLUMN_VOTE_COUNT);
        movieTableCols.add(MovieEntry.COLUMN_DESCRIPTION);
        movieTableCols.add(MovieEntry.COLUMN_IMAGE_URL);
        movieTableCols.add(MovieEntry.COLUMN_POPULARITY);
        movieTableCols.add(MovieEntry.COLUMN_RUNTIME);

        final int COL_NAME_INDEX = cursor.getColumnIndex("name");
        do {
            String colName = cursor.getString(COL_NAME_INDEX);
            movieTableCols.remove(colName);
        } while (cursor.moveToNext());

        assertTrue("Some columns not created on " + MovieEntry.TABLE_NAME + " table", movieTableCols.isEmpty());
        cursor.close();
    }

    public void testReviewTableColumns() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + ReviewEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.", cursor.moveToFirst());

        Set<String> reviewTableCols = new HashSet<>();
        reviewTableCols.add(ReviewEntry._ID);
        reviewTableCols.add(ReviewEntry.COLUMN_AUTHOR);
        reviewTableCols.add(ReviewEntry.COLUMN_CONTENT);
        reviewTableCols.add(ReviewEntry.COLUMN_MOVIE_ID);
        reviewTableCols.add(ReviewEntry.COLUMN_REVIEW_ID);

        final int COL_NAME_INDEX = cursor.getColumnIndex("name");
        do {
            String colName = cursor.getString(COL_NAME_INDEX);
            reviewTableCols.remove(colName);
        } while (cursor.moveToNext());

        assertTrue("Some columns not created on " + ReviewEntry.TABLE_NAME + " table", reviewTableCols.isEmpty());
        cursor.close();
    }

    public void testTrailerTableColumns() {
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + TrailerEntry.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.", cursor.moveToFirst());

        Set<String> trailerTableCols = new HashSet<>();
        trailerTableCols.add(TrailerEntry._ID);
        trailerTableCols.add(TrailerEntry.COLUMN_TITLE);
        trailerTableCols.add(TrailerEntry.COLUMN_YOUTUBE_KEY);
        trailerTableCols.add(TrailerEntry.COLUMN_MOVIE_ID);
        trailerTableCols.add(TrailerEntry.COLUMN_TRAILER_ID);

        final int COL_NAME_INDEX = cursor.getColumnIndex("name");
        do {
            String colName = cursor.getString(COL_NAME_INDEX);
            trailerTableCols.remove(colName);
        } while (cursor.moveToNext());

        assertTrue("Some columns not created on " + TrailerEntry.TABLE_NAME + " table", trailerTableCols.isEmpty());
        cursor.close();
    }

    /**
     * Test if the inserted data corresponds with the same data read from the table
     */
    public void testInsertion() {
        SQLiteDatabase db = helper.getWritableDatabase();

        //insert into the table
        ContentValues insertedValues = TestUtils.createStubMovie();
        long insertedId = db.insert(MovieEntry.TABLE_NAME, null, insertedValues);
        assertTrue("Values not inserted in the table", insertedId != -1);

        //read the same data
        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,
                null, // columns to query
                null, // columns to test (where)
                null, // values to test
                null, // group by
                null, // columns to filter by row group
                null // sort by rule
        );

        assertTrue("No data returned by the query", cursor.moveToFirst());

        TestUtils.validateInsertedData("Data inserted and data read are not the same",
                cursor, insertedValues);

        cursor.close();
        db.close();
    }
}
