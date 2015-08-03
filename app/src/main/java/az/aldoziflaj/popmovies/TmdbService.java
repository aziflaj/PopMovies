package az.aldoziflaj.popmovies;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface TmdbService {
    @GET("/discover/movie?api_key=" + Config.API_KEY)
    void getTopMovies(@Query("sortby") String sortOrder, Callback<Config.ApiResponse> callback);
}
