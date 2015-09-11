package org.bdaoust.project2spotifystreamerstage2.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermToArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;

public class SpotifyStreamerDbHelper extends SQLiteOpenHelper{

    public static final int DATABASE_VERSION = 12;
    public static final String DATABASE_NAME = "spotify_streamer.db";

    public SpotifyStreamerDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SEARCH_TERMS_TABLE = "CREATE TABLE " + SearchTermEntry.TABLE_NAME + " (" +
                SearchTermEntry._ID + " INTEGER PRIMARY KEY, " +
                SearchTermEntry.COLUMN_SEARCH_TERM + " TEXT UNIQUE NOT NULL);";

        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY, " +
                ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID + " TEXT UNIQUE NOT NULL, " +
                ArtistEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                ArtistEntry.COLUMN_IMAGE_URL + " TEXT);";

        final String SQL_CREATE_SEARCH_TERMS_TO_ARTISTS_TABLE = "CREATE TABLE " + SearchTermToArtistEntry.TABLE_NAME + " (" +
                SearchTermToArtistEntry._ID + " INTEGER PRIMARY KEY, " +
                SearchTermToArtistEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +
                SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID + ") REFERENCES " +
                SearchTermEntry.TABLE_NAME + " (" + SearchTermEntry._ID + "), " +

                " FOREIGN KEY (" + SearchTermToArtistEntry.COLUMN_ARTIST_ID + ") REFERENCES " +
                ArtistEntry.TABLE_NAME + " (" + ArtistEntry._ID + "), " +

                " UNIQUE (" + SearchTermToArtistEntry.COLUMN_ARTIST_ID + ", " +
                SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID + ") ON CONFLICT IGNORE);";


        final String SQL_CREATE_TOP_TRACKS_TABLE = "CREATE TABLE " + TopTracksEntry.TABLE_NAME + " (" +
                TopTracksEntry._ID + " INTEGER PRIMARY KEY, " +
                TopTracksEntry.COLUMN_SPOTIFY_TOP_TRACK_ID + " TEXT UNIQUE NOT NULL, " +
                TopTracksEntry.COLUMN_ALBUM_NAME + " TEXT NOT NULL, " +
                TopTracksEntry.COLUMN_TRACK_NAME + " TEXT NOT NULL, " +
                TopTracksEntry.COLUMN_ALBUM_COVER_SMALL_URL + " TEXT, " +
                TopTracksEntry.COLUMN_ALBUM_COVER_LARGE_URL + " TEXT, " +
                TopTracksEntry.COLUMN_SAMPLE_URL + " TEXT NOT NULL, " +
                TopTracksEntry.COLUMN_ARTIST_ID + " INTEGER NOT NULL, " +

                " FOREIGN KEY (" + TopTracksEntry.COLUMN_ARTIST_ID + ") REFERENCES " +
                ArtistEntry.TABLE_NAME + " (" + ArtistEntry._ID + "));";

        db.execSQL(SQL_CREATE_SEARCH_TERMS_TABLE);
        db.execSQL(SQL_CREATE_SEARCH_TERMS_TO_ARTISTS_TABLE);
        db.execSQL(SQL_CREATE_ARTISTS_TABLE);
        db.execSQL(SQL_CREATE_TOP_TRACKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SearchTermToArtistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SearchTermEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TopTracksEntry.TABLE_NAME);
        onCreate(db);
    }
}
