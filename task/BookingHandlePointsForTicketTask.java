package uk.co.odeon.androidapp.task;

import java.util.ArrayList;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.model.BookingRunningTotalRow;
import uk.co.odeon.androidapp.resphandlers.BookingRunningTotalResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class BookingHandlePointsForTicketTask extends AbstractJSONTask<String, Void, ArrayList<BookingRunningTotalRow>> {
    private static final String TAG;

    static {
        TAG = BookingHandlePointsForTicketTask.class.getSimpleName();
    }

    public BookingHandlePointsForTicketTask(TaskTarget<ArrayList<BookingRunningTotalRow>> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpGet get = new HttpGet(new StringBuilder(String.valueOf(Constants.formatLocationUrl(Constants.BOOKING_BASEURL))).append(params[0]).append("/").append("bookingSessionHash/").append(params[1]).append("/").append("bookingSessionId/").append(params[2]).append("/").append("apiKey/").append(Constants.API_KEY).append("/").toString());
        logFullURI(TAG, get.getURI().toString());
        return get;
    }

    protected TypedResponseHandler<ArrayList<BookingRunningTotalRow>> createResponseHandler(String... params) {
        return new BookingRunningTotalResponseHandler();
    }
}
