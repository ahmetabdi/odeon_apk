package uk.co.odeon.androidapp.dialog;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.analytics.tracking.android.ModelFields;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;

public class TellFriendAboutFilmDialog extends ODEONBaseDialog {
    public static final int MSG_CODE_TWITTER = 1;
    protected static final String TAG;
    private OnClickListener cancelClickListener;
    private OnClickListener emailClickListener;
    private OnClickListener facebookClickListener;
    private int filmId;
    private Handler handler;
    private OnClickListener twitterClickListener;

    /* renamed from: uk.co.odeon.androidapp.dialog.TellFriendAboutFilmDialog.6 */
    class AnonymousClass6 implements DialogInterface.OnClickListener {
        private final /* synthetic */ AlertDialog val$alertDialog;

        AnonymousClass6(AlertDialog alertDialog) {
            this.val$alertDialog = alertDialog;
        }

        public void onClick(DialogInterface dialog, int which) {
            this.val$alertDialog.hide();
            TellFriendAboutFilmDialog.this.dismiss();
        }
    }

    private class Data {
        public String bracketCert;
        public String htmlPlot;
        public String htmlTitle;
        public String imageURL;
        public String imageURLBig;
        public String link;
        public String rawCert;
        public String rawPlot;
        public String rawTitle;

        public Data(int filmId) {
            Cursor filmCursor = TellFriendAboutFilmDialog.this.getContext().getContentResolver().query(ContentUris.withAppendedId(FilmColumns.CONTENT_URI, (long) filmId), null, null, null, null);
            try {
                filmCursor.moveToFirst();
                this.rawTitle = filmCursor.getString(filmCursor.getColumnIndex(OfferColumns.TITLE));
                this.htmlTitle = Html.toHtml(new SpannableString(this.rawTitle));
                this.rawCert = filmCursor.getString(filmCursor.getColumnIndex(FilmColumns.CERTIFICATE));
                String str = (this.rawCert == null || "".equals(this.rawCert)) ? "" : "(" + this.rawCert + ")";
                this.bracketCert = str;
                this.imageURL = filmCursor.getString(filmCursor.getColumnIndex(OfferColumns.IMAGE_URL));
                Cursor filmDetailsCursor = TellFriendAboutFilmDialog.this.getContext().getContentResolver().query(ContentUris.withAppendedId(FilmDetailsColumns.CONTENT_URI, (long) filmId), null, null, null, null);
                try {
                    filmDetailsCursor.moveToFirst();
                    this.rawPlot = filmDetailsCursor.getString(filmDetailsCursor.getColumnIndex(FilmDetailsColumns.PLOT));
                    this.htmlPlot = Html.toHtml(new SpannableString(this.rawPlot));
                    this.imageURLBig = filmDetailsCursor.getString(filmDetailsCursor.getColumnIndex(OfferColumns.IMAGE_URL));
                    this.link = new StringBuilder(String.valueOf(ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? "http://www.odeoncinemas.ie" : "http://www.odeon.co.uk")).append("/fanatic/film_info/m").append(filmId).toString();
                } finally {
                    filmDetailsCursor.close();
                }
            } finally {
                filmCursor.close();
            }
        }
    }

    static {
        TAG = TellFriendAboutFilmDialog.class.getSimpleName();
    }

