package uk.co.odeon.androidapp.sitedistance;

import uk.co.odeon.androidapp.task.AbstractBaseTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class SiteDistanceTask extends AbstractBaseTask<Void, Integer, SiteDistanceResult> {
    private static Boolean alreadyRunning;

    static {
        alreadyRunning = Boolean.valueOf(false);
    }

    public SiteDistanceTask(TaskTarget<SiteDistanceResult> target) {
        super(target);
    }

    protected synchronized SiteDistanceResult doInBackground(Void... params) {
        SiteDistanceResult calculateSiteDistancesForCurrentLocation;
        try {
            alreadyRunning = Boolean.valueOf(true);
            calculateSiteDistancesForCurrentLocation = SiteDistance.getInstance().calculateSiteDistancesForCurrentLocation();
            alreadyRunning = Boolean.valueOf(false);
        } catch (Throwable th) {
            alreadyRunning = Boolean.valueOf(false);
        }
        return calculateSiteDistancesForCurrentLocation;
    }

    public static boolean isRunning() {
        return alreadyRunning.booleanValue();
    }
}
