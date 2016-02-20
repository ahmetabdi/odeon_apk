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
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.resphandlers.VoidResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class RateAFilmTask extends AbstractJSONTask<RateAFilmTaskParams, Void, Void> {
    private static final String TAG;

    static {
        TAG = RateAFilmTask.class.getSimpleName();
    }

    public RateAFilmTask(TaskTarget<Void> target) {
        super(target);
    }

    public RateAFilmTask() {
        super(null);
    }

    protected HttpUriRequest createRequest(RateAFilmTaskParams... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_RATEAFILM));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("m", String.valueOf(params[0].getFilmMasterId())));
        nvps.add(new BasicNameValuePair("customerEmail", params[0].getEmail()));
        nvps.add(new BasicNameValuePair("customerPassword", params[0].getPassword()));
        nvps.add(new BasicNameValuePair("rating", String.valueOf(params[0].getRating())));
        nvps.add(new BasicNameValuePair(FilmDetailsColumns.DATA_HASH, "a"));
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

    protected TypedResponseHandler<Void> createResponseHandler(RateAFilmTaskParams... params) {
        return new VoidResponseHandler();
    }
}
