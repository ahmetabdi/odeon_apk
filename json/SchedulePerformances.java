package uk.co.odeon.androidapp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class SchedulePerformances extends AbstractArray<SchedulePerformance> {
    public SchedulePerformances(JSONArray performanceArray) {
        super(performanceArray);
    }

    public SchedulePerformance createObject(JSONObject object) {
        return new SchedulePerformance(object);
    }
}
