package uk.co.odeon.androidapp.sitedistance;

import android.location.Location;

public class SiteDistanceResult {
    public Location location;
    public boolean locationKnown;
    public String postCode;
    public boolean updateWasNecessary;

    public SiteDistanceResult(Location location, String postCode, boolean updateWasNecessary) {
        boolean z = true;
        this.locationKnown = true;
        this.location = null;
        this.postCode = null;
        this.updateWasNecessary = false;
        if (location == null) {
            z = false;
        }
        this.locationKnown = z;
        this.location = location;
        this.postCode = postCode;
        this.updateWasNecessary = updateWasNecessary;
    }

    public SiteDistanceResult(Location location, boolean updateWasNecessary) {
        boolean z = true;
        this.locationKnown = true;
        this.location = null;
        this.postCode = null;
        this.updateWasNecessary = false;
        if (location == null) {
            z = false;
        }
        this.locationKnown = z;
        this.location = location;
        this.updateWasNecessary = updateWasNecessary;
    }
}
