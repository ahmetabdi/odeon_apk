package uk.co.odeon.androidapp.task;

import android.util.Log;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.json.Rewards;

public class RewardsTaskCached extends RewardsTask {
    private static final String TAG;
    private static String cachedForEmail;
    private static Rewards cachedResult;
    private static long cachedTS;

    static {
        TAG = RewardsTask.class.getSimpleName();
        cachedResult = null;
        cachedForEmail = null;
        cachedTS = 0;
    }

    public RewardsTaskCached(TaskTarget<Rewards> target) {
        super(target);
    }

    protected Rewards doInBackground(String... params) {
        boolean cacheOutDated;
        boolean useCache = true;
        String emailParam = params[0];
        Log.i(TAG, "Requesting rewards for " + emailParam);
        if (cachedTS == 0 || System.currentTimeMillis() - cachedTS > Constants.REWARDS_TASK_CACHE_TTL) {
            cacheOutDated = true;
        } else {
            cacheOutDated = false;
        }
        if (cacheOutDated || cachedResult == null || !emailParam.equalsIgnoreCase(cachedForEmail)) {
            useCache = false;
        }
        if (useCache) {
            Log.i(TAG, "Using cached data");
            return cachedResult;
        }
        Log.i(TAG, "No data in cache or cache is expired, reading data via JSON");
        cachedResult = (Rewards) super.doInBackground(params);
        cachedForEmail = emailParam;
        cachedTS = System.currentTimeMillis();
        return cachedResult;
    }

    public static void clearCache() {
        cachedResult = null;
        cachedForEmail = null;
        cachedTS = 0;
    }
}
