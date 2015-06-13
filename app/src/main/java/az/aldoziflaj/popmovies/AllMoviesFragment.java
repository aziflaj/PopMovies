package az.aldoziflaj.popmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class AllMoviesFragment extends Fragment {
    public static final String LOG_TAG = AllMoviesFragment.class.getSimpleName();
    ArrayList<HashMap<String, String>> movieList;
    MovieAdapter movieAdapter;
    GridView moviesGridView;

    public AllMoviesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviesGridView = (GridView) rootView.findViewById(R.id.movies_gridview);

        //*
        // For testing only!
        movieList = new ArrayList<>();
        String[] moviesTitleList = {
                "Interstellar",
                "Jurasic World",
                "Mad Max",
                "Kingsman: The Secret Service"
        };

        String[] moviesPosterList = {
                "nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg", //interstellar
                "uXZYawqUsChGSj54wcuBtEdUJbh.jpg", //jurasic world
                "kqjL17yufvn9OVLyXYpvtyrFfak.jpg", //mad max
                "oAISjx6DvR2yUn9dxj00vP8OcJJ.jpg" //kingsman
        };

        for (int i=0; i<4; i++) {
            HashMap<String, String> tmpMovie = new HashMap<>();
            tmpMovie.put(MovieAdapter.MOVIE_TITLE, moviesTitleList[i]);
            tmpMovie.put(MovieAdapter.MOVIE_POSTER, moviesPosterList[i]);
            movieList.add(tmpMovie);
        }
        //*/

        movieAdapter = new MovieAdapter(
                getActivity(),
                movieList, // list of data to show
                R.layout.movie_info, // the layout of a single item
                new String[] { MovieAdapter.MOVIE_TITLE, MovieAdapter.MOVIE_POSTER },
                new int[] { R.id.movie_title_textview, R.id.movie_poster });

        moviesGridView.setAdapter(movieAdapter);

        moviesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String movieTitle = (String) ((TextView) view.findViewById(R.id.movie_title_textview)).getText();
                Intent detailsIntent = new Intent(getActivity(), MovieDetailsActivity.class);
                detailsIntent.putExtra("movie_title", movieTitle);
                startActivity(detailsIntent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_allmovies, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_refresh) {
            FetchMoviesTask task = new FetchMoviesTask();
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    class FetchMoviesTask extends AsyncTask<Void, Void, String> {
        public final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        Toast errorInConnection;

        @Override
        protected void onPreExecute() {
            errorInConnection = Toast.makeText(getActivity(),
                    "Can't connect to the server",
                    Toast.LENGTH_LONG);
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            String moviesJSONString = null;
            BufferedReader reader = null;

            try {
                Uri moviesUri = Uri.parse(ApiHelper.API_BASE_URL).buildUpon()
                        .appendQueryParameter(ApiHelper.SORT_KEY, ApiHelper.SORT_BY_POPULARITY)
                        .appendQueryParameter(ApiHelper.API_KEY_QUERY, ApiHelper.API_KEY)
                        .build();
                URL url = new URL(moviesUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }

                if (sb.length() == 0) {
                    Log.d(LOG_TAG, "length of message 0");
                    return null;
                }

                moviesJSONString = sb.toString();
                //fetchMovieListFromJSON(moviesJSONString);

            } catch (MalformedURLException e) {
                errorInConnection.show();
                Log.e(LOG_TAG, "Error: " + e.getMessage());
                return null;

            } catch (IOException e) {
                errorInConnection.show();
                Log.e(LOG_TAG, "Error: " + e.getMessage());
                return null;

            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error: "+ e.getMessage());

                    }
                }
            }

            return moviesJSONString;
        }

        @Override
        protected void onPostExecute(String moviesJsonString) {
            movieList = fetchMovieListFromJSON(moviesJsonString);
            Log.d(LOG_TAG, "movielist updated");

            //*
            movieAdapter.notifyDataSetChanged();
            Log.d(LOG_TAG, "movieAdapter notified");
            moviesGridView.invalidateViews();
            moviesGridView.setAdapter(movieAdapter);
            //*/
        }

        private ArrayList<HashMap<String, String>> fetchMovieListFromJSON(String jsonString) {
            ArrayList<String> movieList = new ArrayList<>();
            ArrayList<HashMap<String, String>> kvPair = new ArrayList<>();

            try {
                JSONObject jsonResponse = new JSONObject(jsonString);
                JSONArray jsonMovieList = jsonResponse.getJSONArray("results");
                int movieListLength = jsonMovieList.length();
                Log.d(LOG_TAG, movieListLength + " items fetched");

                for (int i=0; i<movieListLength; i++) {
                    JSONObject currentMovie = jsonMovieList.getJSONObject(i);
                    HashMap<String, String> item = new HashMap<>();
                    item.put("title", currentMovie.getString(ApiHelper.ORIGINAL_TITLE_KEY));
                    //item.put("image", ApiHelper.IMAGE_BASE_URL + currentMovie.getString(ApiHelper.POSTER_PATH_KEY));
                    item.put("image", Integer.toString(R.drawable.mad_max));
                    kvPair.add(item);
                    //movieList.add(currentMovie.getString(ApiHelper.ORIGINAL_TITLE_KEY));
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error: " + e.getMessage());
                e.printStackTrace();
            }

            if (!movieList.isEmpty()) {
                Log.d(LOG_TAG, "movieList is not empty");
                for (String item : movieList) {
                    Log.d(LOG_TAG, item);
                }
            } else {
                Log.d(LOG_TAG, "movieList is empty");
            }

            //return (String[]) movieList.toArray();
            return kvPair;
        }
    }
}
