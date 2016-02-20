package uk.co.odeon.androidapp.sitedistance;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.provider.DatabaseHelper;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;

public class SiteDistance implements LocationListener {
    private static final String TAG;
    private static SiteDistance instance;
    private LocationManager locationManager;
    private Boolean locationUpdateSuspended;
    private ArrayList<Handler> notificationHandlers;
    protected final ODEONApplication odeonApplication;
    private HashMap<String, Object> postCodeLocks;
    private String provider;
    private HandlerThread updateListenerThread;

    /* renamed from: uk.co.odeon.androidapp.sitedistance.SiteDistance.1 */
    class AnonymousClass1 implements Runnable {
        private final /* synthetic */ String[] val$providersToListenFor;

        AnonymousClass1(String[] strArr) {
            this.val$providersToListenFor = strArr;
        }

        public void run() {
            for (String providerToListenFor : this.val$providersToListenFor) {
                Log.d(SiteDistance.TAG, "Requesting location updates for provider: " + providerToListenFor);
                try {
                    SiteDistance.this.locationManager.requestLocationUpdates(providerToListenFor, 300000, Constants.SITEDIST_LOCUPDATE_MINDIST_METERS, SiteDistance.this);
                } catch (IllegalArgumentException e) {
                    Log.w(SiteDistance.TAG, "Requesting location updates for provider failed: " + e.toString(), e);
                }
            }
        }
    }

    static {
        TAG = SiteDistance.class.getSimpleName();
    }

    private SiteDistance() {
        this.updateListenerThread = null;
        this.postCodeLocks = new HashMap();
        this.notificationHandlers = new ArrayList();
        this.locationUpdateSuspended = Boolean.valueOf(false);
        this.odeonApplication = ODEONApplication.getInstance();
        this.locationManager = (LocationManager) ODEONApplication.getInstance().getSystemService("location");
        switchToBestProvider();
    }

    public static SiteDistance getInstance() {
        if (instance == null) {
            instance = new SiteDistance();
        } else {
            instance.activateLocationUpdates();
        }
        return instance;
    }

    public SiteDistanceResult calculateSiteDistancesForPostCode(String postCode, Location location) {
        synchronized (this.postCodeLocks) {
            Object lock = this.postCodeLocks.get(postCode);
            if (lock == null) {
                lock = new Object();
                this.postCodeLocks.put(postCode, lock);
            }
        }
        synchronized (lock) {
            try {
                if (isUpdateNecessary(location, postCode, true)) {
                    updateSiteDistancesForLocation(location, postCode);
                    this.postCodeLocks.remove(postCode);
                    return new SiteDistanceResult(location, postCode, true);
                }
                Log.i(TAG, "No recalculation of distances necessary");
                SiteDistanceResult siteDistanceResult = new SiteDistanceResult(location, postCode, false);
                this.postCodeLocks.remove(postCode);
                return siteDistanceResult;
            } catch (Throwable th) {
                this.postCodeLocks.remove(postCode);
            }
        }
    }

    public synchronized SiteDistanceResult calculateSiteDistancesForCurrentLocation() {
        SiteDistanceResult siteDistanceResult;
        if (this.provider == null) {
            Log.d(TAG, "No active location provider, not calculating distances for current location, provider: " + this.provider);
            siteDistanceResult = new SiteDistanceResult(null, true);
        } else {
            Location location = this.locationManager.getLastKnownLocation(this.provider);
            if (location == null) {
                Log.d(TAG, "No location found, not calculating distances for current location, provider: " + this.provider);
                siteDistanceResult = new SiteDistanceResult(null, true);
            } else if (isUpdateNecessary(location, null, true)) {
                updateSiteDistancesForLocation(location, null);
                siteDistanceResult = new SiteDistanceResult(location, true);
            } else {
                Log.i(TAG, "No recalculation of distances necessary");
                siteDistanceResult = new SiteDistanceResult(location, false);
            }
        }
        return siteDistanceResult;
    }

