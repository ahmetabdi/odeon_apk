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
import uk.co.odeon.androidapp.json.ScheduleDates;
import uk.co.odeon.androidapp.resphandlers.FilmScheduleResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class FilmScheduleTask extends AbstractJSONTask<String, Void, ScheduleDates> {
    private static final String TAG;

    static {
        TAG = FilmScheduleTask.class.getSimpleName();
    }

    public FilmScheduleTask(TaskTarget<ScheduleDates> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_FILMSCHEDULE));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("m", params[0]));
        nvps.add(new BasicNameValuePair("s", params[1]));
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters for filmTimes API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected TypedResponseHandler<ScheduleDates> createResponseHandler(String... params) {
        return new FilmScheduleResponseHandler();
    }
}
