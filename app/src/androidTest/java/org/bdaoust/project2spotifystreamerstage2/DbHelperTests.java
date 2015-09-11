package org.bdaoust.project2spotifystreamerstage2;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermToArtistEntry;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerDbHelper;

import java.util.Map;
import java.util.Set;

public class DbHelperTests extends AndroidTestCase{

    public void testCreateDb(){
        mContext.deleteDatabase(SpotifyStreamerDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new SpotifyStreamerDbHelper(mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb(){
        SpotifyStreamerDbHelper spotifyStreamerDbHelper;
        SQLiteDatabase db;
        Cursor cursor;
        ContentValues testValues;
        long artistRowId, topTrackRowId, searchTermRowId, searchTermToArtistRowId;

        spotifyStreamerDbHelper = new SpotifyStreamerDbHelper(mContext);
        db = spotifyStreamerDbHelper.getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON;");

        /************ Test insert/query Artist *************/
        testValues = createTestArtistValues();
        artistRowId = db.insert(ArtistEntry.TABLE_NAME,null,testValues);
        assertTrue(artistRowId != -1);

        cursor = db.query(
                ArtistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursorValues(cursor, testValues);
        cursor.close();
        /*****************************************************/


        /************ Test insert/query TopTrack *************/
        testValues = createTestTopTrackValues(artistRowId);
        topTrackRowId = db.insert(TopTracksEntry.TABLE_NAME,null,testValues);

        assertTrue(topTrackRowId != -1);

        cursor = db.query(
                TopTracksEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursorValues(cursor, testValues);
        cursor.close();
        /*****************************************************/


        /************ Test insert/query SearchTerm *************/
        testValues = createTestSearchTermValues();
        searchTermRowId = db.insert(SearchTermEntry.TABLE_NAME,null,testValues);

        assertTrue(searchTermRowId != -1);

        cursor = db.query(
                SearchTermEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursorValues(cursor, testValues);
        cursor.close();
        /*****************************************************/



        /************ Test insert/query SearchTermToArtist *************/
        testValues = createTestSearchTermToArtistValues(searchTermRowId, artistRowId);
        searchTermToArtistRowId = db.insert(SearchTermToArtistEntry.TABLE_NAME, null, testValues);

        assertTrue(searchTermToArtistRowId != -1);

        cursor = db.query(
                SearchTermToArtistEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        validateCursorValues(cursor, testValues);
        cursor.close();
        /*****************************************************/
    }

    public static ContentValues createTestArtistValues(){
        ContentValues artistValues;

        artistValues = new ContentValues();

        artistValues.put(ArtistEntry.COLUMN_NAME, "Some Artist Name");
        artistValues.put(ArtistEntry.COLUMN_IMAGE_URL, "http://somelinktotheimage");
        artistValues.put(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID, "someSpotifyArtistID");

        return artistValues;
    }

    public static ContentValues createTestTopTrackValues(long artistId){
        ContentValues topTrackValues;

        topTrackValues = new ContentValues();

        topTrackValues.put(TopTracksEntry.COLUMN_ARTIST_ID, artistId);
        topTrackValues.put(TopTracksEntry.COLUMN_SPOTIFY_TOP_TRACK_ID,"some spotify id");
        topTrackValues.put(TopTracksEntry.COLUMN_ALBUM_NAME,"Some Album Name");
        topTrackValues.put(TopTracksEntry.COLUMN_ALBUM_COVER_SMALL_URL,"path_to_small_image");
        topTrackValues.put(TopTracksEntry.COLUMN_ALBUM_COVER_LARGE_URL,"path_to_large_image");
        topTrackValues.put(TopTracksEntry.COLUMN_TRACK_NAME,"Some Track Name");
        topTrackValues.put(TopTracksEntry.COLUMN_SAMPLE_URL,"sample url");

        return topTrackValues;
    }

    public static ContentValues createTestSearchTermValues(){
        ContentValues searchTermValues;

        searchTermValues = new ContentValues();

        searchTermValues.put(SearchTermEntry.COLUMN_SEARCH_TERM,"pa");

        return searchTermValues;
    }

    public static ContentValues createTestSearchTermToArtistValues(long searchTermId, long artistId){
        ContentValues searchTermToArtistValues;

        searchTermToArtistValues = new ContentValues();

        searchTermToArtistValues.put(SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID,searchTermId);
        searchTermToArtistValues.put(SearchTermToArtistEntry.COLUMN_ARTIST_ID,artistId);

        return searchTermToArtistValues;
    }


    public static void validateCursorValues(Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> expectedValuesSet;

        expectedValuesSet = expectedValues.valueSet();

        if(valueCursor.getPosition() < 0) {
            assertTrue(valueCursor.moveToFirst());
        }

        for (Map.Entry<String, Object> entry : expectedValuesSet) {
            String columnName, expectedValue;
            int columnIndex;

            expectedValue = entry.getValue().toString();
            columnName = entry.getKey();
            columnIndex = valueCursor.getColumnIndex(columnName);

            assertFalse(columnIndex == -1);
            assertEquals(expectedValue, valueCursor.getString(columnIndex));
        }

    }
}
