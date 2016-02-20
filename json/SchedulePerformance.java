package uk.co.odeon.androidapp.json;

import org.json.JSONObject;

public class SchedulePerformance {
    private static final String FIELD_ID = "performanceId";
    private static final String FIELD_TIME = "performanceTime";
    private final JSONObject jsonObject;

    public SchedulePerformance(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public String getID() {
        return this.jsonObject.optString(FIELD_ID);
    }

    public String getTime() {
        return this.jsonObject.optString(FIELD_TIME);
    }
}
