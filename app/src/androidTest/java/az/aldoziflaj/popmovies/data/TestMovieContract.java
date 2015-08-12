package az.aldoziflaj.popmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * This test class is used for testing various methods in the {@code MovieContract} class
 */
public class TestMovieContract extends AndroidTestCase {
    public static final String TEST_POSTER_URL = "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg";
    public static final Uri MOVIE_WITH_POSTER_URI =
            MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                    .appendPath(TEST_POSTER_URL.substring(1)).build();

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * Test the methods for creating a movie URI with a given poster URL and vice-versa,
     * fetching the URL from a given URI.
     */
    public void testPosterUrl() {
        Uri movieWithPoster = MovieContract.MovieEntry.buildMovieWithPoster(TEST_POSTER_URL);

        assertEquals("Movie with poster not created correctly",
                MOVIE_WITH_POSTER_URI, movieWithPoster);

        String fetchedPoster = MovieContract.MovieEntry.getPosterUrlFromUri(movieWithPoster);

        assertEquals("Fetched poster doesn't match the real one",
                TEST_POSTER_URL.substring(1), fetchedPoster);
    }

}
