package uk.co.odeon.androidapp.json;

import org.json.JSONArray;
import org.json.JSONObject;

public class ScheduleAttributes extends AbstractArray<ScheduleAttribute> {
    public ScheduleAttributes(JSONArray attributeArray) {
        super(attributeArray);
    }

    public ScheduleAttribute createObject(JSONObject object) {
        return new ScheduleAttribute(object);
    }
}
