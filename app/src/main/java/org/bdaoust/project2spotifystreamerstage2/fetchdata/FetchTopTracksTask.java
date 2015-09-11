package org.bdaoust.project2spotifystreamerstage2.fetchdata;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.bdaoust.project2spotifystreamerstage2.utils.QueryMapOptions;
import org.bdaoust.project2spotifystreamerstage2.utils.Tools;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;

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


public class FetchTopTracksTask {

    private final Context context;
    private SpotifyService spotifyService;
    private OnSearchForTopTracksListener onSearchForTopTracksListener;

    public FetchTopTracksTask(Context context, OnSearchForTopTracksListener onSearchForTopTracksListener){
        SpotifyApi spotifyApi;

        this.context = context;
        this.onSearchForTopTracksListener = onSearchForTopTracksListener;

        spotifyApi = new SpotifyApi();
        spotifyService = spotifyApi.getService();
    }

    public void fetch(final long artistId) {

        Cursor cursor;
        Map<String,Object> options;
        String spotifyArtistId;

        cursor = context.getContentResolver().query(
                ArtistEntry.buildArtistTopTracks(artistId),
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            onSearchForTopTracksListener.onTopTrackSearchSuccess(true);
        } else {


            options = new QueryMapOptions();
            options.put(SpotifyService.COUNTRY, "CA");

            cursor = context.getContentResolver().query(
                    ArtistEntry.buildArtistUriWithId(artistId),
                    null,
                    null,
                    null,
                    null);

            cursor.moveToFirst();
            spotifyArtistId = cursor.getString(cursor.getColumnIndex(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID));

            spotifyService.getArtistTopTrack(spotifyArtistId, options, new Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, Response response) {
                    List<Track> topTracks;
                    Track topTrack;

                    topTracks = tracks.tracks;

                    if(topTracks.size() > 0)

                    {
                        for (int i = 0; i < topTracks.size(); i++) {
                            topTrack = topTracks.get(i);

                            addTopTrack(topTrack, artistId);
                        }

                        onSearchForTopTracksListener.onTopTrackSearchSuccess(true);

                    }
                    else {
                        onSearchForTopTracksListener.onTopTrackSearchSuccess(false);
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    onSearchForTopTracksListener.onTopTrackSearchFailure();
                }
            });
        }

        cursor.close();
    }

    public interface OnSearchForTopTracksListener{
        void onTopTrackSearchSuccess(boolean topTracksFound);
        void onTopTrackSearchFailure();
    }


    private void addTopTrack(Track track, long artistId){
        Cursor cursor;

        cursor = context.getContentResolver().query(
                TopTracksEntry.CONTENT_URI,
                null,
                TopTracksEntry.COLUMN_SPOTIFY_TOP_TRACK_ID + " = '" + track.id + "'",
                null,
                null
        );

        if(!cursor.moveToFirst()){
            ContentValues contentValues;
            Image smallAlbumCoverImage, largeAlbumCoverImage;
            String smallAlbumCoverUrl = "", largeAlbumCoverUrl = "";

            smallAlbumCoverImage = Tools.findPreferedSizeImageOrSmaller(track.album.images, 200);
            if(smallAlbumCoverImage != null){
                smallAlbumCoverUrl = smallAlbumCoverImage.url;
            }
            largeAlbumCoverImage = Tools.findPreferedSizeImageOrLarger(track.album.images, 200);
            if(largeAlbumCoverImage != null){
                largeAlbumCoverUrl = largeAlbumCoverImage.url;
            }

            contentValues = new ContentValues();
            contentValues.put(TopTracksEntry.COLUMN_SPOTIFY_TOP_TRACK_ID,track.id);
            contentValues.put(TopTracksEntry.COLUMN_ALBUM_NAME,track.album.name);
            contentValues.put(TopTracksEntry.COLUMN_TRACK_NAME,track.name);
            contentValues.put(TopTracksEntry.COLUMN_SAMPLE_URL,track.preview_url);
            contentValues.put(TopTracksEntry.COLUMN_ALBUM_COVER_SMALL_URL,smallAlbumCoverUrl);
            contentValues.put(TopTracksEntry.COLUMN_ALBUM_COVER_LARGE_URL,largeAlbumCoverUrl);
            contentValues.put(TopTracksEntry.COLUMN_ARTIST_ID,artistId);

            context.getContentResolver().insert(TopTracksEntry.CONTENT_URI,contentValues);
        }

        cursor.close();
    }

}
