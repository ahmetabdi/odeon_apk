package uk.co.odeon.androidapp.updateservice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.provider.FilmContent.FilmColumns;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.resphandlers.AllCinemasResponseHandler;
import uk.co.odeon.androidapp.resphandlers.AppInitResponseHandler;
import uk.co.odeon.androidapp.sitedistance.SiteDistance;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.UpdateServiceSimpleStatus;
import uk.co.odeon.androidapp.updateservice.AbstractUpdateService.WaitThread;
import uk.co.odeon.androidapp.util.http.UriRequestTask;

public class AppInitUpdateService extends AbstractUpdateService<UpdateServiceSimpleStatus> {
    private static final String TAG;
    private boolean dataHashEnabled;

    static {
        TAG = AppInitUpdateService.class.getSimpleName();
    }

    public AppInitUpdateService() {
        super(TAG);
        this.dataHashEnabled = true;
    }

    protected void doHandleIntent(Intent intent) {
        SharedPreferences prefs = ODEONApplication.getInstance().getPrefs();
        if (isInitCallNecessary(prefs) && ODEONApplication.getInstance().hasChoosenLocation()) {
            sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.RUNNING);
            this.waitThread = new WaitThread();
            this.waitThread.start();
            try {
                HttpPost initPost = getAppInitPost(prefs);
                AppInitResponseHandler initRespHandler = new AppInitResponseHandler(getContentResolver(), prefs);
                UriRequestTask uInitTask = new UriRequestTask(initPost, initRespHandler, this);
                uInitTask.run();
                Boolean hasInitChanges = uInitTask.getLastResult();
                if (hasInitChanges == null) {
                    Log.e(TAG, "AppInit failed");
                    sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.FAILED);
                } else if (initRespHandler.msgHeader == null || initRespHandler.msgText == null) {
                    UriRequestTask uSitesTask = new UriRequestTask(getAllCinemasPost(prefs), new AllCinemasResponseHandler(getContentResolver(), prefs), this);
                    uSitesTask.run();
                    Boolean hasSiteChanges = uSitesTask.getLastResult();
                    if (hasSiteChanges == null) {
                        Log.e(TAG, "AppInit failed");
                        sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.FAILED);
                        this.waitThread.interrupt();
                        return;
                    }
                    this.waitThread.interrupt();
                    boolean hasChanges = hasSiteChanges.booleanValue() || hasInitChanges.booleanValue();
                    Log.i(TAG, "AppInit done");
                    sendStatusNotifyBroadcastIntent(hasChanges ? UpdateServiceSimpleStatus.DONE_UPDATED : UpdateServiceSimpleStatus.DONE_NOCHANGES);
                    updateInitTimestamp();
                    SiteDistance.getInstance().calculateSiteDistancesForCurrentLocation();
                    return;
                } else {
                    Log.e(TAG, "AppInit has a message");
                    sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.FAILED, initRespHandler.msgHeader, initRespHandler.msgText);
                }
                this.waitThread.interrupt();
            } catch (Throwable th) {
                this.waitThread.interrupt();
            }
        } else {
            sendStatusNotifyBroadcastIntent(UpdateServiceSimpleStatus.DONE_NOTREQ);
        }
    }

    private HttpPost getAllCinemasPost(SharedPreferences prefs) {
        String dataHash = prefs.getString("dataHashForSites", null);
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_ALLCINEMAS));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        if (this.dataHashEnabled && dataHash != null) {
            nvps.add(new BasicNameValuePair(FilmDetailsColumns.DATA_HASH, dataHash));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters allCinemas API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    private HttpPost getAppInitPost(SharedPreferences prefs) {
        String dataHash = prefs.getString(FilmDetailsColumns.DATA_HASH, null);
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_APPINIT));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        if (this.dataHashEnabled && dataHash != null) {
            nvps.add(new BasicNameValuePair(FilmDetailsColumns.DATA_HASH, dataHash));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters allCinemas API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected boolean isInitCallNecessary(SharedPreferences prefs) {
        boolean isNecessaryByTS;
        Log.i(TAG, "Checking whether a init call is necessary");
        long delta = System.currentTimeMillis() - Long.valueOf(prefs.getLong(Constants.PREF_LASTINIT_TS, 0)).longValue();
        if (delta > Constants.APPINIT_DELAY_REQUIRED) {
            isNecessaryByTS = true;
        } else {
            isNecessaryByTS = false;
        }
        String str = TAG;
        String str2 = "App-Init is%s necessary (by timestamp), last call is %d ms ago";
        Object[] objArr = new Object[2];
        objArr[0] = isNecessaryByTS ? "" : " not";
        objArr[1] = Long.valueOf(delta);
        Log.i(str, String.format(str2, objArr));
        if (isNecessaryByTS) {
            return true;
        }
        Cursor c = null;
        try {
            boolean isNecessaryByData;
            c = getContentResolver().query(FilmColumns.CONTENT_URI, new String[]{"_id"}, null, null, null);
            if (c.getCount() == 0) {
                isNecessaryByData = true;
            } else {
                isNecessaryByData = false;
            }
            if (isNecessaryByData) {
                Log.i(TAG, "Even though by timestamp, the data seems to be fresh, we have 0 rows in the db, so we need to re-try pulling data?!?!!");
            }
            if (!(c == null || c.isClosed())) {
                c.close();
            }
            return isNecessaryByData;
        } catch (Throwable th) {
            if (!(c == null || c.isClosed())) {
                c.close();
            }
        }
    }

    protected void updateInitTimestamp() {
        ODEONApplication.getInstance().getPrefs().edit().putLong(Constants.PREF_LASTINIT_TS, System.currentTimeMillis()).commit();
    }

    public String getStatusNotifiyActionName() {
        return Constants.ACTION_APPINIT_STATUS;
    }
}
