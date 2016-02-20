package uk.co.odeon.androidapp.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import java.io.File;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.task.TaskTarget;
import uk.co.odeon.androidapp.task.TwitterTask;
import uk.co.odeon.androidapp.util.drawable.DrawableManager;

public class TwitterActivity extends AbstractODEONBaseActivity implements TaskTarget<Pair<Boolean, Exception>> {
    private static final String CALLBACKURL = "odeonandroidapp://tweet";
    private static final String CONSUMERKEY = "Dj2Z7UejWHdGNImQLDTrg";
    private static final String CONSUMERSECRET = "WcpDfFt99DgvfHxpwpphOxyM0sW4X6G7037meb9bII";
    protected static final String TAG;
    public static final int loadingImageRes = 2130837555;
    public static final int unavailableImageRes = 2130837555;
    protected Cursor filmData;
    protected int filmId;
    protected File filmImage;
    protected String filmUrl;
    private RequestToken requestToken;
    protected int tweetMaxChars;
    private Twitter twitter;

    /* renamed from: uk.co.odeon.androidapp.activity.TwitterActivity.2 */
    class AnonymousClass2 implements OnClickListener {
        private final /* synthetic */ AlertDialog val$alertDialog;

        AnonymousClass2(AlertDialog alertDialog) {
            this.val$alertDialog = alertDialog;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$alertDialog.dismiss();
        }
    }

    public TwitterActivity() {
        this.filmId = 0;
        this.filmData = null;
        this.filmImage = null;
        this.filmUrl = null;
        this.tweetMaxChars = 100;
    }

    static {
        TAG = TwitterActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.twitter);
        configureNavigationHeader();
        if (this.filmId <= 0) {
            this.filmId = getIntent().getIntExtra(Constants.EXTRA_FILM_ID, 0);
        }
        this.filmData = ODEONApplication.getInstance().getFilmDataCursor(this, this.filmId);
        OAuthLogin();
        EditText tweetText = (EditText) findViewById(R.id.tweetText);
        if (tweetText != null) {
            tweetText.append(new StringBuilder(String.valueOf(this.filmData.getString(this.filmData.getColumnIndex(OfferColumns.TITLE)))).append(" looks...").toString());
            tweetText.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == 66) {
                        return true;
                    }
                    return false;
                }
            });
        }
        ImageView imageView = (ImageView) findViewById(R.id.tweetImage);
        if (imageView != null) {
            String imageURL = this.filmData.getString(this.filmData.getColumnIndex(OfferColumns.IMAGE_URL));
            DrawableManager dm = DrawableManager.getInstance();
            this.filmImage = dm.buildImageCacheFileBasedOnURLFilename(imageURL);
            dm.loadDrawable(imageURL, imageView, this.filmImage, R.drawable.film_info_noimg, R.drawable.film_info_noimg);
        }
        this.filmUrl = new StringBuilder(String.valueOf(ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? "http://www.odeoncinemas.ie" : "http://www.odeon.co.uk")).append("/fanatic/film_info/m").append(this.filmData.getInt(this.filmData.getColumnIndex("_id"))).toString();
    }

    void OAuthLogin() {
        try {
            this.twitter = new TwitterFactory().getInstance();
            this.twitter.setOAuthConsumer(CONSUMERKEY, CONSUMERSECRET);
            this.requestToken = this.twitter.getOAuthRequestToken(CALLBACKURL);
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse(this.requestToken.getAuthenticationURL())).setFlags(1610612740));
        } catch (Exception e) {
            showAlertDialog(e);
        }
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try {
            String verifier = intent.getData().getQueryParameter("oauth_verifier");
            if (verifier != null) {
                this.twitter.getOAuthAccessToken(this.requestToken, verifier);
                try {
                    this.tweetMaxChars = (140 - this.twitter.getAPIConfiguration().getCharactersReservedPerMedia()) - this.filmUrl.length();
                    EditText tweetText = (EditText) findViewById(R.id.tweetText);
                    if (tweetText != null) {
                        Log.d(TAG, "Twitter max text length: " + this.tweetMaxChars);
                        tweetText.setFilters(new InputFilter[]{new LengthFilter(this.tweetMaxChars)});
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Failed to set max chars in edit text: " + e.getMessage(), e);
                }
                ViewSwitcher viewSwitcher = (ViewSwitcher) findViewById(R.id.twitterViewSwitcher);
                while (viewSwitcher.getCurrentView().getId() != R.id.tweetLayout) {
                    viewSwitcher.showNext();
                }
                return;
            }
            finish();
        } catch (Exception e2) {
            showAlertDialog(e2);
        }
    }

    public void setTaskResult(Pair<Boolean, Exception> taskResult) {
        hideProgress(true);
        if (taskResult != null && ((Boolean) taskResult.first).booleanValue()) {
            finish();
        } else if (taskResult == null || ((Boolean) taskResult.first).booleanValue()) {
            showAlertDialog(new TwitterException("Unknown error on twitter task"));
        } else {
            showAlertDialog((Exception) taskResult.second);
        }
    }

    private void showAlertDialog(Exception e) {
        Log.e(TAG, "Failed to communicate with Twitter: " + e.getMessage(), e);
        AlertDialog alertDialog = new Builder(this).create();
        alertDialog.setTitle(getResources().getString(R.string.twitter_title));
        alertDialog.setMessage(getResources().getString(R.string.twitter_msg));
        alertDialog.setButton(getResources().getString(R.string.twitter_ok), new AnonymousClass2(alertDialog));
        alertDialog.show();
    }

    protected void configureNavigationHeader() {
        TextView navigationHeaderTitle = (TextView) findViewById(R.id.navigationHeaderTitle);
        if (navigationHeaderTitle != null) {
            navigationHeaderTitle.setText(getResources().getString(R.string.twitter_header_title));
        }
        Button backButton = (Button) findViewById(R.id.navigationHeaderCancel);
        if (backButton != null) {
            backButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    TwitterActivity.this.finish();
                }
            });
        }
        configureNavigationHeaderNext("Done", new View.OnClickListener() {
            public void onClick(View v) {
                EditText tweetText = (EditText) TwitterActivity.this.findViewById(R.id.tweetText);
                if (tweetText != null) {
                    Log.d(TwitterActivity.TAG, "Twitter text length: " + tweetText.getText().length());
                    if (tweetText.getText().length() <= 0 || tweetText.getText().length() > TwitterActivity.this.tweetMaxChars) {
                        tweetText.setError(TwitterActivity.this.getString(R.string.error_form_invalid_length, new Object[]{TwitterActivity.this.getString(R.string.form_field_twitter_text), Integer.valueOf(1), Integer.valueOf(TwitterActivity.this.tweetMaxChars)}));
                        return;
                    }
                    TwitterActivity.this.showProgress(R.string.twitter_progress, true);
                    new TwitterTask(TwitterActivity.this).execute(new Object[]{new StringBuilder(String.valueOf(tweetText.getText().toString())).append(TwitterActivity.this.filmUrl).toString(), TwitterActivity.this.filmImage, TwitterActivity.this.twitter});
                }
            }
        });
    }
}
