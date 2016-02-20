package uk.co.odeon.androidapp.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import java.util.ArrayList;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.sitedistance.SiteDistance;

public class CinemaMapActivity extends MapActivity {
    protected static final String TAG;
    private Cursor cinemaData;
    private final GeoPoint dublin;
    private MapView mapView;
    private final int maxDistanceForZoomOnCinema;
    private MyLocationOverlay me;
    private final GeoPoint sheffield;
    private ViewGroup viewBubble;

    class CinemaItemizedOverlay extends ItemizedOverlay<OverlayItem> {
        private ArrayList<OverlayItem> overlays;

        /* renamed from: uk.co.odeon.androidapp.activity.CinemaMapActivity.CinemaItemizedOverlay.1 */
        class AnonymousClass1 implements OnClickListener {
            private final /* synthetic */ OverlayItem val$item;

            /* renamed from: uk.co.odeon.androidapp.activity.CinemaMapActivity.CinemaItemizedOverlay.1.1 */
            class AnonymousClass1 implements DialogInterface.OnClickListener {
                private final /* synthetic */ AlertDialog val$alertDialog;

                AnonymousClass1(AlertDialog alertDialog) {
                    this.val$alertDialog = alertDialog;
                }

                public void onClick(DialogInterface dialog, int which) {
                    this.val$alertDialog.hide();
                }
            }

            AnonymousClass1(OverlayItem overlayItem) {
                this.val$item = overlayItem;
            }

            public void onClick(View v) {
                if (CinemaMapActivity.this.getIntent().getData() != null) {
                    Intent i;
                    CinemaOverlayItem cItem = this.val$item;
                    if (cItem.getAddress() == null) {
                        Location location = new Location("gps");
                        float longitude = ((float) this.val$item.getPoint().getLongitudeE6()) / 1000000.0f;
                        location.setLatitude((double) (((float) this.val$item.getPoint().getLatitudeE6()) / 1000000.0f));
                        location.setLongitude((double) longitude);
                        i = new Intent("android.intent.action.VIEW", Uri.parse("google.navigation:q=" + location.getLatitude() + "," + location.getLongitude()));
                    } else {
                        i = new Intent("android.intent.action.VIEW", Uri.parse("google.navigation:q=" + cItem.getAddress()));
                    }
                    try {
                        CinemaMapActivity.this.getParent().startActivity(i);
                        return;
                    } catch (ActivityNotFoundException e) {
                        AlertDialog alertDialog = new Builder(CinemaMapActivity.this.getParent()).create();
                        alertDialog.setTitle(CinemaMapActivity.this.getResources().getString(R.string.google_maps_not_found_title));
                        alertDialog.setMessage(CinemaMapActivity.this.getResources().getString(R.string.google_maps_not_found_msg));
                        alertDialog.setButton(CinemaMapActivity.this.getResources().getString(R.string.google_maps_not_found_ok), new AnonymousClass1(alertDialog));
                        alertDialog.show();
                        return;
                    }
                }
                Intent cinemaDetails = new Intent(CinemaMapActivity.this.getParent(), FilmListActivity.class);
                cinemaDetails.putExtra(Constants.EXTRA_CINEMA_ID, Integer.valueOf(this.val$item.getSnippet()).intValue());
                ((NavigatorBarActivity) CinemaMapActivity.this.getParent()).nextActivity(NavigatorBarActivity.buildNextActivityIntent(cinemaDetails, "Cinema Details"));
            }
        }

        public CinemaItemizedOverlay(Drawable defaultMarker) {
            super(boundCenterBottom(defaultMarker));
            this.overlays = new ArrayList();
            populate();
        }

        protected OverlayItem createItem(int i) {
            return (OverlayItem) this.overlays.get(i);
        }

        public int size() {
            return this.overlays.size();
        }

        public void addOverlay(OverlayItem overlay) {
            this.overlays.add(overlay);
            populate();
        }

