package org.bdaoust.project2spotifystreamerstage2.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.bdaoust.project2spotifystreamerstage2.adapters.ArtistAdapter;
import org.bdaoust.project2spotifystreamerstage2.fetchdata.FetchArtistsTask;
import org.bdaoust.project2spotifystreamerstage2.R;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;

public class ArtistSearchFragment extends Fragment implements LoaderCallbacks<Cursor>, FetchArtistsTask.OnSearchForArtistListener{


    private Context context;
    private Toast toast;
    private ArtistSearchFragment artistSearchFragment;
    private ListView artistSearchResults;
    private String searchTerm = "";
    private int artistListPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    private static final String SEARCH_TERM_KEY = "search_term";

    ArtistAdapter artistAdapter;

    private static final int ARTISTS_LOADER = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        artistSearchFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;
        EditText artistQuery;
        ArtistNameTextWatch artistNameTextWatch;

        rootView = inflater.inflate(R.layout.fragment_artist_search,null);

        artistAdapter = new ArtistAdapter(getActivity(), null, 0);

        artistNameTextWatch = new ArtistNameTextWatch();
        artistQuery = (EditText)rootView.findViewById(R.id.artistQuery);
        artistQuery.addTextChangedListener(artistNameTextWatch);

        artistSearchResults = (ListView)rootView.findViewById(R.id.artistSearchResults);
        artistSearchResults.setAdapter(artistAdapter);
        artistSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = artistAdapter.getCursor();

                if(cursor != null && cursor.moveToPosition(position)){
                    long artistId;

                    artistId = cursor.getLong(cursor.getColumnIndex(ArtistEntry._ID));

                    ((OnArtistListChangeListener)getActivity()).onItemSelected(artistId);
                }
                artistListPosition = position;
            }
        });

        if(savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)){
            artistListPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        if(savedInstanceState != null && savedInstanceState.containsKey(SEARCH_TERM_KEY)){
            searchTerm = savedInstanceState.getString(SEARCH_TERM_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        context = getActivity();
        getLoaderManager().initLoader(ARTISTS_LOADER, null, this);


        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().restartLoader(ARTISTS_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                ArtistEntry.buildArtistsUriWithSearchTerm(searchTerm),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        artistAdapter.swapCursor(data);

        if(artistListPosition != ListView.INVALID_POSITION){
            artistSearchResults.smoothScrollToPosition(artistListPosition);

            artistSearchResults.setSelection(artistListPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        artistAdapter.swapCursor(null);
    }

    @Override
    public void onArtistSearchSuccess(boolean artistsFound) {
        if(!artistsFound) {
            artistAdapter.swapCursor(null);
            toast = Toast.makeText(context, getText(R.string.search_result_no_artists_found), Toast.LENGTH_SHORT);
            toast.show();
        }
        else {
            getLoaderManager().restartLoader(ARTISTS_LOADER, null, this);
        }
    }

    @Override
    public void onArtistSearchFailure() {
        artistAdapter.swapCursor(null);
    }

    private class ArtistNameTextWatch implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(toast != null){
                toast.cancel();
            }
        }

        @Override
        public void onTextChanged(CharSequence artist, int start, int before, int count) {
            if(!searchTerm.equals(artist.toString())) {
                FetchArtistsTask fetchArtistsTask;

                searchTerm = artist.toString();
                fetchArtistsTask = new FetchArtistsTask(context,artistSearchFragment);
                fetchArtistsTask.fetch(artist.toString());

                ((OnArtistListChangeListener) getActivity()).onSearchTermChanged(searchTerm);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(artistListPosition != ListView.INVALID_POSITION){
            outState.putInt(SELECTED_KEY, artistListPosition);
        }
        outState.putString(SEARCH_TERM_KEY,searchTerm);

        super.onSaveInstanceState(outState);
    }

    public interface OnArtistListChangeListener {
        void onItemSelected(long artistId);

        void onSearchTermChanged(String searchTerm);
    }

}
