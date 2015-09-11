package org.bdaoust.project2spotifystreamerstage2;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.SearchTermToArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;

public class ContentProviderTests extends AndroidTestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        deleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        deleteAllRecords();
    }

    public void testGetType(){
        ContentResolver contentResolver;
        Uri uri;
        String type;

        contentResolver = mContext.getContentResolver();

        // content://org.bdaoust.project2spotifystreamerstage2/artists
        uri = ArtistEntry.CONTENT_URI;
        type = contentResolver.getType(uri);
        assertEquals(ArtistEntry.CONTENT_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/artists/{id}
        uri = ArtistEntry.buildArtistUriWithId(123);
        type = contentResolver.getType(uri);
        assertEquals(ArtistEntry.CONTENT_ITEM_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/search_terms
        uri = SearchTermEntry.CONTENT_URI;
        type = contentResolver.getType(uri);
        assertEquals(SearchTermEntry.CONTENT_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/search_terms/{id}
        uri = SearchTermEntry.buildSearchTermUriWithId(123);
        type = contentResolver.getType(uri);
        assertEquals(SearchTermEntry.CONTENT_ITEM_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/search_terms_to_artists
        uri = SearchTermToArtistEntry.CONTENT_URI;
        type = contentResolver.getType(uri);
        assertEquals(SearchTermToArtistEntry.CONTENT_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/search_terms_to_artists/{id}
        uri = SearchTermToArtistEntry.buildSearchTermToArtistUriWithId(123);
        type = contentResolver.getType(uri);
        assertEquals(SearchTermToArtistEntry.CONTENT_ITEM_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/top_tracks
        uri = TopTracksEntry.CONTENT_URI;
        type = contentResolver.getType(uri);
        assertEquals(TopTracksEntry.CONTENT_TYPE, type);

        // content://org.bdaoust.project2spotifystreamerstage2/top_tracks/{id}
        uri = TopTracksEntry.buildTopTrackUriWithId(123);
        type = contentResolver.getType(uri);
        assertEquals(TopTracksEntry.CONTENT_ITEM_TYPE, type);

    }

    public void testInsertReadProvider(){
        /******** Search Term ************/
        ContentValues testValues = DbHelperTests.createTestSearchTermValues();
        Uri searchTermUri = mContext.getContentResolver().insert(SearchTermEntry.CONTENT_URI, testValues);
        long searchTermRowId = ContentUris.parseId(searchTermUri);

        assertTrue(searchTermRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                SearchTermEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor,testValues);

        cursor = mContext.getContentResolver().query(
                SearchTermEntry.buildSearchTermUriWithId(searchTermRowId),
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor, testValues);

        /*********** Artist ***********/
        testValues = DbHelperTests.createTestArtistValues();
        Uri artistUri = mContext.getContentResolver().insert(ArtistEntry.CONTENT_URI, testValues);
        long artistRowId = ContentUris.parseId(artistUri);

        assertTrue(artistRowId != -1);

        ArtistEntry.buildArtistUriWithId(artistRowId);

        cursor = mContext.getContentResolver().query(
                ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        DbHelperTests.validateCursorValues(cursor,testValues);

        cursor = mContext.getContentResolver().query(
                ArtistEntry.buildArtistUriWithId(artistRowId),
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor,testValues);


        /********** SearchTermToArtist ***********/
        testValues = DbHelperTests.createTestSearchTermToArtistValues(searchTermRowId, artistRowId);
        Uri searchTermToArtistUri = mContext.getContentResolver().insert(SearchTermToArtistEntry.CONTENT_URI, testValues);
        assertNotNull(searchTermToArtistUri);
        long searchTermToArtistRowId = ContentUris.parseId(searchTermToArtistUri);

        assertTrue(searchTermToArtistRowId != -1);

        cursor = mContext.getContentResolver().query(
                SearchTermToArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor, testValues);

        cursor = mContext.getContentResolver().query(
                SearchTermToArtistEntry.buildSearchTermToArtistUriWithId(searchTermToArtistRowId),
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor,testValues);



        /**********TopTrack ***********/
        testValues = DbHelperTests.createTestTopTrackValues(artistRowId);
        Uri topTrackUri = mContext.getContentResolver().insert(TopTracksEntry.CONTENT_URI,testValues);

        long topTrackRowId = ContentUris.parseId(topTrackUri);

        assertTrue(topTrackRowId != -1);

        cursor = mContext.getContentResolver().query(
                TopTracksEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor,testValues);

        cursor = mContext.getContentResolver().query(
                TopTracksEntry.buildTopTrackUriWithId(topTrackRowId),
                null,
                null,
                null,
                null
        );

        DbHelperTests.validateCursorValues(cursor, testValues);
    }

    public void testSearchForArtistsWithSearchTerm(){
        String searchTermAb = "ab";
        ContentValues abSoulContentValues, abCrazyContentValues, abcdContentValues, rhiannaContentValues;
        ContentResolver contentResolver;
        Uri abSoulArtistUri, abCrazyArtistUri, abcdArtistUri, rhiannaArtistUri;
        long abSoulArtistRowId, abCrazyArtistRowId, abcdArtistRowId, rhiannaArtistRowId;

        contentResolver = mContext.getContentResolver();

        /********* Insert SearchTerms ************/
        ContentValues searchTermContentValues;

        searchTermContentValues = new ContentValues();
        searchTermContentValues.put(SearchTermEntry.COLUMN_SEARCH_TERM, searchTermAb);
        long abSearchTermRowId = ContentUris.parseId(contentResolver.insert(SearchTermEntry.CONTENT_URI,searchTermContentValues));

        searchTermContentValues = new ContentValues();
        searchTermContentValues.put(SearchTermEntry.COLUMN_SEARCH_TERM, "rhianna");
        long rhiannaSearchTermRowId = ContentUris.parseId(contentResolver.insert(SearchTermEntry.CONTENT_URI, searchTermContentValues));
        /*****************************************/

        abSoulContentValues = createArtistAbSoul();
        abCrazyContentValues = createArtistABCrazy();
        abcdContentValues = createArtistABCD();
        rhiannaContentValues = createArtistRhianna();

        abSoulArtistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, abSoulContentValues);
        abSoulArtistRowId = ContentUris.parseId(abSoulArtistUri);

        abCrazyArtistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, abCrazyContentValues);
        abCrazyArtistRowId = ContentUris.parseId(abCrazyArtistUri);

        rhiannaArtistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, rhiannaContentValues);
        rhiannaArtistRowId = ContentUris.parseId(rhiannaArtistUri);

        abcdArtistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, abcdContentValues);
        abcdArtistRowId = ContentUris.parseId(abcdArtistUri);

        /********************** Insert SearchTermToArtist ****************************************/
        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put(SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID,abSearchTermRowId);
        contentValues.put(SearchTermToArtistEntry.COLUMN_ARTIST_ID,abSoulArtistRowId);
        contentResolver.insert(SearchTermToArtistEntry.CONTENT_URI, contentValues);

        contentValues = new ContentValues();
        contentValues.put(SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID,abSearchTermRowId);
        contentValues.put(SearchTermToArtistEntry.COLUMN_ARTIST_ID,abCrazyArtistRowId);
        contentResolver.insert(SearchTermToArtistEntry.CONTENT_URI,contentValues);

        contentValues = new ContentValues();
        contentValues.put(SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID,abSearchTermRowId);
        contentValues.put(SearchTermToArtistEntry.COLUMN_ARTIST_ID,abcdArtistRowId);
        contentResolver.insert(SearchTermToArtistEntry.CONTENT_URI,contentValues);

        contentValues = new ContentValues();
        contentValues.put(SearchTermToArtistEntry.COLUMN_SEARCH_TERM_ID,rhiannaSearchTermRowId);
        contentValues.put(SearchTermToArtistEntry.COLUMN_ARTIST_ID,rhiannaArtistRowId);
        contentResolver.insert(SearchTermToArtistEntry.CONTENT_URI,contentValues);
        /*****************************************************************************************/

        Cursor cursor = mContext.getContentResolver().query(
                ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        cursor.moveToFirst();

        DbHelperTests.validateCursorValues(cursor, abSoulContentValues);
        cursor.moveToNext();

        DbHelperTests.validateCursorValues(cursor, abCrazyContentValues);
        cursor.moveToNext();

        DbHelperTests.validateCursorValues(cursor, rhiannaContentValues);
        cursor.moveToNext();

        DbHelperTests.validateCursorValues(cursor, abcdContentValues);
        assertFalse(cursor.moveToNext());

        cursor.close();

        cursor = mContext.getContentResolver().query(
                ArtistEntry.buildArtistsUriWithSearchTerm(searchTermAb),
                null,
                null,
                null,
                null
        );

        assertEquals(3,cursor.getCount());

        cursor.moveToFirst();
        DbHelperTests.validateCursorValues(cursor, abSoulContentValues);

        cursor.moveToNext();
        DbHelperTests.validateCursorValues(cursor, abCrazyContentValues);

        cursor.moveToNext();
        DbHelperTests.validateCursorValues(cursor, abcdContentValues);

    }

    public void testSearchForTopTracks(){
        ContentValues abSoulContentValues, rhiannaContentValues;
        ContentValues rhiannaTopTrack1ContentValues, rhiannaTopTrack2ContentValues, rhiannaTopTrack3ContentValues, abSoulTopTrack1ContentValues;
        ContentResolver contentResolver;
        Uri abSoulArtistUri, rhiannaArtistUri;
        long abSoulArtistRowId, rhiannaArtistRowId;

        contentResolver = mContext.getContentResolver();


        abSoulContentValues = createArtistAbSoul();
        abSoulArtistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, abSoulContentValues);
        abSoulArtistRowId = ContentUris.parseId(abSoulArtistUri);

        rhiannaContentValues = createArtistRhianna();
        rhiannaArtistUri = contentResolver.insert(ArtistEntry.CONTENT_URI, rhiannaContentValues);
        rhiannaArtistRowId = ContentUris.parseId(rhiannaArtistUri);


        rhiannaTopTrack1ContentValues = createTopTrackValues(rhiannaArtistRowId, 1);
        contentResolver.insert(TopTracksEntry.CONTENT_URI, rhiannaTopTrack1ContentValues);

        rhiannaTopTrack2ContentValues = createTopTrackValues(rhiannaArtistRowId, 2);
        contentResolver.insert(TopTracksEntry.CONTENT_URI, rhiannaTopTrack2ContentValues);

        rhiannaTopTrack3ContentValues = createTopTrackValues(rhiannaArtistRowId, 3);
        contentResolver.insert(TopTracksEntry.CONTENT_URI, rhiannaTopTrack3ContentValues);

        abSoulTopTrack1ContentValues = createTopTrackValues(abSoulArtistRowId, 5);
        contentResolver.insert(TopTracksEntry.CONTENT_URI, abSoulTopTrack1ContentValues);


        Cursor cursor = mContext.getContentResolver().query(
                ArtistEntry.buildArtistTopTracks(rhiannaArtistRowId),
                null,
                null,
                null,
                null
        );

        assertEquals(3, cursor.getCount());

        cursor.moveToFirst();
        DbHelperTests.validateCursorValues(cursor, rhiannaTopTrack1ContentValues);

        cursor.moveToNext();
        DbHelperTests.validateCursorValues(cursor, rhiannaTopTrack2ContentValues);

        cursor.moveToNext();
        DbHelperTests.validateCursorValues(cursor, rhiannaTopTrack3ContentValues);

        cursor.close();
    }

    public void deleteAllRecords(){
        Cursor cursor;

        mContext.getContentResolver().delete(
                SearchTermEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                ArtistEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                SearchTermToArtistEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                TopTracksEntry.CONTENT_URI,
                null,
                null
        );

        cursor = mContext.getContentResolver().query(
                SearchTermEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                ArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );
        assertEquals(0,cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                SearchTermToArtistEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );
        assertEquals(0,cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                TopTracksEntry.CONTENT_URI,
                null,
                null,
                null,
                null,
                null
        );
        assertEquals(0,cursor.getCount());
        cursor.close();
    }


    private ContentValues createArtistAbSoul(){
        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put(ArtistEntry.COLUMN_NAME, "Ab-Soul");
        contentValues.put(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID, "2AV7bayWSUDsA3Iwb7b7UN");
        contentValues.put(ArtistEntry.COLUMN_IMAGE_URL, "https://i.scdn.co/image/cc64067ffb3503596ec2cdfdc2622604dcc53179");

        return contentValues;
    }

    private ContentValues createArtistABCrazy(){
        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put(ArtistEntry.COLUMN_NAME, "AB Crazy");
        contentValues.put(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID, "75IFS4w4dhyYMDGnTfzOAx");
        contentValues.put(ArtistEntry.COLUMN_IMAGE_URL, "https://i.scdn.co/image/6d9e4814df2d85550ef29e17da3846724c043670");

        return contentValues;
    }

    private ContentValues createArtistABCD(){
        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put(ArtistEntry.COLUMN_NAME, "AB/CD");
        contentValues.put(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID, "5MP9bH9aUryiKQeUvABLIU");
        contentValues.put(ArtistEntry.COLUMN_IMAGE_URL, "https://i.scdn.co/image/2e80acd3ad8b3352d5ea25d53b65b57a9d90fb0d");

        return contentValues;

    }

    private ContentValues createArtistRhianna(){
        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put(ArtistEntry.COLUMN_NAME, "Rhianna");
        contentValues.put(ArtistEntry.COLUMN_SPOTIFY_ARTIST_ID, "3EVeAWpKJBHjicn5xRp3mu");
        contentValues.put(ArtistEntry.COLUMN_IMAGE_URL, "https://i.scdn.co/image/a50f03dc67320295870045eb7d7c7502f4bbadd6");

        return contentValues;
    }

    private ContentValues createTopTrackValues(long artistId, int trackNumber){
        ContentValues contentValues;

        contentValues = new ContentValues();
        contentValues.put(TopTracksEntry.COLUMN_ALBUM_NAME,"Some album name " + trackNumber);
        contentValues.put(TopTracksEntry.COLUMN_ALBUM_COVER_SMALL_URL,"http://album_cover_small_" + trackNumber);
        contentValues.put(TopTracksEntry.COLUMN_ALBUM_COVER_LARGE_URL,"http://album_cover_large_" + trackNumber);
        contentValues.put(TopTracksEntry.COLUMN_ARTIST_ID,artistId);
        contentValues.put(TopTracksEntry.COLUMN_SPOTIFY_TOP_TRACK_ID,"Spotify id " + trackNumber);
        contentValues.put(TopTracksEntry.COLUMN_TRACK_NAME,"Track Name " + trackNumber);
        contentValues.put(TopTracksEntry.COLUMN_SAMPLE_URL,"http://sampleUrl " + trackNumber);

        return contentValues;
    }

}