        protected boolean onTap(int index) {
            addBubble((OverlayItem) this.overlays.get(index));
            return true;
        }

        private void addBubble(OverlayItem item) {
            if (CinemaMapActivity.this.viewBubble == null) {
                CinemaMapActivity.this.viewBubble = (ViewGroup) CinemaMapActivity.this.getLayoutInflater().inflate(R.layout.cinema_list_bubble, (ViewGroup) CinemaMapActivity.this.mapView.getParent(), false);
                ((TextView) CinemaMapActivity.this.viewBubble.findViewById(R.id.cinema_list_bubble_text)).setText(item.getTitle());
                int offX = -((int) TypedValue.applyDimension(1, 47.0f, CinemaMapActivity.this.getResources().getDisplayMetrics()));
                int i = -2;
                LayoutParams mvlp = new LayoutParams(-2, i, item.getPoint(), offX, -((int) TypedValue.applyDimension(1, 100.0f, CinemaMapActivity.this.getResources().getDisplayMetrics())), 3);
                ((Button) CinemaMapActivity.this.viewBubble.findViewById(R.id.cinema_list_bubble_btn)).setOnClickListener(new AnonymousClass1(item));
                CinemaMapActivity.this.viewBubble.setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        CinemaMapActivity.this.mapView.removeView(CinemaMapActivity.this.viewBubble);
                        CinemaMapActivity.this.viewBubble = null;
                    }
                });
                CinemaMapActivity.this.mapView.addView(CinemaMapActivity.this.viewBubble, mvlp);
            }
        }
    }

    private class CinemaOverlayItem extends OverlayItem {
        private String address;

        public CinemaOverlayItem(GeoPoint point, String title, String snippet, String address) {
            super(point, title, snippet);
            this.address = null;
            setAddress(address);
        }

        public String getAddress() {
            return this.address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    private class MapTouchListener implements OnTouchListener, OnClickListener {
        private MapTouchListener() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            hideBubble();
            return false;
        }

        public void onClick(View v) {
            hideBubble();
        }

        private void hideBubble() {
            if (CinemaMapActivity.this.viewBubble != null) {
                CinemaMapActivity.this.mapView.removeView(CinemaMapActivity.this.viewBubble);
                CinemaMapActivity.this.viewBubble = null;
            }
        }
    }

    public CinemaMapActivity() {
        this.sheffield = new GeoPoint(53382500, -1465450);
        this.dublin = new GeoPoint(53342500, -6265833);
        this.maxDistanceForZoomOnCinema = 20;
        this.me = null;
        this.cinemaData = null;
    }

    static {
        TAG = CinemaMapActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinema_map);
        this.mapView = (MapView) findViewById(R.id.cinema_map_view);
        this.mapView.getOverlays().clear();
        this.me = new MyLocationOverlay(this, this.mapView);
        this.mapView.getOverlays().add(this.me);
        MapTouchListener mt = new MapTouchListener();
        this.mapView.setOnTouchListener(mt);
        this.mapView.setOnClickListener(mt);
        if (getIntent().getData() != null) {
            this.cinemaData = ODEONApplication.getInstance().getCinemaDataCursor((Activity) this, getIntent().getData());
        }
        GeoPoint closestGeoPoint = addCinemaOverlays();
        if (this.cinemaData != null) {
            this.cinemaData = ODEONApplication.getInstance().getCinemaDataCursor((Activity) this, getIntent().getData());
            GeoPoint cinemaPoint = readGeoPointFromSiteCursor(this.cinemaData);
            this.mapView.getController().animateTo(cinemaPoint);
            this.mapView.getController().setZoom(13);
            ((Overlay) this.mapView.getOverlays().get(0)).onTap(cinemaPoint, this.mapView);
        } else if (closestGeoPoint != null) {
            this.mapView.getController().animateTo(closestGeoPoint);
            this.mapView.getController().setZoom(10);
        } else {
            if (ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire)) {
                this.mapView.getController().animateTo(this.dublin);
            } else {
                this.mapView.getController().animateTo(this.sheffield);
            }
            this.mapView.getController().setZoom(7);
        }
    }

    public void onStart() {
        super.onStart();
        EasyTracker.getInstance().activityStart(this);
    }

    public void onStop() {
        super.onStop();
        EasyTracker.getInstance().activityStop(this);
    }

    private GeoPoint readGeoPointFromSiteCursor(Cursor sitesCursor) {
        int latIndex = sitesCursor.getColumnIndex(SiteColumns.LATITUDE);
        int lonIndex = sitesCursor.getColumnIndex(SiteColumns.LONGITUDE);
        String latStr = sitesCursor.getString(latIndex);
        String lonStr = sitesCursor.getString(lonIndex);
        if (latStr == null || lonStr == null) {
            return null;
        }
        return new GeoPoint((int) (Float.valueOf(latStr).floatValue() * 1000000.0f), (int) (1000000.0f * Float.valueOf(lonStr).floatValue()));
    }

    private GeoPoint addCinemaOverlays() {
        boolean freshLocData = SiteDistance.getInstance().isFreshDataAvailableForCurrentLocation();
        CinemaItemizedOverlay cinemaItemizedOverlay = new CinemaItemizedOverlay(getResources().getDrawable(R.drawable.cinema_list_pin));
        Cursor sitesCursor = managedQuery(SiteColumns.CONTENT_URI, null, null, null, null);
        int idIndex = sitesCursor.getColumnIndex("_id");
        int distIndex = sitesCursor.getColumnIndex(SiteColumns.DISTANCE_FROM_GPS);
        int siteNameIndex = sitesCursor.getColumnIndex(SiteColumns.NAME);
        int addrIndex = sitesCursor.getColumnIndex(SiteColumns.ADDR);
        GeoPoint closestGeoPoint = null;
        float closestDistance = 1.0E8f;
        CinemaOverlayItem currentCinemaItem = null;
        int curSiteId = this.cinemaData == null ? -1 : this.cinemaData.getInt(idIndex);
        sitesCursor.moveToFirst();
        while (!sitesCursor.isAfterLast()) {
            int id = -1;
            try {
                id = sitesCursor.getInt(idIndex);
                String siteName = sitesCursor.getString(siteNameIndex);
                String addr = sitesCursor.getString(addrIndex);
                GeoPoint point = readGeoPointFromSiteCursor(sitesCursor);
                if (point != null) {
                    String str;
                    String valueOf = String.valueOf(id);
                    if (addr == null) {
                        str = null;
                    } else {
                        str = addr.replace('\n', ',');
                    }
                    CinemaOverlayItem overlayitem = new CinemaOverlayItem(point, siteName, valueOf, str);
                    cinemaItemizedOverlay.addOverlay(overlayitem);
                    if (id == curSiteId) {
                        currentCinemaItem = overlayitem;
                    }
                    float dist = sitesCursor.getFloat(distIndex);
                    if (dist < closestDistance) {
                        closestGeoPoint = point;
                        closestDistance = dist;
                    }
                    sitesCursor.moveToNext();
                }
            } catch (Throwable e) {
                Log.e(TAG, "Failed to add overlay for site #" + id, e);
            }
        }
        this.mapView.getOverlays().add(cinemaItemizedOverlay);
        if (currentCinemaItem != null) {
            cinemaItemizedOverlay.addBubble(currentCinemaItem);
        }
        return (!freshLocData || closestDistance >= 20.0f) ? null : closestGeoPoint;
    }

    public void onResume() {
        super.onResume();
        this.me.enableMyLocation();
    }

    public void onPause() {
        super.onPause();
        this.me.disableMyLocation();
    }

    protected boolean isRouteDisplayed() {
        return false;
    }
}
