package az.aldoziflaj.popmovies.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * The response from the server is a JSON object that has an array at the key "results"
 */
public class AllMovies {
    @SerializedName("results")
    List<MovieModel> movieList;

    public List<MovieModel> getMovieList() {
        return movieList;
    }

    public static class MovieModel {

        @SerializedName("original_title")
        private String mTitle;

        @SerializedName("id")
        private int mMovieId;

        @SerializedName("release_date")
        private String mReleaseDate;

        @SerializedName("poster_path")
        private String mPosterPath;

        @SerializedName("vote_average")
        private double mRating;

        @SerializedName("vote_count")
        private int mVoteCount;

        @SerializedName("overview")
        private String mDescription;

        @SerializedName("popularity")
        private double mPopularity;

        public String getTitle() {
            return mTitle;
        }

        public int getMovieId() {
            return mMovieId;
        }

        public String getReleaseDate() {
            return mReleaseDate;
        }

        public String getPosterPath() {
            return mPosterPath;
        }

        public double getRating() {
            return mRating;
        }

        public int getVoteCount() {
            return mVoteCount;
        }

        public String getDescription() {
            return mDescription;
        }

        public double getPopularity() {
            return mPopularity;
        }
    }
}
