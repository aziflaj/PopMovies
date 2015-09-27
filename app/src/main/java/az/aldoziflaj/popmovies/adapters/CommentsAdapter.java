package az.aldoziflaj.popmovies.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.data.MovieContract;


public class CommentsAdapter extends CursorAdapter {
    public CommentsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.comment_list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView authorTextView = (TextView) view.findViewById(R.id.comment_author);
        TextView contentTextView = (TextView) view.findViewById(R.id.comment_content);

        int authorColumnIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        int contentColumnIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);

        String author = cursor.getString(authorColumnIndex);
        String content = cursor.getString(contentColumnIndex);

        authorTextView.setText(author);
        contentTextView.setText(content);
    }
}
