package az.aldoziflaj.popmovies;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import az.aldoziflaj.popmovies.data.MovieContract;

/**
 * Utility methods used for testing
 */
public class TestUtils extends AndroidTestCase {
    public static final String MOVIE_TITLE = "Interstellar";
    public static final int MOVIE_VOTE_COUNT = 1234;
    private static final double MOVIE_POPULARITY = 12.355;
    private static final int MOVIE_RUNTIME = 139;

    /**
     * Create a stub movie to test insertion into the DB
     *
     * @return A {@code ContentValues} instance with the data for the movie
     */
    public static ContentValues createStubMovie() {
        ContentValues cv = new ContentValues();

        cv.put(MovieContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE);

        cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                Utility.releaseDateFormatter("2014-10-26"));

        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 8.8);
        cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, MOVIE_VOTE_COUNT);
        cv.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, "lorem ipsum dolor");
        cv.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, "/img_of_interstellar.jpg");
        cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, MOVIE_POPULARITY);
        cv.put(MovieContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME);

        return cv;
    }

    /**
     * Test if the data inserted into the database are the same with the one read
     *
     * @param errorMessage   Message to print if the data doesn't match
     * @param cursor         A database Cursor holding the data queried
     * @param insertedValues The pre-inserted values, which should be the same with those from the cursor
     */
    public static void validateInsertedData(String errorMessage, Cursor cursor, ContentValues insertedValues) {
        Set<Map.Entry<String, Object>> valueSet = insertedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String colName = entry.getKey();
            int idx = cursor.getColumnIndex(colName);
            assertFalse("Column '" + colName + "' not found. " + errorMessage, idx == -1);

            String expectedValue = entry.getValue().toString();
            String value = cursor.getString(idx);

            assertEquals("Value read doesn't match the expected value" + errorMessage, expectedValue, value);
        }

    }

    /**
     * Creates a stub list of movies to test mass insertion into the DB
     *
     * @return A {@code ContentValues} array of movies
     */
    public static ContentValues[] createStubMovieList() {
        ArrayList<ContentValues> contentValues = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            ContentValues cv = new ContentValues();

            cv.put(MovieContract.MovieEntry.COLUMN_TITLE, MOVIE_TITLE + i);
            cv.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                    Utility.releaseDateFormatter("2014-10-26"));
            cv.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 6.8 + i);
            cv.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, MOVIE_VOTE_COUNT + i * 10);
            cv.put(MovieContract.MovieEntry.COLUMN_DESCRIPTION, "lorem ipsum dolor");
            cv.put(MovieContract.MovieEntry.COLUMN_IMAGE_URL, "/img_of_interstellar.jpg");
            cv.put(MovieContract.MovieEntry.COLUMN_POPULARITY, MOVIE_POPULARITY + i * 2.3);
            cv.put(MovieContract.MovieEntry.COLUMN_RUNTIME, MOVIE_RUNTIME + i * 12);

            contentValues.add(cv);
        }

        if (!contentValues.isEmpty()) {
            ContentValues[] returnValues = new ContentValues[contentValues.size()];
            contentValues.toArray(returnValues);

            return returnValues;
        } else {
            return null;
        }

    }
}
