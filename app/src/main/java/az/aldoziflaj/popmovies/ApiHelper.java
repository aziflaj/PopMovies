package az.aldoziflaj.popmovies;

/**
 * Created by aziflaj on 6/13/15.
 */
public interface ApiHelper {
    String API_KEY = "6e7da6d84d9ec6215cffd1c3e8924c87";
    String API_BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    String IMAGE_DEFAULT_SIZE = "w185";

    String ID_KEY = "id";
    String ORIGINAL_TITLE_KEY = "original_title";
    String RELEASE_DATE_KEY = "release_date";
    String POSTER_PATH_KEY = "poster_path";
    String VOTE_AVERAGE_KEY = "vote_average";
    String OVERVIEW_KEY = "overview";
    String SORT_KEY = "overview";
    String API_KEY_QUERY = "api_key";

    String SORT_BY_POPULARITY = "popularity.desc";
    String SORT_BY_VOTES = "vote_average.desc";
}
