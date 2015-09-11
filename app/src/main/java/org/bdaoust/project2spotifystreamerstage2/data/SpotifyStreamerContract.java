package org.bdaoust.project2spotifystreamerstage2.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class SpotifyStreamerContract {

    public static final String CONTENT_AUTHORITY = "org.bdaoust.project2spotifystreamerstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SEARCH_TERMS = "search_terms";
    public static final String PATH_ARTISTS = "artists";
    public static final String PATH_SEARCH_TERMS_TO_ARTISTS = "search_terms_to_artists";
    public static final String PATH_TOP_TRACKS = "top_tracks";

    public static final class SearchTermEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_TERMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_TERMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_TERMS;

        public static final String TABLE_NAME = "search_terms";

        public static final String COLUMN_SEARCH_TERM = "search_term";

        public static Uri buildSearchTermUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }

    public static final class SearchTermToArtistEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEARCH_TERMS_TO_ARTISTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_TERMS_TO_ARTISTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEARCH_TERMS_TO_ARTISTS;

        public  static final String TABLE_NAME = "search_terms_to_artists";

        public static final String COLUMN_SEARCH_TERM_ID = "search_term_id";
        public static final String COLUMN_ARTIST_ID = "artist_id";

        public static Uri buildSearchTermToArtistUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

    public static final class ArtistEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS;

        public static final String TABLE_NAME = "artists";

        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SPOTIFY_ARTIST_ID = "spotify_artist_id";
        public static final String COLUMN_IMAGE_URL = "image_url";

        public static Uri buildArtistUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static Uri buildArtistsUriWithSearchTerm(String searchTerm) {
            return CONTENT_URI.buildUpon().appendQueryParameter(SearchTermEntry.COLUMN_SEARCH_TERM, searchTerm).build();
        }

        public static Uri buildArtistTopTracks(long id){
            return ContentUris.appendId(CONTENT_URI.buildUpon(), id).appendPath(TopTracksEntry.TABLE_NAME).build();
        }

        public static String getSearchTermFromUri(Uri uri){
            return uri.getQueryParameter(SearchTermEntry.COLUMN_SEARCH_TERM);
        }

        public static long getArtistIdFromUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }
    }

    public static final class TopTracksEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOP_TRACKS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_TRACKS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOP_TRACKS;

        public static final String TABLE_NAME = "top_tracks";

        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_SPOTIFY_TOP_TRACK_ID = "spotify_top_track_id";
        public static final String COLUMN_ALBUM_NAME = "album_name";
        public static final String COLUMN_TRACK_NAME = "track_name";
        public static final String COLUMN_ALBUM_COVER_SMALL_URL = "album_cover_small_url";
        public static final String COLUMN_ALBUM_COVER_LARGE_URL = "album_cover_large_url";
        public static final String COLUMN_SAMPLE_URL = "sample_url";

        public static Uri buildTopTrackUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
