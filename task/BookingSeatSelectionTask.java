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
import uk.co.odeon.androidapp.model.BookingRunningTotalRow;
import uk.co.odeon.androidapp.resphandlers.BookingRunningTotalResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class BookingSeatSelectionTask extends AbstractJSONTask<String, Void, ArrayList<BookingRunningTotalRow>> {
    private static final String TAG;

    static {
        TAG = BookingSeatSelectionTask.class.getSimpleName();
    }

    public BookingSeatSelectionTask(TaskTarget<ArrayList<BookingRunningTotalRow>> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.BOOKING_URL_SELECT_SEATS));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("bookingSessionHash", params[0]));
        nvps.add(new BasicNameValuePair("bookingSessionId", params[1]));
        nvps.add(new BasicNameValuePair("activeSection", params[2]));
        if (params[3] != null && params[3].length() > 0) {
            nvps.add(new BasicNameValuePair("seats", params[3]));
        }
        nvps.add(new BasicNameValuePair("tickets", params[4]));
        if (params[5] != null && params[5].length() > 0) {
            nvps.add(new BasicNameValuePair("hasCEACard", params[5]));
        }
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            logFullURI(TAG, post.getURI().toString(), nvps);
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters for selectSeats API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected TypedResponseHandler<ArrayList<BookingRunningTotalRow>> createResponseHandler(String... params) {
        return new BookingRunningTotalResponseHandler();
    }
}
