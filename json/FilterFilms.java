package uk.co.odeon.androidapp.json;

import android.util.Log;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FilterFilms {
    public static final int BINARY_AUDIO_DESCRIBED = 1;
    public static final int BINARY_CLOSE_CAPTURE = 2;
    private static final String TAG;
    public String errorText;
    public SparseIntArray filmAccessibleAdvanced;
    public SparseIntArray filmAccessibleCurrent;
    public SparseIntArray filmAccessibleNext;
    public String[] filmIdsAdvanced;
    public String[] filmIdsCurrent;
    public String[] filmIdsNext;
    private JSONObject jsonObjectAccessibleData;
    private JSONObject jsonObjectFilmIds;

    static {
        TAG = FilterFilms.class.getSimpleName();
    }

    public FilterFilms(JSONObject jsonObjectFilmIds, JSONObject jsonObjectAccessibleData) {
        this.jsonObjectFilmIds = null;
        this.jsonObjectAccessibleData = null;
        this.errorText = null;
        this.filmIdsCurrent = null;
        this.filmIdsNext = null;
        this.filmIdsAdvanced = null;
        this.filmAccessibleCurrent = null;
        this.filmAccessibleNext = null;
        this.filmAccessibleAdvanced = null;
        this.jsonObjectFilmIds = jsonObjectFilmIds;
        this.jsonObjectAccessibleData = jsonObjectAccessibleData;
        initFilmIdsFilter();
        initFilmAccessiblityData();
    }

    public FilterFilms(String errorText) {
        this.jsonObjectFilmIds = null;
        this.jsonObjectAccessibleData = null;
        this.errorText = null;
        this.filmIdsCurrent = null;
        this.filmIdsNext = null;
        this.filmIdsAdvanced = null;
        this.filmAccessibleCurrent = null;
        this.filmAccessibleNext = null;
        this.filmAccessibleAdvanced = null;
        this.errorText = errorText;
    }

    private void initFilmIdsFilter() {
        if (this.jsonObjectFilmIds != null) {
            JSONArray filmKeys = this.jsonObjectFilmIds.names();
            ArrayList<String> filmIdsCurrentList = new ArrayList(filmKeys.length());
            ArrayList<String> filmIdsNextList = new ArrayList(filmKeys.length());
            ArrayList<String> filmIdsAdvancedList = new ArrayList(filmKeys.length());
            int i = 0;
            while (i < filmKeys.length()) {
                try {
                    String filmId = filmKeys.getString(i);
                    JSONObject filmObject = this.jsonObjectFilmIds.getJSONObject(filmKeys.getString(i));
                    if (filmObject.optBoolean("hasTodayPerformance", false)) {
                        filmIdsCurrentList.add(filmId);
                    }
                    if (filmObject.optBoolean("hasNextSevenDaysPerformance", false)) {
                        filmIdsNextList.add(filmId);
                    }
                    if (filmObject.optBoolean("hasAdvancedPerformance", false)) {
                        filmIdsAdvancedList.add(filmId);
                    }
                    i += BINARY_AUDIO_DESCRIBED;
                } catch (JSONException e) {
                    Log.w(TAG, "Could not parse film id for init json films data: " + e, e);
                } catch (NullPointerException e2) {
                    Log.w(TAG, "No film id available for init json films data: " + e2, e2);
                }
            }
            this.filmIdsCurrent = (String[]) filmIdsCurrentList.toArray(new String[filmIdsCurrentList.size()]);
            this.filmIdsNext = (String[]) filmIdsNextList.toArray(new String[filmIdsNextList.size()]);
            this.filmIdsAdvanced = (String[]) filmIdsAdvancedList.toArray(new String[filmIdsAdvancedList.size()]);
            return;
        }
        this.filmIdsCurrent = new String[0];
        this.filmIdsNext = new String[0];
        this.filmIdsAdvanced = new String[0];
    }

    private void initFilmAccessiblityData() {
        this.filmAccessibleCurrent = new SparseIntArray();
        this.filmAccessibleNext = new SparseIntArray();
        this.filmAccessibleAdvanced = new SparseIntArray();
        if (this.jsonObjectAccessibleData != null) {
            JSONArray filmKeys = this.jsonObjectAccessibleData.names();
            int i = 0;
            while (i < filmKeys.length()) {
                try {
                    String filmId = filmKeys.getString(i);
                    JSONObject filmAccessibleObject = this.jsonObjectAccessibleData.getJSONObject(filmKeys.getString(i));
                    int accessibleBinaryCurrent = 0;
                    int accessibleBinaryNext = 0;
                    int accessibleBinaryAdvanced = 0;
                    JSONObject filmAccessibleCurrentObject = filmAccessibleObject.optJSONObject("today");
                    JSONObject filmAccessibleNextObject = filmAccessibleObject.optJSONObject("nextSevenDays");
                    JSONObject filmAccessibleAdvancedObject = filmAccessibleObject.optJSONObject("advanced");
                    if (this.filmAccessibleCurrent != null) {
                        if (filmAccessibleCurrentObject.optBoolean("hasAudiDescribed", false)) {
                            accessibleBinaryCurrent = 0 + BINARY_AUDIO_DESCRIBED;
                        }
                        if (filmAccessibleCurrentObject.optBoolean("hasCloseCaption", false)) {
                            accessibleBinaryCurrent += BINARY_CLOSE_CAPTURE;
                        }
                    }
                    if (this.filmAccessibleNext != null) {
                        if (filmAccessibleNextObject.optBoolean("hasAudiDescribed", false)) {
                            accessibleBinaryNext = 0 + BINARY_AUDIO_DESCRIBED;
                        }
                        if (filmAccessibleNextObject.optBoolean("hasCloseCaption", false)) {
                            accessibleBinaryNext += BINARY_CLOSE_CAPTURE;
                        }
                    }
                    if (this.filmAccessibleAdvanced != null) {
                        if (filmAccessibleAdvancedObject.optBoolean("hasAudiDescribed", false)) {
                            accessibleBinaryAdvanced = 0 + BINARY_AUDIO_DESCRIBED;
                        }
                        if (filmAccessibleAdvancedObject.optBoolean("hasCloseCaption", false)) {
                            accessibleBinaryAdvanced += BINARY_CLOSE_CAPTURE;
                        }
                    }
                    if (accessibleBinaryCurrent > 0) {
                        this.filmAccessibleCurrent.put(Integer.parseInt(filmId), accessibleBinaryCurrent);
                    }
                    if (accessibleBinaryNext > 0) {
                        this.filmAccessibleNext.put(Integer.parseInt(filmId), accessibleBinaryNext);
                    }
                    if (accessibleBinaryAdvanced > 0) {
                        this.filmAccessibleAdvanced.put(Integer.parseInt(filmId), accessibleBinaryAdvanced);
                    }
                    i += BINARY_AUDIO_DESCRIBED;
                } catch (JSONException e) {
                    Log.w(TAG, "Could not parse film id for accessible films data: " + e, e);
                    return;
                } catch (NullPointerException e2) {
                    Log.w(TAG, "No film id available for accessible films data: " + e2, e2);
                    return;
                }
            }
        }
    }
}
