package uk.co.odeon.androidapp.activity;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.sitedistance.SiteDistance;

public class MainTabActivity extends TabActivity {
    public boolean onSearchRequested() {
        return false;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!ODEONApplication.getInstance().checkAndPerformEntryPointOverwrite()) {
            setContentView(R.layout.tabs);
            TabHost tabHost = getTabHost();
            View tabViewFilms = createTabView(tabHost.getContext(), "Films", getResources().getDrawable(R.drawable.tab_image_film_selector));
            tabHost.addTab(tabHost.newTabSpec("films").setIndicator(tabViewFilms).setContent(new Intent().setClass(this, FilmNavigatorBarActivity.class)));
            View tabViewCinemas = createTabView(tabHost.getContext(), "Cinemas", getResources().getDrawable(R.drawable.tab_image_cinema_selector));
            tabHost.addTab(tabHost.newTabSpec("cinemas").setIndicator(tabViewCinemas).setContent(new Intent().setClass(this, CinemaNavigatorBarActivity.class)));
            View tabViewRewards = createTabView(tabHost.getContext(), "Rewards", getResources().getDrawable(R.drawable.tab_image_rewards_selector));
            tabHost.addTab(tabHost.newTabSpec(Constants.ACTION_REWARDS).setIndicator(tabViewRewards).setContent(new Intent().setClass(this, RewardsNavigatorBarActivity.class)));
            View tabViewMyOdeon = createTabView(tabHost.getContext(), "MyOdeon", getResources().getDrawable(R.drawable.tab_image_myodeon_selector));
            tabHost.addTab(tabHost.newTabSpec("myodeon").setIndicator(tabViewMyOdeon).setContent(new Intent().setClass(this, MyOdeonNavigatorBarActivity.class)));
            if (!ODEONApplication.getInstance().hasChoosenLocation()) {
                startActivityForResult(new Intent(this, LocationChooseActivity.class), Constants.REQUEST_CODE_FINISH_CHECK);
            }
        }
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
        SiteDistance.getInstance().suspendLocationUpdates();
    }

    private static View createTabView(Context context, String text, Drawable icon) {
        View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
        ((TextView) view.findViewById(R.id.tabsText)).setText(text);
        ((ImageView) view.findViewById(R.id.tabsImage)).setImageDrawable(icon);
        return view;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_FINISH_CHECK && resultCode == Constants.TICKET_LIST_RELOAD) {
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settingsMenuItem:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.aboutMenuItem:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.helpMenuItem:
                ODEONApplication.trackEvent("Config HELP", "Click", "");
                Intent webviewIntent = new Intent(this, WebviewActivity.class);
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE, getString(R.string.help_header_title));
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_URL, Constants.formatLocationUrl(Constants.URL_HELP));
                startActivity(webviewIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
