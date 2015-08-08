package az.aldoziflaj.popmovies.api;

import az.aldoziflaj.popmovies.Config;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * This interface serves as a client for The Movie DB. Retrofit does the job
 */
public interface TmdbService {

    /**
     * This method sends a GET request to the specified route of the service
     *
     * @param sortOrder The {@code sortby} query parameter, to sort the response as specified
     * @param callback  A {@code retrofit.Callback} to be called after the response is received
     */
    @GET("/discover/movie?api_key=" + Config.API_KEY)
    void getTopMovies(@Query("sortby") String sortOrder, Callback<AllMoviesResponse> callback);

    @GET("/movie/{id}?api_key=" + Config.API_KEY)
    void getMovieRuntime(@Path("id") int id, Callback<MovieRuntime> callback);

    @GET("/movie/{id}/videos?api_key=" + Config.API_KEY)
    void getMovieTrailers(@Path("id") int id, Callback<TrailerList> callback);

    @GET("/movie/{id}/reviews?api_key=" + Config.API_KEY)
    void getMovieReviews(@Path("id") int id, Callback<CommentList> callback);
}
