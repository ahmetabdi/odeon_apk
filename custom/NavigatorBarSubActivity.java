package uk.co.odeon.androidapp.custom;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.AbstractODEONBaseActivity;

public class NavigatorBarSubActivity extends AbstractODEONBaseActivity {
    protected void startSubActivity(Intent intent, String title) {
        if (getParent() instanceof NavigatorBarActivity) {
            ((NavigatorBarActivity) getParent()).nextActivity(NavigatorBarActivity.buildNextActivityIntent(intent, title));
            return;
        }
        startActivity(intent);
    }

    protected void inflateCustomHeader(View view) {
        if (getParent() instanceof NavigatorBarActivity) {
            ((NavigatorBarActivity) getParent()).inflateCustomHeader(view);
        } else if (findViewById(16908290).getRootView() instanceof ViewGroup) {
            ((ViewGroup) findViewById(16908290).getRootView()).addView(view, 0);
        }
    }

    public void onBackPressed() {
        if (getParent() instanceof NavigatorBarActivity) {
            getParent().onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    public void onRecycled() {
    }

    protected void onPause() {
        super.onPause();
        hideExtraButton();
    }

    protected void hideExtraButton() {
        if (getParent() != null && (getParent() instanceof NavigatorBarActivity)) {
            Button extraButton = (Button) ((NavigatorBarActivity) getParent()).findViewById(R.id.headerSubButtonExtra);
            if (extraButton != null && getClass().getName().equals(extraButton.getTag())) {
                extraButton.setVisibility(8);
                extraButton.setText("");
                extraButton.setOnClickListener(null);
                extraButton.setTag(null);
            }
        }
    }

    protected boolean configureExtraNavButton() {
        boolean z = false;
        if (getParent() != null && (getParent() instanceof NavigatorBarActivity)) {
            NavigatorBarActivity navBar = (NavigatorBarActivity) getParent();
            z = configureExtraNavButton(navBar);
            if (z) {
                ((Button) navBar.findViewById(R.id.headerSubButtonExtra)).setTag(getClass().getName());
            }
        }
        return z;
    }

    protected boolean configureExtraNavButton(NavigatorBarActivity navBar) {
        return false;
    }
}