    public boolean isLocationKnown() {
        if (this.provider == null || this.locationManager.getLastKnownLocation(this.provider) == null) {
            return false;
        }
        return true;
    }

    public boolean isUpdateNecessary(Location cmpLoc, String postCode, boolean checkTS) {
        SharedPreferences prefs = ODEONApplication.getInstance().getPrefs();
        Log.d(TAG, "NECESSARY? " + cmpLoc + " - " + postCode);
        boolean isPostCode = postCode != null;
        if (checkTS) {
            long lastTS = prefs.getLong(isPostCode ? Constants.PREF_SITEDIST_POSTCODE_LASTTS : Constants.PREF_SITEDIST_LASTTS, 0);
            Log.d(TAG, "LASTTS: " + lastTS);
            if (lastTS == 0) {
                return true;
            }
            long deltaTS = System.currentTimeMillis() - lastTS;
            Log.d(TAG, "DELTATS: " + deltaTS);
            if (deltaTS > Constants.SITEDIST_DELAY_REQUIRED) {
                return true;
            }
        }
        String lastLocStr = prefs.getString(isPostCode ? Constants.PREF_SITEDIST_LAST_POSTCODE_LOC : Constants.PREF_SITEDIST_LASTLOC, null);
        Log.d(TAG, "LAST LOC: " + lastLocStr);
        if (lastLocStr == null) {
            return true;
        }
        Location lastLoc = str2loc(lastLocStr);
        double distance = distance(cmpLoc, lastLoc, this.odeonApplication.getChoosenLocation());
        Log.d(TAG, "LAST LOC2: " + lastLoc + " DIST " + distance);
        return distance > 250.0d;
    }

    public boolean isFreshDataAvailableForCurrentLocation() {
        return isFreshDataAvailableForCurrentLocation(true);
    }

    public boolean isFreshDataAvailableForCurrentLocation(boolean checkTS) {
        if (this.provider == null) {
            return false;
        }
        Location location = this.locationManager.getLastKnownLocation(this.provider);
        if (location == null || isUpdateNecessary(location, null, checkTS)) {
            return false;
        }
        return true;
    }

    protected String loc2str(Location loc) {
        return loc.getLatitude() + ":" + loc.getLongitude();
    }

    protected Location str2loc(String str) {
        Location loc = new Location("siteDist");
        StringTokenizer tok = new StringTokenizer(str, ":");
        String latStr = tok.nextToken();
        String lonStr = tok.nextToken();
        loc.setLatitude(Double.valueOf(latStr).doubleValue());
        loc.setLongitude(Double.valueOf(lonStr).doubleValue());
        return loc;
    }

    protected void updatePrefs(Location cmpLoc, String postCode) {
        boolean isPostCode = postCode != null;
        ODEONApplication.getInstance().getPrefs().edit().putString(isPostCode ? Constants.PREF_SITEDIST_LAST_POSTCODE_LOC : Constants.PREF_SITEDIST_LASTLOC, loc2str(cmpLoc)).putLong(isPostCode ? Constants.PREF_SITEDIST_POSTCODE_LASTTS : Constants.PREF_SITEDIST_LASTTS, System.currentTimeMillis()).commit();
    }

