package az.aldoziflaj.popmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;
import java.util.Set;

import az.aldoziflaj.popmovies.TestUtils;

/**
 * Test the database created by MovieDbHelper class.
 * Test CRUD (Create, Read, Update, Delete)
 */
public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void dropDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    public void setUp() throws Exception {
        dropDatabase();
    }

    /**
     * Test if the database is created correctly and the table(s) have the necessary columns
     */
    public void testDbCreation() {
        Set<String> tableNames = new HashSet<>();
        tableNames.add(MovieContract.MovieTable.TABLE_NAME); //the table to check if created

        dropDatabase();
        SQLiteDatabase db = new MovieDbHelper(mContext).getReadableDatabase();
        assertTrue("Database not opened at all!", db.isOpen());

        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);
        assertTrue("Database not created correctly", cursor.moveToFirst());

        do {
            tableNames.remove(cursor.getString(0));
        } while (cursor.moveToNext());

        //all removed from tableNames
        assertTrue("Some tables not created!", tableNames.isEmpty());


        /******************************************************************/
        /**           CHECK IF TABLES HAVE THE CORRECT COLUMNS           **/
        /******************************************************************/
        cursor = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieTable.TABLE_NAME + ")", null);

        assertTrue("Error: Unable to query the database for table information.",
                cursor.moveToFirst());

        Set<String> movieTableCols = new HashSet<>();
        movieTableCols.add(MovieContract.MovieTable.COLUMN_TITLE);
        movieTableCols.add(MovieContract.MovieTable.COLUMN_RELEASE_DATE);
        movieTableCols.add(MovieContract.MovieTable.COLUMN_VOTE_AVERAGE);
        movieTableCols.add(MovieContract.MovieTable.COLUMN_VOTE_COUNT);
        movieTableCols.add(MovieContract.MovieTable.COLUMN_DESCRIPTION);
        movieTableCols.add(MovieContract.MovieTable.COLUMN_IMAGE_URL);

        final int COL_NAME_INDEX = cursor.getColumnIndex("name");
        do {
            String colName = cursor.getString(COL_NAME_INDEX);
            movieTableCols.remove(colName);
        } while (cursor.moveToNext());

        assertTrue("Some columns not created on " + MovieContract.MovieTable.TABLE_NAME + " table",
                movieTableCols.isEmpty());

        db.close();
    }

    /**
     * Test if the inserted data corresponds with the same data read from the table
     */
    public void testInsertion() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        //insert into the table
        ContentValues insertedValues = TestUtils.createStubMovie();
        long insertedId = db.insert(MovieContract.MovieTable.TABLE_NAME, null, insertedValues);
        assertTrue("Values not inserted in the table", insertedId != -1);

        //read the same data
        Cursor cursor = db.query(
                MovieContract.MovieTable.TABLE_NAME,
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
