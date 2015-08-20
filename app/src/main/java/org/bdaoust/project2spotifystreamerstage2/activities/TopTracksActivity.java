package org.bdaoust.project2spotifystreamerstage2.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import com.squareup.picasso.Picasso;

import org.bdaoust.project2spotifystreamerstage2.QueryMapOptions;
import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.Tools;

public class TopTracksActivity extends AppCompatActivity implements View.OnClickListener{

    private SpotifyService spotifyService;
    private String artistId;

    private List<Track> artistTopTracks;
    private ArtistTopTracksListAdapter artistTopTracksListAdapter;
    private Context context;

    private final static String ARTIST_NAME_KEY = "ARTIST_NAME";
    private final static String ARTIST_ID_KEY = "ARTIST_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_tracks);

        String artistName;
        SpotifyApi spotifyApi;
        ListView artistTopTracksView;
        ActionBar actionBar;
        Bundle bundle;

        bundle = this.getIntent().getExtras();
        artistId = bundle.getString(ARTIST_ID_KEY);
        artistName = bundle.getString(ARTIST_NAME_KEY);

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(artistName);
        }

        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();
        context = this;


        artistTopTracks = new ArrayList<>();
        artistTopTracksListAdapter = new ArtistTopTracksListAdapter();
        artistTopTracksView = (ListView)findViewById(R.id.artistTopTracks);
        artistTopTracksView.setAdapter(artistTopTracksListAdapter);
    }

    @Override
    protected void onResume() {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        intent = new Intent(context,MediaPlayerActivity.class);
        startActivity(intent);
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
                artistTrackView = getLayoutInflater().inflate(R.layout.track_list_item, null);
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

            artistTrackView.setOnClickListener((TopTracksActivity)context);

            return artistTrackView;
        }
    }


}
