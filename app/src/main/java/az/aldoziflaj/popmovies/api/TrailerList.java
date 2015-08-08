package az.aldoziflaj.popmovies.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerList {

    @SerializedName("results")
    private List<MovieTrailer> trailerList;

    public List<MovieTrailer> getMovieList() {
        return trailerList;
    }
}
