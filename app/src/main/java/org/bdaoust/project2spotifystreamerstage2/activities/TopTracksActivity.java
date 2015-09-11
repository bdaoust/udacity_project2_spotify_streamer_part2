package org.bdaoust.project2spotifystreamerstage2.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.bdaoust.project2spotifystreamerstage2.utils.MyKeys;
import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.fragments.TopTracksFragment;

public class TopTracksActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top_tracks);

        if(savedInstanceState == null) {
            FragmentManager fragmentManager;
            FragmentTransaction fragmentTransaction;
            TopTracksFragment topTracksFragment;
            long artistId;
            Bundle args;

            artistId = getIntent().getLongExtra(MyKeys.ARTIST_ID_KEY,0);

            args = new Bundle();
            args.putLong(MyKeys.ARTIST_ID_KEY,artistId);

            topTracksFragment = new TopTracksFragment();
            topTracksFragment.setArguments(args);

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.top_tracks_container, topTracksFragment);
            fragmentTransaction.commit();
        }
    }
}
