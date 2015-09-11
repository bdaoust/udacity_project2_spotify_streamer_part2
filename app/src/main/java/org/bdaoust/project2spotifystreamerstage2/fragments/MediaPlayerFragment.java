package org.bdaoust.project2spotifystreamerstage2.fragments;


import android.app.Dialog;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.TopTracksEntry;
import org.bdaoust.project2spotifystreamerstage2.data.SpotifyStreamerContract.ArtistEntry;
import org.bdaoust.project2spotifystreamerstage2.utils.MyKeys;
import org.bdaoust.project2spotifystreamerstage2.utils.Tools;

import java.io.IOException;

public class MediaPlayerFragment extends DialogFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor>{

    private ImageButton playBtn, pauseBtn, prevBtn, nextBtn;
    private ImageView albumCover;
    private TextView albumNameTV, trackNameTV, artistNameTV;
    private TextView currentTimeTV, totalTimeTV;
    private SeekBar seekBar;
    private ActionBar actionBar;
    private String prevTitle, prevSubTitle;

    private int lastCurrentSeconds = 0;
    private float duration;
    private boolean seekBarSelected = false;
    private int seekPercentage = 0;

    private MediaPlayer mediaPlayer;
    private MediaUIUpdater mediaUIUpdater;

    private long artistId;
    private int position;

    private static final int MEDIA_PLAYER_LOADER = 0;

    private static final String[] MEDIA_PLAYER_COLUMNS = {
            ArtistEntry.COLUMN_NAME,
            TopTracksEntry.COLUMN_ALBUM_NAME,
            TopTracksEntry.COLUMN_TRACK_NAME,
            TopTracksEntry.COLUMN_ALBUM_COVER_LARGE_URL,
            TopTracksEntry.COLUMN_SAMPLE_URL
    };

    private static final int COL_ARTIST_NAME = 0;
    private static final int COL_ALBUM_NAME = 1;
    private static final int COL_TRACK_NAME = 2;
    private static final int COL_ALBUM_COVER = 3;
    private static final int COL_SAMPLE_URL = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle;

        bundle = getArguments();

