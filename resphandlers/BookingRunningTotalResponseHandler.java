package uk.co.odeon.androidapp.resphandlers;

import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingRunningTotalElement;
import uk.co.odeon.androidapp.model.BookingRunningTotalElement.Type;
import uk.co.odeon.androidapp.model.BookingRunningTotalRow;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;
import uk.co.odeon.androidapp.util.http.AbstractTypedJSONResponseHandler;

public class BookingRunningTotalResponseHandler extends AbstractTypedJSONResponseHandler<ArrayList<BookingRunningTotalRow>> {
    private static final String TAG;

    static {
        TAG = BookingRunningTotalResponseHandler.class.getSimpleName();
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        if (jsonObj != null) {
            JSONObject config = jsonObj.optJSONObject("config");
            if (config == null) {
                Log.e(TAG, "Received JSON object without config.");
                return false;
            }
            Log.d(TAG, "Received config: " + config);
            JSONArray data = jsonObj.optJSONArray("data");
            JSONObject dataObject = jsonObj.optJSONObject("data");
            if (data == null && dataObject == null) {
                Log.e(TAG, "Received JSON object without data.");
                return false;
            }
            Object obj;
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder("Received data: ");
            if (data != null) {
                obj = data;
            } else {
                JSONObject jSONObject = dataObject;
            }
            Log.d(str, stringBuilder.append(obj).toString());
            if (config.optString("action") == null || !config.optString("action").equals("bookingError")) {
                try {
                    setResult(jsonDataToModel(data));
                    return true;
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to select seats. JSON object was not parsable.", e);
                }
            } else if (dataObject.optString("errorText") != null) {
                BookingProcess bookingProcess = BookingProcess.getInstance();
                bookingProcess.setLastError(dataObject.optString("errorText"));
                Log.e(TAG, "Error received by JSON config with message: " + bookingProcess.getLastError());
                return false;
            } else {
                Log.e(TAG, "Error received by JSON config but no error message available.");
                return false;
            }
        }
        Log.e(TAG, "Received JSON object is NULL.");
        return false;
    }

    protected ArrayList<BookingRunningTotalRow> jsonDataToModel(JSONArray data) throws JSONException {
        ArrayList<BookingRunningTotalRow> rows = new ArrayList();
        for (int n = 0; n < data.length(); n++) {
            JSONArray rowData = data.optJSONArray(n);
            BookingRunningTotalRow row = new BookingRunningTotalRow();
            row.runningTotalElements = new ArrayList();
            for (int m = 0; m < rowData.length(); m++) {
                JSONObject elementData = rowData.optJSONObject(m);
                if (!(elementData.optString("type") == null || elementData.optJSONObject("data") == null)) {
                    BookingRunningTotalElement element = new BookingRunningTotalElement();
                    element.type = Type.valueOf(elementData.optString("type"));
                    element.text = elementData.optJSONObject("data").optString("value");
                    switch (elementData.optJSONObject("data").optInt("align")) {
                        case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                            element.gravity = 1;
                            break;
                        case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                            element.gravity = 5;
                            break;
                        default:
                            element.gravity = 3;
                            break;
                    }
                    String font = elementData.optJSONObject("data").optString("font");
                    boolean z = font != null && font.toLowerCase().indexOf("bold") >= 0;
                    element.bold = z;
                    element.action = elementData.optJSONObject("data").optString("action");
                    row.runningTotalElements.add(element);
                }
            }
            rows.add(row);
        }
        return rows;
    }
}
