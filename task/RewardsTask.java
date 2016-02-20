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
import uk.co.odeon.androidapp.json.Rewards;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.resphandlers.RewardsResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class RewardsTask extends AbstractJSONTask<String, Void, Rewards> {
    private static final String TAG;

    static {
        TAG = RewardsTask.class.getSimpleName();
    }

    public RewardsTask(TaskTarget<Rewards> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_GETREWARDS));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("customerEmail", params[0]));
        nvps.add(new BasicNameValuePair("customerPassword", params[1]));
        nvps.add(new BasicNameValuePair(FilmDetailsColumns.DATA_HASH, params[2]));
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            logFullURI(TAG, post.getURI().toString(), nvps);
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters for Rewards API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected TypedResponseHandler<Rewards> createResponseHandler(String... params) {
        return new RewardsResponseHandler();
    }
}
