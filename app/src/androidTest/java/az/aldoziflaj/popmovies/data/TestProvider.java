package az.aldoziflaj.popmovies.data;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

import az.aldoziflaj.popmovies.TestUtils;

/**
 * Test the MovieProvider
 * TODO: remove Suppress
 */
@Suppress
public class TestProvider extends AndroidTestCase {

    private void deleteAllRecords() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);

        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null, null, null, null, null, null);

        assertEquals("Some records not deleted in " + MovieContract.MovieEntry.TABLE_NAME, cursor.getCount(), 0);

        cursor.close();
        db.close();
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

        assertEquals("CONTENT_URI of MovieEntry should be a Dir",
                matcher.match(MovieContract.MovieEntry.CONTENT_URI),
                MovieProvider.MOVIE);

        assertEquals("CONTENT_URI of MovieEntry should be a Dir",
                matcher.match(MovieContract.MovieEntry.buildMovieWithPoster(testPoster)),
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
        Uri insertedUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, insertedValues);

        long insertedId = MovieContract.MovieEntry.getIdFromUri(insertedUri);
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

    /**
     * This method tests the {@code update()} method of the content provider
     */
    public void testUpdate() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();
        boolean votesNotNull;

        //insert a stub movie into SQLite
        ContentValues values = TestUtils.createStubMovie();
        mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

        //change the value
        Integer votes = values.getAsInteger(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);
        if (votes != null) {
            votesNotNull = true;
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, votes + 10);
        } else {
            votesNotNull = false;
            values.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, TestUtils.MOVIE_VOTE_COUNT); //put a dummy
        }

        int updatedRows = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                values,
                MovieContract.MovieEntry.COLUMN_TITLE + " = ? ",
                new String[]{TestUtils.MOVIE_TITLE}
        );

        assertEquals(updatedRows + " row(s) updated instead of only 1", 1, updatedRows);

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // get all columns
                MovieContract.MovieEntry.COLUMN_TITLE + " = ? ",
                new String[]{TestUtils.MOVIE_TITLE},
                null);

        assertTrue("No data queried", cursor.moveToFirst());

        int votesColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT);

        if (votesNotNull) {
            assertEquals("Updated rows are not actually updated",
                    TestUtils.MOVIE_VOTE_COUNT + 10, cursor.getInt(votesColumnIndex));
        } else {
            assertEquals("Updated rows are not actually updated",
                    TestUtils.MOVIE_VOTE_COUNT, cursor.getInt(votesColumnIndex));
        }

        cursor.close();
        db.close();
    }

    /**
     * Delete ALL the records
     * <a href="http://cdn.meme.am/instances/500x/53654088.jpg"></a>
     */
    public void testDelete() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        //insert into the table using the content provider
        ContentValues[] list = TestUtils.createStubMovieList();
        if (list == null) {
            fail("No items to insert into the DB");
        }

        int massInsertCount = getContext().getContentResolver().bulkInsert(
                MovieContract.MovieEntry.CONTENT_URI, list);

        assertTrue("Some of the records not inserted", massInsertCount == list.length);

        //delete a single item
        int deleteOneRow = mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_TITLE + " = ?",
                new String[]{list[0].getAsString(MovieContract.MovieEntry.COLUMN_TITLE)}
        );

        assertEquals("Didn't delete a single row", 1, deleteOneRow);

        // DELETE ALL THE RECORDS!
        int deleted = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);

        assertEquals("Not all records deleted", list.length, deleted + deleteOneRow);

        db.close();
    }

    /**
     * This method tests the {@code bulkInsert()} method of the Content Provider
     */
    public void testMassInsertion() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues[] list = TestUtils.createStubMovieList();

        if (list == null) {
            fail("No item to mass insert");
        }

        int massInsertCount = getContext().getContentResolver().bulkInsert(
                MovieContract.MovieEntry.CONTENT_URI, list);

        assertTrue("Some of the records not inserted", massInsertCount == list.length);

        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, null, null, null);

        assertTrue("Couldn't fetch anything from the DB", cursor.moveToFirst());
        assertTrue("Couldn't fetch all records from the DB", cursor.getCount() == list.length);

        int index = 0;
        for (ContentValues item : list) {
            TestUtils.validateInsertedData("Item " + index + " has errors", cursor, item);

            index++;
            if (index < list.length) {
                cursor.moveToNext();
            }
        }

        cursor.close();
        db.close();
    }
}
