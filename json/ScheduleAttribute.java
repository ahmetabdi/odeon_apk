package uk.co.odeon.androidapp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleAttribute {
    private static final String FIELD_NAME = "attribute";
    private static final String FIELD_PERFORMANCES = "showtimes";
    private final JSONObject jsonObject;
    public final SchedulePerformances performances;

    public ScheduleAttribute(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.performances = new SchedulePerformances(getRawPerformances());
    }

    public String getName() {
        return this.jsonObject.optString(FIELD_NAME);
    }

    private JSONArray getRawPerformances() {
        return this.jsonObject.optJSONArray(FIELD_PERFORMANCES);
    }
}
