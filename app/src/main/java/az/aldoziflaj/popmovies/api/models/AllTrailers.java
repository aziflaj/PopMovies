package az.aldoziflaj.popmovies.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllTrailers {

    @SerializedName("results")
    private List<MovieTrailer> trailerList;

    public List<MovieTrailer> getMovieList() {
        return trailerList;
    }

    public static class MovieTrailer {

        @SerializedName("key")
        private String mKey;

        @SerializedName("name")
        private String mTrailerTitle;

        @SerializedName("site")
        private String mSite;

        public String getKey() {
            return mKey;
        }

        public String getTrailerTitle() {
            return mTrailerTitle;
        }

        public String getSite() {
            return mSite;
        }
    }
}
