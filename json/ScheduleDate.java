package uk.co.odeon.androidapp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleDate {
    private static final String FIELD_ATTRIBUTES = "attributes";
    private static final String FIELD_NAME = "date";
    public final ScheduleAttributes attributes;
    private final JSONObject jsonObject;

    public ScheduleDate(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
        this.attributes = new ScheduleAttributes(getRawAttributes());
    }

    public String getName() {
        return this.jsonObject.optString(FIELD_NAME);
    }

    private JSONArray getRawAttributes() {
        return this.jsonObject.optJSONArray(FIELD_ATTRIBUTES);
    }
}
