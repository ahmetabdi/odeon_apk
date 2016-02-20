package uk.co.odeon.androidapp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleDates extends AbstractArray<ScheduleDate> {
    public String errorText;

    public ScheduleDates(JSONArray dateArray) {
        super(dateArray);
        this.errorText = null;
    }

    public ScheduleDates(String errorText) {
        super(null);
        this.errorText = null;
        this.errorText = errorText;
    }

    public ScheduleDate createObject(JSONObject object) {
        return new ScheduleDate(object);
    }
}
