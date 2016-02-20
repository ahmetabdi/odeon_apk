package uk.co.odeon.androidapp.util.http;

import android.net.Uri;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class AbstractJSONResponseHandler implements ResponseHandler {
    private static final String TAG;

    public abstract boolean handleJSONRepsonse(JSONObject jSONObject, Uri uri);

    static {
        TAG = AbstractJSONResponseHandler.class.getSimpleName();
    }

    public boolean handleResponse(HttpResponse response, Uri uri) throws IOException {
        Log.d(TAG, "Streaming JSON");
        String jsonStr = convertStreamToString(response.getEntity().getContent());
        Log.d(TAG, "Parsing JSON");
        try {
            JSONObject jsonObj = new JSONObject(jsonStr);
            Log.d(TAG, "Parsed JSON, now calling real handler");
            return handleJSONRepsonse(jsonObj, uri);
        } catch (JSONException e) {
            throw new IOException("Failed to process JSON response: " + e.getMessage());
        }
    }

    public String convertStreamToString(InputStream is) throws IOException {
        if (is == null) {
            return "";
        }
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while (true) {
                int n = reader.read(buffer);
                if (n == -1) {
                    break;
                }
                writer.write(buffer, 0, n);
            }
            return writer.toString();
        } finally {
            is.close();
        }
    }
}
