package az.aldoziflaj.popmovies.api.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllComments {

    @SerializedName("results")
    private List<Comment> commentList;

    public List<Comment> getCommentList() {
        return commentList;
    }

    public static class Comment {
        @SerializedName("author")
        private String author;

        @SerializedName("content")
        private String content;

        public String getAuthor() {
            return author;
        }

        public String getContent() {
            return content;
        }
    }
}
