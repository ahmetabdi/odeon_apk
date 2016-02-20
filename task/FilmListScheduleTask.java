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
import uk.co.odeon.androidapp.json.FilterFilms;
import uk.co.odeon.androidapp.provider.FilmContent.FilmDetailsColumns;
import uk.co.odeon.androidapp.resphandlers.FilmListScheduleResponseHandler;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;

public class FilmListScheduleTask extends AbstractJSONTask<String, Void, FilterFilms> {
    private static final String TAG;

    static {
        TAG = FilmListScheduleTask.class.getSimpleName();
    }

    public FilmListScheduleTask(TaskTarget<FilterFilms> target) {
        super(target);
    }

    protected HttpUriRequest createRequest(String... params) {
        HttpPost post = new HttpPost(Constants.formatLocationUrl(Constants.API_URL_FILMLISTSCHEDULE));
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("s", params[0]));
        nvps.add(new BasicNameValuePair("apiKey", Constants.API_KEY));
        if (params.length > 1 && params[1] != null) {
            nvps.add(new BasicNameValuePair(FilmDetailsColumns.DATA_HASH, params[1]));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            return post;
        } catch (UnsupportedEncodingException e) {
            String err = "Failed to build POST parameters for filmList API call: " + e.getMessage();
            Log.e(TAG, err, e);
            throw new RuntimeException(err, e);
        }
    }

    protected TypedResponseHandler<FilterFilms> createResponseHandler(String... params) {
        return new FilmListScheduleResponseHandler(params[0]);
    }
}
