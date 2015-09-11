package org.bdaoust.project2spotifystreamerstage2.fetchdata;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.bdaoust.project2spotifystreamerstage2.utils.Tools;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermToArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermEntry;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;

import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Image;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class FetchArtistsTask {

    private final Context context;
    private SpotifyService spotifyService;
    private OnSearchForArtistListener onSearchForArtistListener;

    public FetchArtistsTask(Context context, OnSearchForArtistListener onSearchForArtistListener){
        SpotifyApi spotifyApi;

        this.context = context;
        this.onSearchForArtistListener = onSearchForArtistListener;

        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();
    }

    public void fetch(final String artistSearchTerm){

        if(searchTermExists(artistSearchTerm)){

            Cursor cursor = context.getContentResolver().query(
                    ArtistEntry.buildArtistsUriWithSearchTerm(artistSearchTerm),
                    null,
                    null,
                    null,
                    null
            );

            if(cursor.moveToFirst()){
                onSearchForArtistListener.onArtistSearchSuccess(true);
            }
            else {
                onSearchForArtistListener.onArtistSearchSuccess(false);
            }

            cursor.close();
        }

        else {

            spotifyService.searchArtists(artistSearchTerm, new Callback<ArtistsPager>() {

                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    List<Artist> artists;
                    ContentValues searchTermContentValues;
                    Uri searchTermUri;
                    long searchTermRowId;

                    artists = artistsPager.artists.items;


                    /*********** Insert the searchTerm in the DB ******************/
                    searchTermContentValues = new ContentValues();
                    searchTermContentValues.put(SearchTermEntry.COLUMN_SEARCH_TERM,artistSearchTerm);
                    searchTermUri = context.getContentResolver().insert(SearchTermEntry.CONTENT_URI, searchTermContentValues);
                    searchTermRowId = ContentUris.parseId(searchTermUri);
                    /**************************************************************/

                    if(artists.size() > 0) {
                        ContentValues searchTermToArtistContentValues;
                        Artist artist;
                        long artistRowId;

                        for(int i=0; i< artists.size(); i++){
                            artist = artists.get(i);

                            artistRowId = addArtist(artist);

                            /************ Link the artist to the search term *************/
                            searchTermToArtistContentValues = new ContentValues();
                            searchTermToArtistContentValues.put(SearchTermToArtistEntry.COLUMN_ARTIST_ID,artistRowId);
                            searchTermToArtistContentValues.put(SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID,searchTermRowId);
                            context.getContentResolver().insert(SearchTermToArtistEntry.CONTENT_URI,searchTermToArtistContentValues);
                        /*****************************************************************/
                        }


                        onSearchForArtistListener.onArtistSearchSuccess(true);

                    }
                    else {
                        onSearchForArtistListener.onArtistSearchSuccess(false);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    onSearchForArtistListener.onArtistSearchFailure();
                }
            });

        }

    }

    public interface OnSearchForArtistListener{
        void onArtistSearchSuccess(boolean artistsFound);
        void onArtistSearchFailure();
    }

    private boolean searchTermExists(String searchTerm){
        boolean searchTermExists;
        Cursor cursor = context.getContentResolver().query(
                SearchTermEntry.CONTENT_URI,
                null,
                SearchTermEntry.COLUMN_SEARCH_TERM + " = ?",
                new String[]{searchTerm},
                null
        );

        if(cursor.moveToFirst()) {
            searchTermExists = cursor.moveToFirst();
            cursor.close();
        }
        else {
            searchTermExists = false;
        }

        return searchTermExists;
    }

    private long addArtist(Artist artist){
        Uri artistUri;
        long artistRowId;
        ContentValues contentValues;
        Cursor cursor;

        cursor = context.getContentResolver().query(
                ArtistEntry.CONTENT_URI,
                null,
                ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID + " = '" + artist.id + "'",
                null,
                null
        );

        if(cursor.moveToFirst()){
            artistRowId = cursor.getLong(cursor.getColumnIndex(ArtistEntry._ID));
        }
        else {

            Image preferedArtistImage;
            String artistImageUrl = "";

            preferedArtistImage = Tools.findPreferedSizeImageOrSmaller(artist.images, 200);

            if(preferedArtistImage != null){
                artistImageUrl = preferedArtistImage.url;
            }

            contentValues = new ContentValues();
            contentValues.put(ArtistEntry.COLUMN_NAME, artist.name);
            contentValues.put(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID, artist.id);
            contentValues.put(ArtistEntry.COLUMN_IMAGE_URL, artistImageUrl);

            artistUri = context.getContentResolver().insert(ArtistEntry.CONTENT_URI, contentValues);
            artistRowId = ContentUris.parseId(artistUri);
        }

        cursor.close();

        return artistRowId;
    }

}
