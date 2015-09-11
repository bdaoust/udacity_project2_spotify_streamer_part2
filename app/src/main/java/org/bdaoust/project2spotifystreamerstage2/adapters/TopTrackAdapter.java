package org.bdaoust.project2spotifystreamerstage2.adapters;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;

public class TopTrackAdapter extends CursorAdapter{

    public TopTrackAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.track_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView albumIconView;
        TextView albumNameView, trackNameView;
        String albumName, trackName, albumIconUrl;
        int colAlbumNameIndex, colTrackNameIndex, colAlbumIconIndex;

        albumIconView = (ImageView)view.findViewById(R.id.albumIcon);
        albumNameView = (TextView)view.findViewById(R.id.albumName);
        trackNameView = (TextView)view.findViewById(R.id.trackName);

        colAlbumNameIndex = cursor.getColumnIndex(TopTracksEntry.COLUMN_ALBUM_NAME);
        colTrackNameIndex = cursor.getColumnIndex(TopTracksEntry.COLUMN_TRACK_NAME);
        colAlbumIconIndex = cursor.getColumnIndex(TopTracksEntry.COLUMN_ALBUM_COVER_SMALL_URL);

        albumName = cursor.getString(colAlbumNameIndex);
        trackName = cursor.getString(colTrackNameIndex);
        albumIconUrl = cursor.getString(colAlbumIconIndex);

        albumNameView.setText(albumName);
        trackNameView.setText(trackName);
        if(!albumIconUrl.equals("")){
            Picasso.with(context).load(albumIconUrl).into(albumIconView);
        }
    }
}
