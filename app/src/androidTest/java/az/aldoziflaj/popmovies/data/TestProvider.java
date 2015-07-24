package az.aldoziflaj.popmovies.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import az.aldoziflaj.popmovies.TestUtils;

/**
 * Test the MovieProvider
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
    public void testInsertionAndQuerying() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        UriMatcher matcher = MovieProvider.createUriMatcher();

        //insert into the table using the content provider
        ContentValues insertedValues = TestUtils.createStubMovie();
        Uri insertedUri = mContext.getContentResolver().insert(MovieContract.MovieTable.CONTENT_URI, insertedValues);

        long insertedId = MovieContract.MovieTable.getIdFromUri(insertedUri);
        assertTrue("Values not inserted in the table", insertedId > 0);

        assertEquals("insertedUri should have an ID",
                MovieProvider.MOVIE_WITH_ID, matcher.match(insertedUri));

        //read the same data
        Cursor cursor = mContext.getContentResolver().query(
                insertedUri, null, null, null, null);

        assertTrue("No data returned by the query", cursor.moveToFirst());

        TestUtils.validateInsertedData("Data inserted and data read are not the same",
                cursor, insertedValues);

        cursor.close();
        db.close();
    }
}
