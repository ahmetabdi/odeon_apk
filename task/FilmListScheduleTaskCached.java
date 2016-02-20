package uk.co.odeon.androidapp.task;

import android.util.Log;
import java.util.HashMap;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.json.FilterFilms;

public class FilmListScheduleTaskCached extends FilmListScheduleTask {
    private static final String TAG;
    private static HashMap<String, FilterFilms> cachedResults;
    private static HashMap<String, Long> cachedTSs;

    static {
        TAG = RewardsTask.class.getSimpleName();
        cachedResults = new HashMap();
        cachedTSs = new HashMap();
    }

    public FilmListScheduleTaskCached(TaskTarget<FilterFilms> target) {
        super(target);
    }

    protected FilterFilms doInBackground(String... params) {
        String siteParam = params[0];
        Log.i(TAG, "Requesting film list schedule for " + siteParam);
        long cachedTS = cachedTSs.containsKey(siteParam) ? ((Long) cachedTSs.get(siteParam)).longValue() : 0;
        boolean cacheOutDated = cachedTS == 0 || System.currentTimeMillis() - cachedTS > Constants.FILM_LIST_SCHEDULE_TASK_CACHE_TTL;
        boolean useCache = !cacheOutDated && cachedResults.containsKey(siteParam);
        if (useCache) {
            Log.i(TAG, "Using cached data");
            return (FilterFilms) cachedResults.get(siteParam);
        }
        if (!cachedResults.containsKey(siteParam)) {
            ODEONApplication.getInstance().removeSiteSpecificDatahashFromPreferences(siteParam, Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO, Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO_SITES);
        }
        Log.i(TAG, "No data in cache or cache is expired, reading data via JSON");
        String dataHashParam = ODEONApplication.getInstance().getPrefs().getString(new StringBuilder(Constants.PREFS_DATAHASH_ADD_SCHEDULE_INFO).append(siteParam).toString(), null);
        FilterFilms result = (FilterFilms) super.doInBackground(siteParam, dataHashParam);
        if (result != null) {
            cachedResults.put(siteParam, result);
            cachedTSs.put(siteParam, Long.valueOf(System.currentTimeMillis()));
            return result;
        } else if (cachedResults.containsKey(siteParam)) {
            return (FilterFilms) cachedResults.get(siteParam);
        } else {
            return null;
        }
    }

    public static void clearCache() {
        cachedResults = new HashMap();
        cachedTSs = new HashMap();
    }
}
