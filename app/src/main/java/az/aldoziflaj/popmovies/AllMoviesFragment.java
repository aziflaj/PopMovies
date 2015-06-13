package az.aldoziflaj.popmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import az.aldoziflaj.popmovies.apiclient.FetchMoviesTask;


/**
 * A placeholder fragment containing a simple view.
 */
public class AllMoviesFragment extends Fragment {
    public static final String LOG_TAG = AllMoviesFragment.class.getSimpleName();
    SimpleAdapter simpleAdapter;

    public AllMoviesFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView moviesGridView = (GridView) rootView.findViewById(R.id.movies_gridview);

        //*
        // For testing only!
        final ArrayList<HashMap<String, String>> movieList = new ArrayList<>();
        String[] moviesTitleList = {
                "Interstellar",
                "Jurasic World",
                "Mad Max",
                "Kingsman: The Secret Service"
        };

        int[] moviesPosterList = {
                R.drawable.interstellar,
                R.drawable.jurasic_world,
                R.drawable.mad_max,
                R.drawable.kingsman
        };

        for (int i=0; i<4; i++) {
            HashMap<String, String> tmpMovie = new HashMap<>();
            tmpMovie.put("title", moviesTitleList[i]);
            tmpMovie.put("image", Integer.toString(moviesPosterList[i]));
            movieList.add(tmpMovie);
        }
        //*/

        simpleAdapter = new SimpleAdapter(
                getActivity(),
                movieList, // list of data to show
                R.layout.movie_info, // the layout of a single item
                new String[] {"title", "image"},
                new int[] { R.id.movie_title_textview, R.id.movie_poster });

        moviesGridView.setAdapter(simpleAdapter);

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
            FetchMoviesTask task = new FetchMoviesTask(getActivity());
            task.execute();
        }

        return super.onOptionsItemSelected(item);
    }
}