    protected void updateSiteDistancesForLocation(Location cmpLoc, String postCode) {
        Log.i(TAG, "Updating distances for cmpLoc=" + cmpLoc + ", postCode=" + postCode);
        String[] proj = new String[]{"Site._id", SiteColumns.LATITUDE, SiteColumns.LONGITUDE};
        Cursor c = null;
        DatabaseHelper dbh = DatabaseHelper.getInstance(ODEONApplication.getInstance());
        dbh.getWritableDatabase().beginTransaction();
        c = ODEONApplication.getInstance().getContentResolver().query(SiteColumns.CONTENT_URI, proj, null, null, null);
        int latColIdx = c.getColumnIndex(SiteColumns.LATITUDE);
        int lonColIdx = c.getColumnIndex(SiteColumns.LONGITUDE);
        int idColIdx = c.getColumnIndex("_id");
        c.moveToFirst();
        while (!c.isAfterLast()) {
            int id = c.getInt(idColIdx);
            String latStr = c.getString(latColIdx);
            String lonStr = c.getString(lonColIdx);
            Location siteLoc = null;
            try {
                double lat = Double.valueOf(latStr).doubleValue();
                double lon = Double.valueOf(lonStr).doubleValue();
                Location location = new Location(this.provider);
                try {
                    location.setLatitude(lat);
                    location.setLongitude(lon);
                    siteLoc = location;
                } catch (Throwable th) {
                    siteLoc = location;
                    Log.w(TAG, "Invalid lat/lon for site #" + id + " lat=" + latStr + " lon=" + lonStr);
                    if (siteLoc != null) {
                        try {
                            updateSiteDistanceForLocation(id, cmpLoc, siteLoc, postCode);
                        } finally {
                            if (!(c == null || c.isClosed())) {
                                c.close();
                            }
                            if (dbh.getWritableDatabase().inTransaction()) {
                                dbh.getWritableDatabase().endTransaction();
                            }
                            postUpdateNotifiyMessageToAllHandlers();
                        }
                    }
                    c.moveToNext();
                }
            } catch (Throwable th2) {
                Log.w(TAG, "Invalid lat/lon for site #" + id + " lat=" + latStr + " lon=" + lonStr);
                if (siteLoc != null) {
                    updateSiteDistanceForLocation(id, cmpLoc, siteLoc, postCode);
                }
                c.moveToNext();
            }
            if (siteLoc != null) {
                updateSiteDistanceForLocation(id, cmpLoc, siteLoc, postCode);
            }
            c.moveToNext();
        }
        dbh.getWritableDatabase().setTransactionSuccessful();
        updatePrefs(cmpLoc, postCode);
    }

