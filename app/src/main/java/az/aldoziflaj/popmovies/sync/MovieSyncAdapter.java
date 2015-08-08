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

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.api.TmdbService;
import az.aldoziflaj.popmovies.Utility;
import az.aldoziflaj.popmovies.activities.MainActivity;
import az.aldoziflaj.popmovies.api.AllMoviesResponse;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // time in seconds when to sync
    public static final int SYNC_INTERVAL = 60 * 60 * 10; // 10 hours
    private static final long ONE_DAY = 1000 * 60 * 60 * 24;
    private static final int MOVIE_NOTIFICATION_ID = 1001;

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
        String sortOrder = Utility.getPreferredSortOrder(getContext());

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(Config.API_BASE_URL)
                .build();
        TmdbService tmdbService = restAdapter.create(TmdbService.class);
        tmdbService.getTopMovies(sortOrder, new Callback<AllMoviesResponse>() {
            @Override
            public void success(AllMoviesResponse allMovies, Response response) {
                Utility.storeMovieList(getContext(), allMovies.getMovieList());
                sendNotification();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SyncAdapter", "Error: " + error);
            }
        });

    }

    private void sendNotification() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean displayNotifications = prefs.getBoolean(getContext().getString(R.string.prefs_notification_key), true);

        if (!displayNotifications) {
            return;
        }

        String lastNotificationKey = getContext().getString(R.string.prefs_notification_last_key);
        long lastSyncTime = prefs.getLong(lastNotificationKey, 0);

        if (System.currentTimeMillis() - lastSyncTime >= ONE_DAY) {
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