        artistId = bundle.getLong(MyKeys.ARTIST_ID_KEY);
        position = bundle.getInt(MyKeys.POSITION_KEY);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mediaPlayer == null){
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        if (!getResources().getBoolean(R.bool.large_layout) && actionBar != null) {
            String title = getResources().getString(R.string.title_activity_media_player);

            prevTitle = (String) actionBar.getTitle();
            prevSubTitle = (String) actionBar.getSubtitle();
            setActionBarTitleAndSubTitle(actionBar, title, null);
        }

        getLoaderManager().initLoader(MEDIA_PLAYER_LOADER, null, this);
        mediaUIUpdater = new MediaUIUpdater();
        mediaUIUpdater.execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        if(!getResources().getBoolean(R.bool.large_layout) && actionBar != null) {
            setActionBarTitleAndSubTitle(actionBar, prevTitle, prevSubTitle);
        }

        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
        mediaUIUpdater.cancel(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;

        rootView = inflater.inflate(R.layout.fragment_media_player,container,false);

        playBtn = (ImageButton)rootView.findViewById(R.id.playBtn);
        pauseBtn = (ImageButton)rootView.findViewById(R.id.pauseBtn);
        prevBtn = (ImageButton)rootView.findViewById(R.id.prevBtn);
        nextBtn = (ImageButton)rootView.findViewById(R.id.nextBtn);

        playBtn.setOnClickListener(this);
        pauseBtn.setOnClickListener(this);
        prevBtn.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        currentTimeTV = (TextView)rootView.findViewById(R.id.currentTime);
        totalTimeTV = (TextView)rootView.findViewById(R.id.totalTime);

        albumNameTV = (TextView)rootView.findViewById(R.id.albumName);
        trackNameTV = (TextView)rootView.findViewById(R.id.trackName);
        artistNameTV = (TextView)rootView.findViewById(R.id.artistName);
        albumCover = (ImageView)rootView.findViewById(R.id.albumCover);

        seekBar = (SeekBar)rootView.findViewById(R.id.seekBar);
        seekBar.setMax(100);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seekPercentage = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarSelected = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarSelected = false;
                mediaPlayer.seekTo(seekPercentage*mediaPlayer.getDuration()/100);
            }
        });

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
                playMedia();
                break;
            case R.id.pauseBtn:
                pauseMedia();
                break;
            case R.id.prevBtn:
                mediaPlayer.stop();
                position--;
                seekPercentage = 0;
                getLoaderManager().restartLoader(MEDIA_PLAYER_LOADER, null, this);
                break;
            case R.id.nextBtn:
                mediaPlayer.stop();
                position++;
                seekPercentage = 0;
                getLoaderManager().restartLoader(MEDIA_PLAYER_LOADER,null,this);
                break;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new CursorLoader(
                getActivity(),
                ArtistEntry.buildArtistTopTracks(artistId),
                MEDIA_PLAYER_COLUMNS,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        String albumImageUrl;
        Uri trackPreviewUri;

        data.moveToPosition(position);

        if(isPreviousTrackAvailable(position)){
            prevBtn.setEnabled(true);
        }
        else {
            prevBtn.setEnabled(false);
        }

        if(isNextTrackAvailable(position,data.getCount())){
            nextBtn.setEnabled(true);
        }
        else {
            nextBtn.setEnabled(false);
        }

        albumNameTV.setText(data.getString(COL_ALBUM_NAME));
        trackNameTV.setText(data.getString(COL_TRACK_NAME));
        artistNameTV.setText(data.getString(COL_ARTIST_NAME));

        albumImageUrl = data.getString(COL_ALBUM_COVER);

        if(!albumImageUrl.equals("")) {
            Picasso.with(getActivity()).load(albumImageUrl).into(albumCover);
        }

        trackPreviewUri = Uri.parse(data.getString(COL_SAMPLE_URL));

        try {
            if(mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(this.getActivity(), trackPreviewUri);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        int seconds;
                        duration = mp.getDuration();
                        seconds = (int) (duration / 1000);
                        totalTimeTV.setText(Tools.formatSecondsToTimeString(seconds));
                        mediaPlayer.seekTo(seekPercentage * mediaPlayer.getDuration() / 100);
                        mediaPlayer.start();
                    }
                });
                mediaPlayer.prepareAsync();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    private void playMedia(){

        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
        playBtn.setVisibility(View.GONE);
        pauseBtn.setVisibility(View.VISIBLE);
    }

    private void pauseMedia(){
        if(mediaPlayer != null){
            mediaPlayer.pause();
            playBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
        }
    }

    private void setActionBarTitleAndSubTitle(ActionBar actionBar, String title, String subTitle){
            actionBar.setTitle(title);
            actionBar.setSubtitle(subTitle);
    }

    private class MediaUIUpdater extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            while(!mediaUIUpdater.isCancelled()){

                publishProgress();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            float currentPosition;
            int currentSeconds;
            int percentage;

            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                    currentPosition = (float) mediaPlayer.getCurrentPosition();
                    currentSeconds = (int) (currentPosition / 1000);

                    if (currentSeconds != lastCurrentSeconds) {
                        currentTimeTV.setText(Tools.formatSecondsToTimeString(currentSeconds));
                        lastCurrentSeconds = currentSeconds;
                    }

                    if (duration > 0) {
                        percentage = (int) (100 * currentPosition / duration);

                        if (!seekBarSelected) {
                            seekBar.setProgress(percentage);
                        }
                    }
                }
            }
            catch (Exception e){
                Log.e("AA","Media Player Illegal State: " + e.toString());
            }
        }
    }

    private boolean isPreviousTrackAvailable(int position){
        return (position > 0);
    }

    private boolean isNextTrackAvailable(int position, int count){
        return !(position >= (count -1));
    }
}