    protected void updateSiteDistanceForLocation(int siteId, Location cmpLoc, Location siteLoc, String postCode) {
        Log.d(TAG, "Updating distance for site " + siteId + ", cmpLoc=" + loc2str(cmpLoc) + ", siteLoc=" + loc2str(siteLoc) + ", postCode=" + postCode);
        double distance = distance(cmpLoc, siteLoc, this.odeonApplication.getChoosenLocation());
        String distCol = postCode != null ? SiteColumns.DISTANCE_FROM_POSTCODE : SiteColumns.DISTANCE_FROM_GPS;
        Log.d(TAG, "Distance is " + distance + ", updating site, column " + distCol);
        ContentValues cv = new ContentValues();
        cv.put(distCol, Double.valueOf(distance));
        ODEONApplication.getInstance().getContentResolver().update(ContentUris.withAppendedId(SiteColumns.CONTENT_URI, (long) siteId), cv, null, null);
    }

    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged " + this.provider + " " + location);
        calculateSiteDistancesForCurrentLocation();
    }

    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled " + provider);
        switchToBestProvider();
    }

    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled " + provider);
        switchToBestProvider();
    }

    protected void switchToBestProvider() {
        String newProvider = this.locationManager.getBestProvider(new Criteria(), true);
        Log.d(TAG, "New best provider is: " + newProvider + " old provider is: " + this.provider);
        if (newProvider == null) {
            Log.d(TAG, "No active location provider :(");
            if (this.provider != null || this.updateListenerThread == null) {
                setupLocationListener(new String[]{"network", "gps"});
            }
            this.provider = null;
        } else if (!newProvider.equals(this.provider) || this.updateListenerThread == null) {
            this.provider = newProvider;
            setupLocationListener(new String[]{this.provider});
            postUpdateNotifiyMessageToAllHandlers();
        } else {
            Log.d(TAG, "Already bound to best provider");
        }
    }

    protected void setupLocationListener(String[] providersToListenFor) {
        if (this.updateListenerThread != null) {
            Log.d(TAG, "Detaching loction updates/thread from old provider");
            this.locationManager.removeUpdates(this);
            Looper looper = this.updateListenerThread.getLooper();
            if (looper != null) {
                looper.quit();
            }
        }
        Log.d(TAG, "Attaching location updates thread");
        this.updateListenerThread = new HandlerThread("GPS Thread");
        this.updateListenerThread.start();
        new Handler(this.updateListenerThread.getLooper()).post(new AnonymousClass1(providersToListenFor));
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged " + provider + " #" + status);
        postUpdateNotifiyMessageToAllHandlers();
    }

    public void addNotificationHandler(Handler h) {
        synchronized (this.notificationHandlers) {
            if (!this.notificationHandlers.contains(h)) {
                this.notificationHandlers.add(h);
            }
        }
    }

    public void removeNotificationHandler(Handler h) {
        synchronized (this.notificationHandlers) {
            if (!this.notificationHandlers.contains(h)) {
                this.notificationHandlers.remove(h);
            }
        }
    }

    public synchronized void clearNotificationHandler() {
        this.notificationHandlers.clear();
    }

    public synchronized void countNotificationHandlers() {
        this.notificationHandlers.clear();
    }

    protected void postUpdateNotifiyMessageToAllHandlers() {
        Message msg = new Message();
        msg.what = Constants.SITEDIST_MSG_NOTIFY;
        msg.obj = this.provider == null ? null : this.locationManager.getLastKnownLocation(this.provider);
        postMessageToAllHandlers(msg);
    }

    protected void postMessageToAllHandlers(Message msg) {
        synchronized (this.notificationHandlers) {
            Iterator it = this.notificationHandlers.iterator();
            while (it.hasNext()) {
                Handler h = (Handler) it.next();
                try {
                    Message msgClone = new Message();
                    msgClone.copyFrom(msg);
                    h.sendMessage(msgClone);
                } catch (Throwable e) {
                    Log.w(TAG, "Failed to send update message to notification handler", e);
                }
            }
        }
    }

    public void activateLocationUpdates() {
        synchronized (this.locationUpdateSuspended) {
            if (this.locationUpdateSuspended.booleanValue()) {
                Log.d(TAG, "Attaching loction updates/thread because location listener should run again");
                instance.switchToBestProvider();
                this.locationUpdateSuspended = Boolean.valueOf(false);
            }
        }
    }

    public void suspendLocationUpdates() {
        synchronized (this.locationUpdateSuspended) {
            if (!this.locationUpdateSuspended.booleanValue()) {
                Log.d(TAG, "Detaching loction updates/thread because location listener should be closed");
                if (this.locationManager != null) {
                    this.locationManager.removeUpdates(this);
                }
                if (this.updateListenerThread != null) {
                    Looper looper = this.updateListenerThread.getLooper();
                    if (looper != null) {
                        looper.quit();
                    }
                }
                this.locationUpdateSuspended = Boolean.valueOf(true);
            }
        }
    }

    protected double distance(Location loc1, Location loc2, APP_LOCATION location) {
        return distance(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude(), location);
    }

    protected double distance(double lat1, double lon1, double lat2, double lon2, APP_LOCATION location) {
        double dist = (60.0d * rad2deg(Math.acos((Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))) + ((Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))) * Math.cos(deg2rad(lon1 - lon2)))))) * 1.1515d;
        if (APP_LOCATION.ire.equals(location)) {
            return dist / 0.621371192d;
        }
        return dist;
    }

    protected double deg2rad(double deg) {
        return (3.141592653589793d * deg) / 180.0d;
    }

    protected double rad2deg(double rad) {
        return (180.0d * rad) / 3.141592653589793d;
    }
}
