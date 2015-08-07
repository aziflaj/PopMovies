package az.aldoziflaj.popmovies;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * This interface serves as a client for The Movie DB. Retrofit does the job
 * TODO: create prototypes for other REST routes (check if needed)
 */
public interface TmdbService {

    /**
     * This method sends a GET request to the specified route of the service
     *
     * @param sortOrder The {@code sortby} query parameter, to sort the response as specified
     * @param callback  A {@code retrofit.Callback} to be called after the response is received
     */
    @GET("/discover/movie?api_key=" + Config.API_KEY)
    void getTopMovies(@Query("sortby") String sortOrder, Callback<Config.ApiResponse> callback);
}
