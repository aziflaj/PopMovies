package az.aldoziflaj.popmovies.api;

import com.google.gson.annotations.SerializedName;

/**
 * This class will be used to get a Java Object from the JSON response by using GSON
 * TODO: Get Movie popularity (for sorting)
 */
public class Movie {

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
}
