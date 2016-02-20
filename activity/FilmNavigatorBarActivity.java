package uk.co.odeon.androidapp.activity;

import android.content.Intent;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;

public class FilmNavigatorBarActivity extends NavigatorBarActivity {
    protected void registerRootActivity() {
        addRootActivities(new RootActivity[]{new RootActivity(this, new Intent(this, FilmListActivity.class).setAction("top5nowBooking"), 1, "Top Films"), new RootActivity(this, new Intent(this, FilmListActivity.class).setAction("allFilms"), 2, "Films A-Z"), new RootActivity(this, new Intent(this, FilmListActivity.class).setAction("comingSoon"), 3, "Coming Soon")});
    }
}
