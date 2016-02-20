package uk.co.odeon.androidapp.updateservice;

import android.content.ContentUris;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.provider.FilmContent;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.resphandlers.FilmDetailResponseHandler;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.UpdateServiceSimpleStatus;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.WaitThread;
import uk.co.odeon.androidapp.util.http.UriRequestTask;

public class FilmDetailService extends AbstractUpdateService<UpdateServiceSimpleStatus> {
    private static final String TAG;
    private static final UriMatcher sUriMatcher;

    static {
        TAG = FilmDetailService.class.getSimpleName();
        sUriMatcher = new UriMatcher(-1);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "filmdetails/#", 1);
        sUriMatcher.addURI(FilmContent.AUTHORITY, "films/#", 2);
    }

    public FilmDetailService() {
        super(TAG);
    }

    public String getStatusNotifiyActionName() {
        return Constants.ACTION_FILMDETAIL_STATUS;
    }

    protected String getDataHash(Uri filmDetailsUri) {
        Cursor filmDetails = null;
        try {
            filmDetails = getContentResolver().query(filmDetailsUri, new String[]{FilmDetailsColumns.DATA_HASH}, null, null, null);
            if (filmDetails.getCount() == 0) {
                Log.d(TAG, "Film details data not yet found: " + filmDetailsUri + " --> no dataHash");
                return null;
            }
            filmDetails.moveToFirst();
            String string = filmDetails.getString(filmDetails.getColumnIndex(FilmDetailsColumns.DATA_HASH));
            if (filmDetails == null || filmDetails.isClosed()) {
                return string;
            }
            filmDetails.close();
            return string;
        } finally {
            if (!(filmDetails == null || filmDetails.isClosed())) {
                filmDetails.close();
            }
        }
    }

    protected HttpPost getFilmDetailsPOST(Uri filmDetailsUri, int filmId) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_FILMDETAILS));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("m", String.valueOf(filmId)));
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        nvps.add(new BasicNameValuePair(FilmDetailsColumns.DATA_HASH, getDataHash(filmDetailsUri)));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters form film details API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected void doHandleIntent(Intent intent) {
        if (intent.getData() == null) {
            Log.e(TAG, "No film URL specified");
            sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.FAILED);
        } else if (sUriMatcher.match(intent.getData()) == -1) {
            Log.e(TAG, "Invalid film URL specified: " + intent.getData());
            sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.FAILED.setURI(intent.getData()));
        } else {
            Integer filmId = Integer.valueOf((String) intent.getData().getPathSegments().get(1));
            Uri filmDetailsUri = ContentUris.withAppendedId(FilmDetailsColumns.CONTENT_URI, (long) filmId.intValue());
            if (isAPICallNecessary(filmDetailsUri)) {
                sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.RUNNING);
                this.waitThread = new WaitThread();
                this.waitThread.start();
                UriRequestTask uTask = new UriRequestTask(getFilmDetailsPOST(filmDetailsUri, filmId.intValue()), new FilmDetailResponseHandler(getContentResolver(), filmDetailsUri), this);
                uTask.run();
                boolean hasChanges = uTask.getLastResult(false).booleanValue();
                this.waitThread.interrupt();
                Log.i(TAG, "FilmDetailService done");
                sendStatusNotifyBroadcastIntent(hasChanges ? UpdateServiceSimpleStatus.DONE_UPDATED : UpdateServiceSimpleStatus.DONE_NOCHANGES);
                return;
            }
            sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.DONE_NOTREQ.setURI(intent.getData()));
        }
    }

    protected boolean isAPICallNecessary(Uri filmDetailsUri) {
        Cursor c = null;
        try {
            c = getContentResolver().query(filmDetailsUri, new String[]{FilmDetailsColumns.LAST_UPDATE_TS}, null, null, null);
            if (c.getCount() == 0) {
                return true;
            }
            c.moveToFirst();
            long delta = System.currentTimeMillis() - c.getLong(c.getColumnIndex(FilmDetailsColumns.LAST_UPDATE_TS));
            if (delta > Constants.FILM_DETAIL_DELAY_REFRESH_REQUIRED) {
                Log.i(TAG, "Last update of film details is " + delta + "ms ago, we will ask for a refresh");
                if (!(c == null || c.isClosed())) {
                    c.close();
                }
                return true;
            }
            Log.i(TAG, "Last update of film details is " + delta + "ms ago, no refresh necessary");
            if (!(c == null || c.isClosed())) {
                c.close();
            }
            return false;
        } finally {
            if (!(c == null || c.isClosed())) {
                c.close();
            }
        }
    }
}
