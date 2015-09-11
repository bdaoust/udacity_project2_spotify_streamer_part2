package org.bdaoust.project2spotifystreamerstage2.fragments;

import android.database.Cursor;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;


import org.bdaoust.project2spotifystreamerstage2.fetchdata.FetchTopTracksTask;
import org.bdaoust.project2spotifystreamerstage2.utils.MyKeys;

import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.adapters.TopTrackAdapter;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;


public class TopTracksFragment extends Fragment implements FetchTopTracksTask.OnSearchForTopTracksListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private long artistId;

    private TopTrackAdapter topTrackAdapter;

    private static final int TOP_TRACK_LOADER = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        ListView artistTopTracksView;

        rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);
        topTrackAdapter = new TopTrackAdapter(getActivity(), null, 0);

        artistTopTracksView = (ListView) rootView.findViewById(R.id.artistTopTracks);
        artistTopTracksView.setAdapter(topTrackAdapter);
        artistTopTracksView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager;
                MediaPlayerFragment mediaPlayerFragment;
                Bundle bundle;
                Cursor cursor;
                int colArtistId;

                cursor = topTrackAdapter.getCursor();

                if (cursor != null && cursor.moveToPosition(position)) {

                    fragmentManager = getFragmentManager();
                    mediaPlayerFragment = new MediaPlayerFragment();

                    bundle = new Bundle();

                    colArtistId = cursor.getColumnIndex(TopTracksEntry.COLUMN_ARTIST_ID);
                    bundle.putLong(MyKeys.ARTIST_ID_KEY, cursor.getLong(colArtistId));
                    bundle.putInt(MyKeys.POSITION_KEY, position);

                    mediaPlayerFragment.setArguments(bundle);

                    if (getResources().getBoolean(R.bool.large_layout)) {
                        mediaPlayerFragment.show(fragmentManager, "MediaPlayerFragment");
                    } else {
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.add(android.R.id.content, mediaPlayerFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }

                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle;

        bundle = getArguments();

        if(bundle != null && bundle.containsKey(MyKeys.ARTIST_ID_KEY)){
            artistId = bundle.getLong(MyKeys.ARTIST_ID_KEY);

            if(artistId != -1) {
                FetchTopTracksTask fetchTopTracksTask = new FetchTopTracksTask(getActivity(), this);
                fetchTopTracksTask.fetch(artistId);
            }

        }

        if(artistId != -1) {
            getLoaderManager().initLoader(TOP_TRACK_LOADER, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(artistId != -1) {
            getLoaderManager().restartLoader(TOP_TRACK_LOADER, null, this);

            if(!getResources().getBoolean(R.bool.large_layout)){
                Cursor cursor = getActivity().getContentResolver().query(
                        ArtistEntry.buildArtistUriWithId(artistId),
                        new String[]{ArtistEntry.COLUMN_NAME},
                        null,
                        null,
                        null
                );

                cursor.moveToFirst();

                String artistName = cursor.getString(0);
                ActionBar actionBar;
                actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();

                if(actionBar != null){
                    actionBar.setSubtitle(artistName);
                }
            }
        }

    }

    @Override
    public void onTopTrackSearchSuccess(boolean topTracksFound) {
        if(topTracksFound) {
            getLoaderManager().restartLoader(TOP_TRACK_LOADER, null, this);
        }
        else {
            topTrackAdapter.swapCursor(null);
        }
    }

    @Override
    public void onTopTrackSearchFailure() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                ArtistEntry.buildArtistTopTracks(artistId),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        topTrackAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        topTrackAdapter.swapCursor(null);
    }


}
