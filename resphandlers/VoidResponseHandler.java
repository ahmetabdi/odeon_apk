package uk.co.odeon.androidapp.resphandlers;

import android.net.Uri;
import java.io.IOException;
import org.apache.http.HttpResponse;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class VoidResponseHandler implements TypedResponseHandler<Void> {
    public Void getResult() {
        return null;
    }

    public boolean handleResponse(HttpResponse response, Uri uri) throws IOException {
        return true;
    }
}
