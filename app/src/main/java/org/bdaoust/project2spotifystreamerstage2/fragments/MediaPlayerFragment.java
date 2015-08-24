package org.bdaoust.project2spotifystreamerstage2.fragments;


import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.bdaoust.project2spotifystreamerstage2.R;

import java.io.IOException;

public class MediaPlayerFragment extends DialogFragment implements View.OnClickListener{

    private ImageButton playBtn;
    private String trackPreviewUrl;
    private String albumImageUrl, trackName, artistName, albumName;
    private TextView currentTime, totalTime, albumNameTV, trackNameTV, artistNameTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;

        bundle = getArguments();
        trackPreviewUrl = bundle.getString("TRACK_PREVIEW_URL");
        albumImageUrl = bundle.getString("TRACK_ALBUM_IMAGE");

        trackName = bundle.getString("TRACK_NAME");
        artistName = bundle.getString("TRACK_ARTIST_NAME");
        albumName = bundle.getString("TRACK_ALBUM_NAME");

        Log.v("AAA", "TRACK_PREVIEW_URL: " + bundle.getString("TRACK_PREVIEW_URL"));
        Log.v("AAA", "TRACK_NAME: " + bundle.getString("TRACK_NAME"));
        Log.v("AAA", "TRACK_ARTIST: " + bundle.getString("TRACK_ARTIST"));
        Log.v("AAA", "TRACK_ALBUM_NAME: " + bundle.getString("TRACK_ALBUM_NAME"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;
        ImageView albumCover;

        rootView = inflater.inflate(R.layout.fragment_media_player,container,false);
        playBtn = (ImageButton)rootView.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        currentTime = (TextView)rootView.findViewById(R.id.currentTime);
        totalTime = (TextView)rootView.findViewById(R.id.totalTime);

        albumNameTV = (TextView)rootView.findViewById(R.id.albumName);
        trackNameTV = (TextView)rootView.findViewById(R.id.trackName);
        artistNameTV = (TextView)rootView.findViewById(R.id.artistName);

        albumNameTV.setText(albumName);
        trackNameTV.setText(trackName);
        artistNameTV.setText(artistName);

        albumCover = (ImageView)rootView.findViewById(R.id.albumCover);
        Picasso.with(getActivity()).load(albumImageUrl).into(albumCover);

        return rootView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.playBtn:
                Uri myUri = Uri.parse(trackPreviewUrl);

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try {
                    mediaPlayer.setDataSource(this.getActivity(), myUri);
                    mediaPlayer.prepare();
                    int duration = mediaPlayer.getDuration();
                    totalTime.setText("00:" + duration / 1000);
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

