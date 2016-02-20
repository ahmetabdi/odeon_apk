package uk.co.odeon.androidapp.task;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.resphandlers.BookingInitResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class BookingUnselectSeatsTask extends AbstractJSONTask<String, Void, Boolean> {
    private static final String TAG;

    static {
        TAG = BookingUnselectSeatsTask.class.getSimpleName();
    }

    public BookingUnselectSeatsTask(TaskTarget<Boolean> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.BOOKING_URL_UNSELECT_SEATS));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("bookingSessionHash", params[0]));
        nvps.add(new BasicNameValuePair("bookingSessionId", params[1]));
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            logFullURI(TAG, post.getURI().toString(), nvps);
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters for bookingInit API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected TypedResponseHandler<Boolean> createResponseHandler(String... params) {
        return new BookingInitResponseHandler();
    }
}
