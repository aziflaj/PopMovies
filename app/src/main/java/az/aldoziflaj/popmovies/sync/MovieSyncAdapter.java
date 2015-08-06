package az.aldoziflaj.popmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import az.aldoziflaj.popmovies.Config;
import az.aldoziflaj.popmovies.R;
import az.aldoziflaj.popmovies.TmdbService;
import az.aldoziflaj.popmovies.Utility;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {

    // time in seconds when to sync
    public static final int SYNC_INTERVAL = 60 * 60 * 10; // 10 hours

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
        tmdbService.getTopMovies(sortOrder, new Callback<Config.ApiResponse>() {
            @Override
            public void success(Config.ApiResponse apiResponse, Response response) {
                Utility.storeMovieList(getContext(), apiResponse.getMovieList());
                // TODO send a notification for new movies. see http://git.io/vO1Me
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SyncAdapter", "Error: " + error);
            }
        });

    }

}
