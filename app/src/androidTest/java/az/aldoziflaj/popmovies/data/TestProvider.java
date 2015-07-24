package az.aldoziflaj.popmovies.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import az.aldoziflaj.popmovies.TestUtils;

/**
 * TODO javadoc
 */
public class TestProvider extends AndroidTestCase {

    private void deleteAllRecords() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        db.delete(MovieContract.MovieTable.TABLE_NAME, null, null);

        Cursor cursor = db.query(MovieContract.MovieTable.TABLE_NAME,
                null, null, null, null, null, null);

        assertEquals("Some records not deleted in " + MovieContract.MovieTable.TABLE_NAME, cursor.getCount(), 0);
    }


    @Override
    public void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    /**
     * This method tests the {@code UriMatcher} inside the {@code MovieProvider} class
     */
    public void testUriMatcher() {
        final String testPoster = "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg";

        UriMatcher matcher = MovieProvider.createUriMatcher();

        assertEquals("CONTENT_URI of MovieTable should be a Dir",
                matcher.match(MovieContract.MovieTable.CONTENT_URI),
                MovieProvider.MOVIE);

        assertEquals("CONTENT_URI of MovieTable should be a Dir",
                matcher.match(MovieContract.MovieTable.buildMovieWithPoster(testPoster)),
                MovieProvider.MOVIE_WITH_POSTER);
    }

    /**
     * This method tests the {@code query()} method of the Content Provider
     */
    public void testQuerying() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        //insert into the table
        ContentValues insertedValues = TestUtils.createStubMovie();
        long insertedId = db.insert(MovieContract.MovieTable.TABLE_NAME, null, insertedValues);
        assertTrue("Values not inserted in the table", insertedId != -1);

        //read the same data
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieTable.CONTENT_URI,
                null, null, null, null);

        assertTrue("No data returned by the query", cursor.moveToFirst());

        TestUtils.validateInsertedData("Data inserted and data read are not the same",
                cursor, insertedValues);

        cursor.close();
        db.close();
    }
}
