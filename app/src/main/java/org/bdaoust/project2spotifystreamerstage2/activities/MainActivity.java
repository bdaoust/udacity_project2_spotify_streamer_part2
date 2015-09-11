package org.bdaoust.project2spotifystreamerstage2.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import org.bdaoust.project2spotifystreamerstage2.utils.MyKeys;
import org.bdaoust.project2spotifystreamerstage2.R;
import org.bdaoust.project2spotifystreamerstage2.fragments.ArtistSearchFragment;
import org.bdaoust.project2spotifystreamerstage2.fragments.TopTracksFragment;


public class MainActivity extends AppCompatActivity implements ArtistSearchFragment.OnArtistListChangeListener{

    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.top_tracks_container) != null){

            twoPane = true;

            if (savedInstanceState == null){
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;

                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.top_tracks_container,new TopTracksFragment());
                fragmentTransaction.commit();
            }
        }
        else {
            twoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public void onItemSelected(long artistId) {
        if(twoPane){
            loadDetailFragment(artistId);
        }
        else {
            Intent intent;

            intent = new Intent(this,TopTracksActivity.class);
            intent.putExtra(MyKeys.ARTIST_ID_KEY, artistId);

            startActivity(intent);
        }
    }

    @Override
    public void onSearchTermChanged(String searchTerm) {
        if(twoPane){
            loadDetailFragment(-1);
        }
    }

    private void loadDetailFragment(long artistId){
        Bundle args;
        TopTracksFragment topTracksFragment;
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        args = new Bundle();
        args.putLong(MyKeys.ARTIST_ID_KEY, artistId);

        topTracksFragment = new TopTracksFragment();
        topTracksFragment.setArguments(args);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.top_tracks_container, topTracksFragment);
        fragmentTransaction.commit();
    }
}