    public TellFriendAboutFilmDialog(Context ctx, int filmId) {
        super(ctx);
        this.handler = null;
        this.emailClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Tell friends about film Syndication", "Click", "Email A Friend");
                TellFriendAboutFilmDialog.this.sendEmail();
                TellFriendAboutFilmDialog.this.dismiss();
            }
        };
        this.facebookClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Tell friends about film Syndication", "Click", "Facebook Share");
                TellFriendAboutFilmDialog.this.sendViaFacebook();
                TellFriendAboutFilmDialog.this.dismiss();
            }
        };
        this.twitterClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Tell friends about film Syndication", "Click", "Twitter Share");
                if (TellFriendAboutFilmDialog.this.handler != null) {
                    Message message = new Message();
                    message.what = TellFriendAboutFilmDialog.MSG_CODE_TWITTER;
                    TellFriendAboutFilmDialog.this.handler.sendMessage(message);
                }
                TellFriendAboutFilmDialog.this.dismiss();
            }
        };
        this.cancelClickListener = new OnClickListener() {
            public void onClick(View v) {
                TellFriendAboutFilmDialog.this.dismiss();
            }
        };
        this.filmId = filmId;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(MSG_CODE_TWITTER);
        setContentView(R.layout.tell_friend_about_film_dialog);
        findViewById(R.id.tellFriendBtnEmail).setOnClickListener(this.emailClickListener);
        findViewById(R.id.tellFriendBtnFacebook).setOnClickListener(this.facebookClickListener);
        findViewById(R.id.tellFriendBtnTwitter).setOnClickListener(this.twitterClickListener);
        findViewById(R.id.tellFriendBtnCancel).setOnClickListener(this.cancelClickListener);
    }

    public void sendViaFacebook() {
        try {
            Data d = new Data(this.filmId);
            JSONObject mediaObject = new JSONObject();
            mediaObject.put("type", "image");
            mediaObject.put("src", d.imageURLBig);
            mediaObject.put("href", d.link);
            JSONArray media = new JSONArray();
            media.put(mediaObject);
            JSONObject attachment = new JSONObject();
            attachment.put(SiteColumns.NAME, d.rawTitle);
            attachment.put("href", d.link);
            attachment.put("caption", "ODEON");
            attachment.put(ModelFields.DESCRIPTION, d.rawPlot);
            attachment.put("media", media);
            JSONObject linkObject = new JSONObject();
            linkObject.put(OfferColumns.TEXT, "Details");
            linkObject.put("href", d.link);
            JSONArray links = new JSONArray();
            links.put(linkObject);
            Bundle params = new Bundle();
            params.putString("attachment", attachment.toString());
            params.putString("action_links", links.toString());
            new Facebook(Constants.FACEBOOK_APP_ID).dialog(getContext(), "stream.publish", params, new DialogListener() {
                public void onFacebookError(FacebookError e) {
                    Toast.makeText(TellFriendAboutFilmDialog.this.getContext(), R.string.tellfriend_fb_error, 0).show();
                }

                public void onError(DialogError e) {
                    Toast.makeText(TellFriendAboutFilmDialog.this.getContext(), R.string.tellfriend_fb_error, 0).show();
                }

                public void onComplete(Bundle values) {
                    Toast.makeText(TellFriendAboutFilmDialog.this.getContext(), R.string.tellfriend_fb_success, 0).show();
                }

                public void onCancel() {
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.tellfriend_fb_error, 0).show();
        }
    }

    private void sendEmail() {
        String emailText = getEmailText();
        Intent emailIntent = new Intent("android.intent.action.SEND");
        emailIntent.setType("text/html");
        emailIntent.putExtra("android.intent.extra.TEXT", Html.fromHtml(emailText));
        emailIntent.putExtra("android.intent.extra.SUBJECT", getContext().getResources().getString(R.string.tellfriend_email_subject));
        try {
            getContext().startActivity(emailIntent);
        } catch (Throwable e) {
            Log.e(TAG, "Failed to send Mail: " + e.getMessage(), e);
            AlertDialog alertDialog = new Builder(getContext()).create();
            alertDialog.setTitle(getContext().getResources().getString(R.string.tellfriend_email_err_title));
            alertDialog.setMessage(getContext().getResources().getString(R.string.tellfriend_email_err_text));
            alertDialog.setButton(getContext().getResources().getString(R.string.tellfriend_email_err_ok), new AnonymousClass6(alertDialog));
            alertDialog.show();
        }
    }

    private String getEmailText() {
        Data d = new Data(this.filmId);
        String domain = ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? "odeoncinemas.ie" : "odeon.co.uk";
        return getContext().getResources().getString(R.string.tellfriend_email_text, new Object[]{domain, d.htmlTitle, d.bracketCert, d.htmlPlot, d.link});
    }
}
