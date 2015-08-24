package org.bdaoust.project2spotifystreamerstage2.fragments;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.bdaoust.project2spotifystreamerstage2.MyKeys;
import org.bdaoust.project2spotifystreamerstage2.QueryMapOptions;
import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopTracksFragment extends Fragment implements View.OnClickListener{
    private SpotifyService spotifyService;
    private String artistId;

    private List<Track> artistTopTracks;
    private ArtistTopTracksListAdapter artistTopTracksListAdapter;
    private Context context;
    private TopTracksFragment topTracksFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotifyApi spotifyApi;

        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();

        artistTopTracks = new ArrayList<>();
        artistTopTracksListAdapter = new ArtistTopTracksListAdapter();

        topTracksFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView;
        ListView artistTopTracksView;

        rootView = inflater.inflate(R.layout.fragment_top_tracks,container,false);

        artistTopTracksView = (ListView)rootView.findViewById(R.id.artistTopTracks);
        artistTopTracksView.setAdapter(artistTopTracksListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle;

        context = getActivity();
        bundle = getArguments();

        if(bundle != null && bundle.containsKey(MyKeys.ARTIST_ID_KEY)){
            artistId = bundle.getString(MyKeys.ARTIST_ID_KEY);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Map options;

        options = new QueryMapOptions();
        options.put(SpotifyService.COUNTRY, "CA");

        spotifyService.getArtistTopTrack(artistId, options, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                artistTopTracks = tracks.tracks;
                artistTopTracksListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        FragmentManager fragmentManager;
        MediaPlayerFragment mediaPlayerFragment;
        Bundle bundle;
        int position;

        fragmentManager = getFragmentManager();
        mediaPlayerFragment = new MediaPlayerFragment();

        position = (int)v.getTag();
        bundle = new Bundle();

        /**************************************/
        bundle.putString("TRACK_PREVIEW_URL",artistTopTracks.get(position).preview_url);
        bundle.putString("TRACK_NAME",artistTopTracks.get(position).name);
        bundle.putString("TRACK_ARTIST_NAME",artistTopTracks.get(position).artists.get(0).name);
        bundle.putString("TRACK_ALBUM_NAME",artistTopTracks.get(position).album.name);
        bundle.putString("TRACK_ALBUM_IMAGE", artistTopTracks.get(position).album.images.get(0).url);
        /***************************************/

        mediaPlayerFragment.setArguments(bundle);

        if(getResources().getBoolean(R.bool.large_layout)){
            mediaPlayerFragment.show(fragmentManager, "MediaPlayerFragment");
        }
        else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(android.R.id.content,mediaPlayerFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }
    }

    private class ArtistTopTracksListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return artistTopTracks.size();
        }

        @Override
        public Object getItem(int position) {
            return artistTopTracks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View artistTrackView;
            TextView albumName, trackName;
            final ImageView albumIcon;
            Image preferedAlbumImage;

            if(convertView == null) {
                artistTrackView = getActivity().getLayoutInflater().inflate(R.layout.track_list_item, null);
            }
            else {
                artistTrackView = convertView;
            }

            albumName = (TextView)artistTrackView.findViewById(R.id.albumName);
            trackName = (TextView)artistTrackView.findViewById(R.id.trackName);
            albumIcon = (ImageView)artistTrackView.findViewById(R.id.albumIcon);

            albumName.setText(artistTopTracks.get(position).album.name);
            trackName.setText(artistTopTracks.get(position).name);
            albumIcon.setImageBitmap(null);

            preferedAlbumImage = Tools.findPreferedSizeImage(artistTopTracks.get(position).album.images, 200);
            if(preferedAlbumImage !=null) {
                Picasso.with(context).load(preferedAlbumImage.url).into(albumIcon);
            }

            artistTrackView.setOnClickListener(topTracksFragment);
            artistTrackView.setTag(position);

            return artistTrackView;
        }
    }

}
