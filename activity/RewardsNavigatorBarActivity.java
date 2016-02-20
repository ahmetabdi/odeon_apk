package uk.co.odeon.androidapp.activity;

import android.content.Intent;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;

public class RewardsNavigatorBarActivity extends NavigatorBarActivity {
    protected void registerRootActivity() {
        addRootActivities(new RootActivity[]{new RootActivity(this, new Intent(this, RewardsActivity.class), 1, null)});
    }
}
