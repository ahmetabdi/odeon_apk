package uk.co.odeon.androidapp.util.http;

import android.net.Uri;
import java.io.IOException;
import org.apache.http.HttpResponse;

public interface ResponseHandler {
    boolean handleResponse(HttpResponse httpResponse, Uri uri) throws IOException;
}
