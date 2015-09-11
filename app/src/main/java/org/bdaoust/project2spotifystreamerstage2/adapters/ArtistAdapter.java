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
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;

public class ArtistAdapter extends CursorAdapter{

    public ArtistAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.artist_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String artistName;
        String artistImageUrl;
        ImageView artistIcon;
        int colArtistName, colArtistImage;

        colArtistName = cursor.getColumnIndex(ArtistEntry.COLUMN_NAME);
        colArtistImage = cursor.getColumnIndex(ArtistEntry.COLUMN_IMAGE_URL);

        artistName = cursor.getString(colArtistName);
        artistImageUrl = cursor.getString(colArtistImage);

        artistIcon = (ImageView)view.findViewById(R.id.artistIcon);

        ((TextView)view.findViewById(R.id.artistName)).setText(artistName);
        if(!artistImageUrl.equals("")) {
            Picasso.with(context).load(artistImageUrl).into(artistIcon);
        }
        else {
            artistIcon.setImageDrawable(null);
        }
    }
}
