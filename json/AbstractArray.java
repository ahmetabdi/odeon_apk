package uk.co.odeon.androidapp.json;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractArray<T> extends ArrayList<T> {
    public abstract T createObject(JSONObject jSONObject);

    public AbstractArray(JSONArray jsonArray) {
        if (jsonArray != null) {
            for (int n = 0; n < jsonArray.length(); n++) {
                JSONObject o = jsonArray.optJSONObject(n);
                T object;
                if (o == null) {
                    JSONArray a = jsonArray.optJSONArray(n);
                    if (a != null) {
                        for (int p = 0; p < a.length(); p++) {
                            JSONObject nao = a.optJSONObject(p);
                            if (nao != null) {
                                object = createObject(nao);
                                if (object != null) {
                                    add(object);
                                }
                            }
                        }
                    }
                } else if (o.optJSONObject(String.valueOf(n * 3)) != null) {
                    for (int m = 0; m < 3; m++) {
                        JSONObject no = o.optJSONObject(String.valueOf((n * 3) + m));
                        if (no != null) {
                            object = createObject(no);
                            if (object != null) {
                                add(object);
                            }
                        }
                    }
                } else {
                    object = createObject(o);
                    if (object != null) {
                        add(object);
                    }
                }
            }
        }
    }
}
