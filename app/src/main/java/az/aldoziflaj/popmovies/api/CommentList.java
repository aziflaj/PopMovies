package az.aldoziflaj.popmovies.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommentList {

    @SerializedName("results")
    private List<Comment> commentList;

    public List<Comment> getCommentList() {
        return commentList;
    }
}
