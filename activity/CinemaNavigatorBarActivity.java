package uk.co.odeon.androidapp.activity;

import android.content.Intent;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;

public class CinemaNavigatorBarActivity extends NavigatorBarActivity {
    protected void registerRootActivity() {
        boolean mapActivityClassFound = true;
        Intent mapIntent = null;
        try {
            mapIntent = new Intent(this, CinemaMapActivity.class);
        } catch (NoClassDefFoundError e) {
            mapActivityClassFound = false;
        }
        if (mapActivityClassFound && ODEONApplication.hasSystemGoogleMapsInstalled(this)) {
            addRootActivities(new RootActivity[]{new RootActivity(this, new Intent(this, CinemaListActivity.class).setAction(Constants.ACTION_CINEMALIST_CLOSETOME), 1, getResources().getString(R.string.cinema_nav_closetome)), new RootActivity(this, new Intent(this, CinemaListActivity.class).setAction(Constants.ACTION_CINEMALIST_AZ), 3, getResources().getString(R.string.cinema_nav_az)), new RootActivity(this, mapIntent, 4, getResources().getString(R.string.cinema_nav_map))});
            return;
        }
        addRootActivities(new RootActivity[]{new RootActivity(this, new Intent(this, CinemaListActivity.class).setAction(Constants.ACTION_CINEMALIST_CLOSETOME), 1, getResources().getString(R.string.cinema_nav_closetome)), new RootActivity(this, new Intent(this, CinemaListActivity.class).setAction(Constants.ACTION_CINEMALIST_AZ), 3, getResources().getString(R.string.cinema_nav_az))});
    }
}
