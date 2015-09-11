package org.bdaoust.project2spotifystreamerstage2.data;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermToArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;

public class SpotifyStreamerProvider extends ContentProvider{

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private SpotifyStreamerDbHelper dbHelper;

    private static final int CODE_SEARCH_TERMS = 1;
    private static final int CODE_SEARCH_TERM_BY_ID = 2;
    private static final int CODE_SEARCH_TERMS_TO_ARTISTS = 3;
    private static final int CODE_SEARCH_TERM_TO_ARTIST_BY_ID = 4;
    private static final int CODE_ARTISTS = 5;
    private static final int CODE_ARTIST_BY_ID = 6;
    private static final int CODE_ARTIST_TOP_TRACKS = 7;
    private static final int CODE_TOP_TRACKS = 8;
    private static final int CODE_TOP_TRACK_BY_ID = 9;


    private static final SQLiteQueryBuilder artistBySearchTermQuery, artistTopTracksQuery;

    static {
        artistBySearchTermQuery = new SQLiteQueryBuilder();
        artistBySearchTermQuery.setTables(
                SearchTermEntry.TABLE_NAME +
                        " INNER JOIN " +
                        SearchTermToArtistEntry.TABLE_NAME +
                        " ON " + SearchTermEntry.TABLE_NAME + "." + SearchTermEntry._ID +
                        " = " + SearchTermToArtistEntry.TABLE_NAME + "." + SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID +
                        " INNER JOIN " +
                        ArtistEntry.TABLE_NAME +
                        " ON " + ArtistEntry.TABLE_NAME + "." + ArtistEntry._ID +
                        " = " + SearchTermToArtistEntry.TABLE_NAME + "." + SearchTermToArtistEntry.COLUMN_ARTIST_ID
        );

        artistTopTracksQuery = new SQLiteQueryBuilder();
        artistTopTracksQuery.setTables(
                ArtistEntry.TABLE_NAME +
                        " INNER JOIN " +
                        TopTracksEntry.TABLE_NAME +
                        " ON " + ArtistEntry.TABLE_NAME + "." + ArtistEntry._ID +
                        " = " + TopTracksEntry.TABLE_NAME + "." + TopTracksEntry.COLUMN_ARTIST_ID
        );
    }

    @Override
    public boolean onCreate() {
        dbHelper = new SpotifyStreamerDbHelper(getContext());

        return true;
    }

    private Cursor getArtists(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder){
        Cursor cursor;
        String searchTerm;

        searchTerm = ArtistEntry.getSearchTermFromUri(uri);

        if(searchTerm == null){

            cursor = dbHelper.getReadableDatabase().query(
                    ArtistEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);
        }
        else{
            cursor = artistBySearchTermQuery.query(dbHelper.getReadableDatabase(),
                    projection,
                    SearchTermEntry.COLUMN_SEARCH_TERM + " = ? ",
                    new String[]{searchTerm},
                    null,
                    null,
                    sortOrder);
        }

        return cursor;
    }

