package uk.co.odeon.androidapp.task;

import android.util.Log;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.util.http.TypedResponseHandler;
import uk.co.odeon.androidapp.util.http.UriRequestTask;

public abstract class AbstractJSONTask<Params, Progress, Result> extends AbstractBaseTask<Params, Progress, Result> {
    protected abstract HttpUriRequest createRequest(Params... paramsArr);

    protected abstract TypedResponseHandler<Result> createResponseHandler(Params... paramsArr);

    public AbstractJSONTask(TaskTarget<Result> target) {
        super(target);
    }

    protected Result doInBackground(Params... params) {
        HttpUriRequest req = createRequest(params);
        TypedResponseHandler<Result> respHandler = createResponseHandler(params);
        new UriRequestTask(req, respHandler, ODEONApplication.getInstance()).run();
        return respHandler.getResult();
    }

    protected void logFullURI(String tag, String url) {
        logFullURI(tag, url, null);
    }

    protected void logFullURI(String tag, String url, List<NameValuePair> params) {
        String txt = url;
        if (params != null) {
            for (NameValuePair param : params) {
                if (param.getName().equalsIgnoreCase("customerPassword")) {
                    txt = new StringBuilder(String.valueOf(txt)).append("/").append(param.getName()).append("/xxxxx").toString();
                } else {
                    txt = new StringBuilder(String.valueOf(txt)).append("/").append(param.getName()).append("/").append(param.getValue()).toString();
                }
            }
        }
        Log.i(tag, "Full request URI: " + txt);
    }
}
