package az.aldoziflaj.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.List;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.activities.MainActivity;
import az.aldoziflaj.popmovies.api.TmdbService;
import az.aldoziflaj.popmovies.api.models.AllComments;
import az.aldoziflaj.popmovies.api.models.AllMovies;
import az.aldoziflaj.popmovies.api.models.AllTrailers;
import az.aldoziflaj.popmovies.api.models.MovieRuntime;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // time in seconds when to sync
    public static final int SYNC_INTERVAL = 60 * 60 * 10; // 10 hours
    private static final int MOVIE_NOTIFICATION_ID = 1001;
    private static long lastSyncTime = 0L;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(
                getAccount(context),
                context.getString(R.string.content_authority),
                bundle);
    }

    private static Account getAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name),
                context.getString(R.string.sync_account_type));

        if (accountManager.getPassword(newAccount) == null) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            // schedule the sync adapter
            ContentResolver.addPeriodicSync(newAccount,
                    context.getString(R.string.content_authority),
                    Bundle.EMPTY,
                    SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(newAccount,
                    context.getString(R.string.content_authority),
                    true);

            syncImmediately(context);
        }

        return newAccount;
    }

    public static void initSyncAdapter(Context context) {
        getAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        if (Utility.isOneDayLater(lastSyncTime)) {
            String sortOrder = Utility.getPreferredSortOrder(getContext());

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(Config.API_BASE_URL)
                    .build();
            final TmdbService tmdbService = restAdapter.create(TmdbService.class);

            //get list of movies
            tmdbService.getTopMovies(sortOrder, new Callback<AllMovies>() {
                @Override
                public void success(AllMovies allMovies, Response response) {
                    List<AllMovies.MovieModel> movieList = allMovies.getMovieList();

                    // store all movies in the DB
                    Utility.storeMovieList(getContext(), movieList);

                    for (final AllMovies.MovieModel movie : movieList) {
                        tmdbService.getMovieRuntime(movie.getMovieId(), new Callback<MovieRuntime>() {
                            @Override
                            public void success(MovieRuntime movieRuntime, Response response) {
                                int runtime = movieRuntime.getRuntime();
                                Utility.updateMovieWithRuntime(getContext(), movie.getMovieId(), runtime);

                            /*
                            // This is just for testing
                            Cursor c = getContext().getContentResolver().query(
                                    MovieContract.MovieEntry.CONTENT_URI,
                                    new String[]{MovieContract.MovieEntry._ID,
                                            MovieContract.MovieEntry.COLUMN_RUNTIME},
                                    MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                                    new String[]{Integer.toString(movie.getMovieId())},
                                    null
                            );
                            if (c.moveToFirst()) {
                                int runtimeColIndex = c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RUNTIME);
                                int readRuntime = c.getInt(runtimeColIndex);
                                Log.d("Movie - Get Runtime",
                                        String.format("'%s' runs for %d minutes",
                                                movie.getTitle(), readRuntime));
                            } else {
                                Log.e("Movie - Get Runtime", "Not read!");
                            }
                            c.close();
                            //*/
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e("SyncAdapter", "Error: " + error);
                            }
                        });

                        tmdbService.getMovieReviews(movie.getMovieId(), new Callback<AllComments>() {
                            @Override
                            public void success(AllComments allComments, Response response) {
                                List<AllComments.Comment> commentList = allComments.getCommentList();

                                Utility.storeCommentList(getContext(), movie.getMovieId(), commentList);
                            /*
                            // This is just for testing
                            Cursor c = getContext().getContentResolver().query(
                                    MovieContract.ReviewEntry.CONTENT_URI,
                                    new String[]{MovieContract.ReviewEntry._ID,
                                            MovieContract.ReviewEntry.COLUMN_AUTHOR,
                                            MovieContract.ReviewEntry.COLUMN_CONTENT},
                                    MovieContract.ReviewEntry.COLUMN_MOVIE_ID + "= ?",
                                    new String[]{Integer.toString(movie.getMovieId())},
                                    null
                            );

                            if (c.moveToFirst()) {
                                do {
                                    int authorColIndex = c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR);
                                    int contentColIndex = c.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT);

                                    Log.d("Movie - Get Comments",
                                            String.format("%s says: %s",
                                                    c.getString(authorColIndex),
                                                    c.getString(contentColIndex)));

                                } while (c.moveToNext());
                            } else {
                                Log.d("Movie - Get Comments", "No comments");
                            }
                            c.close();
                            //*/
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e("SyncAdapter", "Error: " + error);
                            }
                        });

                        tmdbService.getMovieTrailers(movie.getMovieId(), new Callback<AllTrailers>() {
                            @Override
                            public void success(AllTrailers allTrailers, Response response) {
                                Utility.storeTrailerList(getContext(), movie.getMovieId(), allTrailers.getTrailerList());
                            /*
                            // This is just for testing
                            Cursor c = getContext().getContentResolver().query(
                                    MovieContract.TrailerEntry.CONTENT_URI,
                                    new String[]{MovieContract.TrailerEntry._ID,
                                            MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY},
                                    MovieContract.TrailerEntry.COLUMN_MOVIE_ID + "= ?",
                                    new String[]{Integer.toString(movie.getMovieId())},
                                    null
                            );
                            if (c.moveToFirst()) {
                                do {
                                    int youtubeKeyColIndex = c.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
                                    String youtubeKey = c.getString(youtubeKeyColIndex);
                                    Log.d("Movie - Get Trailers",
                                            String.format("%s has this key trailer: %s",
                                                    movie.getTitle(), youtubeKey));
                                } while (c.moveToNext());
                            } else {
                                Log.e("Movie - Get Trailers", "Not read!");
                            }
                            c.close();
                            //*/
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e("SyncAdapter", "Error: " + error);
                            }
                        });
                    }

                    sendNotification();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("SyncAdapter", "Error: " + error);
                }
            });
        }
    }

    private void sendNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean displayNotifications = prefs.getBoolean(getContext().getString(R.string.prefs_notification_key), true);

        if (!displayNotifications) {
            return;
        }

        String lastNotificationKey = getContext().getString(R.string.prefs_notification_last_key);
        lastSyncTime = prefs.getLong(lastNotificationKey, 0);

        if (Utility.isOneDayLater(lastSyncTime)) {
            //Show notification

            int smallIcon = R.mipmap.ic_launcher;
            Bitmap largeIcon = BitmapFactory.decodeResource(
                    getContext().getResources(),
                    R.mipmap.ic_launcher);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext())
                    .setSmallIcon(smallIcon)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(getContext().getString(R.string.app_name))
                    .setContentText(getContext().getString(R.string.notification_content));

            Intent notificationIntent = new Intent(getContext(), MainActivity.class);

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
            stackBuilder.addNextIntent(notificationIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            builder.setContentIntent(resultPendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(MOVIE_NOTIFICATION_ID, builder.build()); //notify

            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastNotificationKey, System.currentTimeMillis());
            editor.apply();
        }

    }

}
