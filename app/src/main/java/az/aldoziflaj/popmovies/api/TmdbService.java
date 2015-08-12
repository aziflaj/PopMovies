package az.aldoziflaj.popmovies.api;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.api.models.AllMovies;
import az.aldoziflaj.popmovies.api.models.AllComments;
import az.aldoziflaj.popmovies.api.models.MovieRuntime;
import az.aldoziflaj.popmovies.api.models.AllTrailers;
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
    void getTopMovies(@Query("sortby") String sortOrder, Callback<AllMovies> callback);

    @GET("/movie/{id}?api_key=" + Config.API_KEY)
    void getMovieRuntime(@Path("id") int id, Callback<MovieRuntime> callback);

    @GET("/movie/{id}/videos?api_key=" + Config.API_KEY)
    void getMovieTrailers(@Path("id") int id, Callback<AllTrailers> callback);

    @GET("/movie/{id}/reviews?api_key=" + Config.API_KEY)
    void getMovieReviews(@Path("id") int id, Callback<AllComments> callback);
}
