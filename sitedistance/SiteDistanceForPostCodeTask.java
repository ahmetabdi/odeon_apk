package uk.co.odeon.androidapp.sitedistance;

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
import uk.co.odeon.androidapp.task.AbstractJSONTask;
import uk.co.odeon.androidapp.task.TaskTarget;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class SiteDistanceForPostCodeTask extends AbstractJSONTask<String, Integer, SiteDistanceResult> {
    private static final String TAG;

    static {
        TAG = SiteDistanceForPostCodeTask.class.getSimpleName();
    }

    public SiteDistanceForPostCodeTask(TaskTarget<SiteDistanceResult> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_POSTCODE));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("p", params[0]));
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters for postCode API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected TypedResponseHandler<SiteDistanceResult> createResponseHandler(String... params) {
        return new SiteDistanceForPostCodeResponseHandler(params[0]);
    }
}
