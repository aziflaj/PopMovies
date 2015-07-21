package az.aldoziflaj.popmovies.data;

import android.provider.BaseColumns;

public class MovieContract {

    public class MovieTable implements BaseColumns {
        public static final String TABLE_NAME = "movies";

        // columns
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_DESCRIPTION = "desc";
        public static final String COLUMN_IMAGE_URL = "image_url";
    }
}
