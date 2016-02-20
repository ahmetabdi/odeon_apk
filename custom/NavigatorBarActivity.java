package uk.co.odeon.androidapp.custom;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.util.ArrayList;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public abstract class NavigatorBarActivity extends ActivityGroup {
    private static final String EXTRA_TITLE = "title";
    private static final String TAG;
    private OnClickListener buttonClickListener;
    private RootActivity[] rootActivities;
    private int rootActivityIndex;
    private ArrayList<SubActivity> subActivities;

    /* renamed from: uk.co.odeon.androidapp.custom.NavigatorBarActivity.2 */
    class AnonymousClass2 implements Runnable {
        private final /* synthetic */ View val$wd;

        AnonymousClass2(View view) {
            this.val$wd = view;
        }

        public void run() {
            Log.v(NavigatorBarActivity.TAG, "Focusing view " + (this.val$wd.requestFocus() ? "sucessful" : "failed"));
        }
    }

    public class RootActivity {
        public static final int TYPE_CENTRE = 2;
        public static final int TYPE_LEFT = 1;
        public static final int TYPE_RIGHT = 3;
        public static final int TYPE_SINGLE = 4;
        public String activityId;
        public boolean hidden;
        public Intent intent;
        public String label;
        final /* synthetic */ NavigatorBarActivity this$0;
        public int type;

        public RootActivity(NavigatorBarActivity navigatorBarActivity, Intent intent, int type, String label) {
            boolean z = true;
            this.this$0 = navigatorBarActivity;
            this.activityId = null;
            this.intent = null;
            this.type = TYPE_LEFT;
            this.label = "";
            this.hidden = false;
            this.activityId = navigatorBarActivity.buildRandomActivityId(intent);
            this.intent = intent;
            this.type = type;
            this.label = label;
            if (label != null) {
                z = false;
            }
            this.hidden = z;
        }
    }

    public class SubActivity {
        public String activityId;
        public Intent intent;

        public SubActivity(String activityId, Intent intent) {
            this.activityId = null;
            this.intent = null;
            this.activityId = activityId;
            this.intent = intent;
        }
    }

    protected abstract void registerRootActivity();

    static {
        TAG = NavigatorBarActivity.class.getSimpleName();
    }

    public NavigatorBarActivity() {
        super(false);
        this.rootActivityIndex = 0;
        this.subActivities = new ArrayList(5);
        this.buttonClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (v.getId() == R.id.headerSubButtonBack) {
                    NavigatorBarActivity.this.previousActivity();
                    return;
                }
                NavigatorBarActivity.this.rootActivityIndex = ((ViewGroup) v.getParent()).indexOfChild(v);
                NavigatorBarActivity.this.changeContentView(NavigatorBarActivity.this.rootActivities[NavigatorBarActivity.this.rootActivityIndex].activityId, NavigatorBarActivity.this.rootActivities[NavigatorBarActivity.this.rootActivityIndex].intent);
            }
        };
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigator_bar);
        registerRootActivity();
        changeContentView(this.rootActivities[this.rootActivityIndex].activityId, this.rootActivities[this.rootActivityIndex].intent);
        findViewById(R.id.headerSubButtonBack).setOnClickListener(this.buttonClickListener);
    }

    public void addRootActivities(RootActivity[] roots) {
        int i = 0;
        this.rootActivityIndex = 0;
        this.rootActivities = roots;
        LinearLayout headerRoot = (LinearLayout) findViewById(R.id.headerRoot);
        RootActivity[] rootActivityArr = this.rootActivities;
        int length = rootActivityArr.length;
        while (i < length) {
            RootActivity rootActivity = rootActivityArr[i];
            headerRoot.addView(buildRootButton(rootActivity.label, rootActivity.hidden));
            i++;
        }
    }

    private Button buildRootButton(String title, boolean hidden) {
        Button button = (Button) getLayoutInflater().inflate(R.layout.navigation_bar_button, null);
        button.setWidth((int) TypedValue.applyDimension(1, 100.0f, getResources().getDisplayMetrics()));
        if (hidden) {
            button.setVisibility(8);
        } else {
            button.setText(title);
            button.setOnClickListener(this.buttonClickListener);
        }
        return button;
    }

    private void previousActivity() {
        if (!isRootActivity()) {
            Intent previousActivity = this.subActivities.size() > 1 ? ((SubActivity) this.subActivities.get(this.subActivities.size() - 2)).intent : this.rootActivities[this.rootActivityIndex].intent;
            String previousActivityId = this.subActivities.size() > 1 ? ((SubActivity) this.subActivities.get(this.subActivities.size() - 2)).activityId : this.rootActivities[this.rootActivityIndex].activityId;
            String currentActivityId = ((SubActivity) this.subActivities.get(this.subActivities.size() - 1)).activityId;
            this.subActivities.remove(this.subActivities.size() - 1);
            changeContentView(previousActivityId, previousActivity);
            getLocalActivityManager().destroyActivity(currentActivityId, true);
        }
    }

    public void nextActivity(Intent i) {
        SubActivity subActivity = new SubActivity(buildRandomActivityId(i), i);
        this.subActivities.add(subActivity);
        changeContentView(subActivity.activityId, subActivity.intent);
    }

    public static Intent buildNextActivityIntent(Intent intent, String title) {
        intent.putExtra(EXTRA_TITLE, title);
        return intent;
    }

    private String buildRandomActivityId(Intent intent) {
        return new StringBuilder(String.valueOf(intent.getComponent().getClassName())).append("_").append(intent.getAction()).append("_").append((int) (Math.random() * 10000.0d)).toString();
    }

    public boolean isRootActivity() {
        return this.subActivities.size() <= 0;
    }

    public boolean isRootActivity(Intent intent) {
        if (this.subActivities.size() <= 0) {
            return true;
        }
        return intent.equals(((SubActivity) this.subActivities.get(0)).intent);
    }

    public synchronized void changeContentView(String activityId, Intent i) {
        View wd = null;
        synchronized (this) {
            Window w;
            Log.d(TAG, "Changing ContentView to activityId " + activityId);
            if (!isRootActivity()) {
                Button extraButton = (Button) findViewById(R.id.headerSubButtonExtra);
                if (extraButton != null) {
                    extraButton.setVisibility(8);
                    extraButton.setText("");
                    extraButton.setOnClickListener(null);
                }
            }
            LocalActivityManager mgr = getLocalActivityManager();
            Activity startedActivity = mgr.getActivity(activityId);
            if (startedActivity != null) {
                Log.d(TAG, "Recycling activity #" + activityId);
                w = startedActivity.getWindow();
            } else {
                Log.d(TAG, "Starting new activity #" + activityId);
                w = mgr.startActivity(activityId, i);
            }
            if (w != null) {
                wd = w.getDecorView();
            }
            if (wd != null) {
                ViewGroup navigatorContent = (ViewGroup) findViewById(R.id.navigatorContent);
                runOnUiThread(new AnonymousClass2(wd));
                hideKeyboard(navigatorContent);
                if (wd.getParent() != null) {
                    navigatorContent.removeAllViews();
                }
                navigatorContent.addView(wd);
                wd.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_from_right));
                if (navigatorContent.getChildCount() > 1) {
                    navigatorContent.removeViewAt(0);
                }
                ViewSwitcher headerSwitcher = (ViewSwitcher) findViewById(R.id.headerSwitcher);
                if (!isRootActivity()) {
                    String obj = (i.getExtras() == null || i.getExtras().get(EXTRA_TITLE) == null) ? "" : i.getExtras().get(EXTRA_TITLE).toString();
                    setSubActivityTitle(obj);
                    if (!(headerSwitcher == null || headerSwitcher.getCurrentView() == null || headerSwitcher.getCurrentView().getId() != R.id.headerRoot)) {
                        headerSwitcher.showNext();
                    }
                } else if (!(headerSwitcher == null || headerSwitcher.getCurrentView() == null || headerSwitcher.getCurrentView().getId() != R.id.headerSub)) {
                    headerSwitcher.showPrevious();
                }
                handleButtonSelection();
                if (startedActivity != null && (startedActivity instanceof NavigatorBarSubActivity)) {
                    ((NavigatorBarSubActivity) startedActivity).onRecycled();
                }
            }
        }
    }

    protected void changeContentView(ViewGroup navigatorContent, View wd, Intent i) {
    }

    public void registerExtraButton(String label, OnClickListener onClickListener) {
        Button extraButton = (Button) findViewById(R.id.headerSubButtonExtra);
        if (extraButton != null) {
            extraButton.setVisibility(0);
            extraButton.setText(label);
            extraButton.setOnClickListener(onClickListener);
        }
    }

    private void handleButtonSelection() {
        LinearLayout headerRoot = (LinearLayout) findViewById(R.id.headerRoot);
        int i = 0;
        while (i < this.rootActivities.length) {
            int drawableId;
            switch (this.rootActivities[i].type) {
                case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    drawableId = i == this.rootActivityIndex ? R.drawable.nav_bar_btn_right_square_hilite : R.drawable.nav_bar_btn_right_square;
                    break;
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    drawableId = i == this.rootActivityIndex ? R.drawable.nav_bar_btn_4_square_hilite : R.drawable.nav_bar_btn_4_square;
                    break;
                case RootActivity.TYPE_RIGHT /*3*/:
                    drawableId = i == this.rootActivityIndex ? R.drawable.nav_bar_btn_left_square_hilite : R.drawable.nav_bar_btn_left_square;
                    break;
                case RootActivity.TYPE_SINGLE /*4*/:
                    if (i == this.rootActivityIndex) {
                        drawableId = R.drawable.nav_bar_btn_4_round_hilite;
                    } else {
                        drawableId = R.drawable.nav_bar_btn_4_round;
                    }
                    break;
                default:
                    if (i != this.rootActivityIndex) {
                        drawableId = R.drawable.nav_bar_btn_4_round;
                        break;
                    } else {
                        drawableId = R.drawable.nav_bar_btn_4_round_hilite;
                        break;
                    }
            }
            ((Button) headerRoot.getChildAt(i)).setBackgroundDrawable(getResources().getDrawable(drawableId));
            i++;
        }
    }

    private void setSubActivityTitle(String title) {
        ((TextView) findViewById(R.id.headerSubTitle)).setText(title);
    }

    public void inflateCustomHeader(View view) {
        ((LinearLayout) findViewById(R.id.headerRoot)).addView(view);
    }

    public void onBackPressed() {
        if (isRootActivity()) {
            super.onBackPressed();
        } else {
            previousActivity();
        }
    }

    private void hideKeyboard(View v) {
        ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
