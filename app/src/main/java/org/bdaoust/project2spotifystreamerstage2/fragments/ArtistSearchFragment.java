package org.bdaoust.project2spotifystreamerstage2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.Tools;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ArtistSearchFragment extends Fragment{

    private ArtistListAdapter artistListAdapter;
    private SpotifyService spotifyService;
    private Context context;
    private Toast toast;
    private List<Artist> artists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SpotifyApi spotifyApi;

        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();

        artists = new ArrayList<>();
        artistListAdapter = new ArtistListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;
        EditText artistQuery;
        ListView artistSearchResults;
        ArtistNameTextWatch artistNameTextWatch;

        rootView = inflater.inflate(R.layout.fragment_artist_search,null);

        artistNameTextWatch = new ArtistNameTextWatch();
        artistQuery = (EditText)rootView.findViewById(R.id.artistQuery);
        artistQuery.addTextChangedListener(artistNameTextWatch);

        artistSearchResults = (ListView)rootView.findViewById(R.id.artistSearchResults);
        artistSearchResults.setAdapter(artistListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
    }

    private class ArtistListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return artists.size();
        }

        @Override
        public Object getItem(int position) {
            return artists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View artistView;
            TextView artistName;
            final ImageView artistIcon;
            Image preferedArtistImage;

            if(convertView == null) {
                artistView = getActivity().getLayoutInflater().inflate(R.layout.artist_list_item, null);
            }
            else {
                artistView = convertView;
            }

            artistName = (TextView)artistView.findViewById(R.id.artistName);
            artistIcon = (ImageView)artistView.findViewById(R.id.artistIcon);

            artistName.setText(artists.get(position).name);
            artistIcon.setImageBitmap(null);

            preferedArtistImage = Tools.findPreferedSizeImage(artists.get(position).images, 200);
            if(preferedArtistImage !=null) {
                Picasso.with(context).load(preferedArtistImage.url).into(artistIcon);
            }

            artistView.setTag(artists.get(position));

            artistView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Artist artist;

                    artist = (Artist)v.getTag();
                    ((OnArtistClickListener)getActivity()).onItemSelected(artist.name, artist.id);

                }
            });

            return artistView;
        }
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
            spotifyService.searchArtists(artist.toString(), new Callback<ArtistsPager>() {

                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    artists = artistsPager.artists.items;
                    artistListAdapter.notifyDataSetChanged();

                    if(artists.size() == 0){
                        toast  = Toast.makeText(context, getText(R.string.search_result_no_artists_found), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    artists.clear();
                    artistListAdapter.notifyDataSetChanged();
                }
            });

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    public interface OnArtistClickListener {
        void onItemSelected(String artistName, String artistId);
    }
}