    private Cursor getArtistTopTracks(Uri uri, String[] projection, String sortOrder){
        Cursor cursor;

        long artistId;
        artistId = ArtistEntry.getArtistIdFromUri(uri);

        cursor = artistTopTracksQuery.query(dbHelper.getReadableDatabase(),
                projection,
                TopTracksEntry.COLUMN_ARTIST_ID + " = " + artistId,
                null,
                null,
                null,
                sortOrder);

        return cursor;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (uriMatcher.match(uri)){
            case CODE_SEARCH_TERMS:
                cursor = dbHelper.getReadableDatabase().query(
                        SearchTermEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_SEARCH_TERM_BY_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        SearchTermEntry.TABLE_NAME,
                        projection,
                        SearchTermEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_ARTISTS:
                cursor = getArtists(uri, projection, selection, selectionArgs, sortOrder);
                break;
            case CODE_ARTIST_BY_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        ArtistEntry.TABLE_NAME,
                        projection,
                        ArtistEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_ARTIST_TOP_TRACKS:
                cursor = getArtistTopTracks(uri, projection, sortOrder);
                break;
            case CODE_SEARCH_TERMS_TO_ARTISTS:
                cursor = dbHelper.getReadableDatabase().query(
                        SearchTermToArtistEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_SEARCH_TERM_TO_ARTIST_BY_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        SearchTermToArtistEntry.TABLE_NAME,
                        projection,
                        SearchTermToArtistEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_TOP_TRACKS:
                cursor = dbHelper.getReadableDatabase().query(
                        TopTracksEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case CODE_TOP_TRACK_BY_ID:
                cursor = dbHelper.getReadableDatabase().query(
                        TopTracksEntry.TABLE_NAME,
                        projection,
                        TopTracksEntry._ID + " = " + ContentUris.parseId(uri),
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        int match = uriMatcher.match(uri);

        switch (match){
            case CODE_SEARCH_TERMS:
                return SearchTermEntry.CONTENT_TYPE;
            case CODE_SEARCH_TERM_BY_ID:
                return SearchTermEntry.CONTENT_ITEM_TYPE;

            case CODE_ARTISTS:
                return ArtistEntry.CONTENT_TYPE;
            case CODE_ARTIST_BY_ID:
                return ArtistEntry.CONTENT_ITEM_TYPE;

            case CODE_SEARCH_TERMS_TO_ARTISTS:
                return SearchTermToArtistEntry.CONTENT_TYPE;
            case CODE_SEARCH_TERM_TO_ARTIST_BY_ID:
                return SearchTermToArtistEntry.CONTENT_ITEM_TYPE;

            case CODE_TOP_TRACKS:
                return TopTracksEntry.CONTENT_TYPE;
            case CODE_TOP_TRACK_BY_ID:
                return TopTracksEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int uriMatch = uriMatcher.match(uri);
        Uri returnUri;
        long rowId;

        switch (uriMatch){
            case CODE_SEARCH_TERMS:
                rowId = database.insert(SearchTermEntry.TABLE_NAME, null, values);
                if(rowId != -1){
                    returnUri = SearchTermEntry.buildSearchTermUriWithId(rowId);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CODE_ARTISTS:
                rowId = database.insert(ArtistEntry.TABLE_NAME, null, values);
                if(rowId != -1){
                    returnUri = ArtistEntry.buildArtistUriWithId(rowId);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CODE_SEARCH_TERMS_TO_ARTISTS:
                rowId = database.insert(SearchTermToArtistEntry.TABLE_NAME, null, values);
                if(rowId != -1){
                    returnUri = SearchTermToArtistEntry.buildSearchTermToArtistUriWithId(rowId);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CODE_TOP_TRACKS:
                rowId = database.insert(TopTracksEntry.TABLE_NAME, null, values);
                if(rowId != -1){
                    returnUri = TopTracksEntry.buildTopTrackUriWithId(rowId);
                }
                else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case CODE_SEARCH_TERMS:
                rowsDeleted = database.delete(SearchTermEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_ARTISTS:
                rowsDeleted = database.delete(ArtistEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case CODE_SEARCH_TERMS_TO_ARTISTS:
                rowsDeleted = database.delete(SearchTermToArtistEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case CODE_TOP_TRACKS:
                rowsDeleted = database.delete(TopTracksEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if(rowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SpotifyStreamerContract.CONTENT_AUTHORITY;

        // /search_terms
        matcher.addURI(authority, SpotifyStreamerContract.PATH_SEARCH_TERMS, CODE_SEARCH_TERMS);
        // /search_terms/{id}
        matcher.addURI(authority, SpotifyStreamerContract.PATH_SEARCH_TERMS + "/#", CODE_SEARCH_TERM_BY_ID);

        // /artists
        matcher.addURI(authority, SpotifyStreamerContract.PATH_ARTISTS, CODE_ARTISTS);
        // /artists/{id}
        matcher.addURI(authority, SpotifyStreamerContract.PATH_ARTISTS + "/#", CODE_ARTIST_BY_ID);
        // /artists/{id}/top_tracks
        matcher.addURI(authority, SpotifyStreamerContract.PATH_ARTISTS + "/#" + "/" + TopTracksEntry.TABLE_NAME, CODE_ARTIST_TOP_TRACKS);

        // /search_terms_to_artists
        matcher.addURI(authority, SpotifyStreamerContract.PATH_SEARCH_TERMS_TO_ARTISTS, CODE_SEARCH_TERMS_TO_ARTISTS);
        // /search_terms_to_artists/{id}
        matcher.addURI(authority, SpotifyStreamerContract.PATH_SEARCH_TERMS_TO_ARTISTS + "/#", CODE_SEARCH_TERM_TO_ARTIST_BY_ID);

        // /top_tracks
        matcher.addURI(authority, SpotifyStreamerContract.PATH_TOP_TRACKS, CODE_TOP_TRACKS);
        // /top_tracks/{id}
        matcher.addURI(authority, SpotifyStreamerContract.PATH_TOP_TRACKS + "/#", CODE_TOP_TRACK_BY_ID);


        return matcher;
    }
}
