package uk.co.odeon.androidapp.util.http;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;

public class UriRequestTask implements Runnable {
    private static final int KEEPALIVE_SECONDS = 30;
    private static final int MAX_CONNECTIONS = 20;
    private static final String TAG;
    private static final int TIMEOUT = 20000;
    private static DefaultHttpClient httpClient;
    protected Context mAppContext;
    private ResponseHandler mHandler;
    private Boolean mLastResult;
    private int mRawResponse;
    private HttpUriRequest mRequest;

    static {
        TAG = UriRequestTask.class.getSimpleName();
    }

    public UriRequestTask(HttpUriRequest request, ResponseHandler handler, Context appContext) {
        this.mRawResponse = -1;
        this.mLastResult = null;
        this.mRequest = request;
        this.mHandler = handler;
        this.mAppContext = appContext;
    }

    public void setRawResponse(int rawResponse) {
        this.mRawResponse = rawResponse;
    }

    public void run() {
        try {
            Log.i(TAG, "Running HTTP Request to: " + this.mRequest.getURI());
            HttpResponse response = execute(this.mRequest);
            Log.i(TAG, "Received Response, passing to handler " + this.mHandler.getClass());
            this.mLastResult = Boolean.valueOf(this.mHandler.handleResponse(response, getUri()));
        } catch (IOException e) {
            Log.w(getClass().getSimpleName(), "exception processing asynch request", e);
        }
    }

    private HttpResponse execute(HttpUriRequest mRequest) throws IOException {
        if (this.mRawResponse >= 0) {
            return new RawResponse(this.mAppContext, this.mRawResponse);
        }
        return getHTTPClient().execute(mRequest);
    }

    private static HttpClient getHTTPClient() {
        if (httpClient != null) {
            return httpClient;
        }
        Log.i(TAG, String.format("Configuring HttpClient, MAX_CONNECTIONS %d, TIMEOUT %d, KEEPALIVE_SECONDS %d", new Object[]{Integer.valueOf(MAX_CONNECTIONS), Integer.valueOf(TIMEOUT), Integer.valueOf(KEEPALIVE_SECONDS)}));
        HttpParams params = new BasicHttpParams();
        ConnManagerParams.setMaxTotalConnections(params, MAX_CONNECTIONS);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpConnectionParams.setConnectionTimeout(params, TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, TIMEOUT);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
        httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
                return 30;
            }
        });
        return httpClient;
    }

    public Uri getUri() {
        return Uri.parse(this.mRequest.getURI().toString());
    }

    public Boolean getLastResult() {
        return this.mLastResult;
    }

    public Boolean getLastResult(boolean defaultIfNull) {
        if (this.mLastResult != null) {
            defaultIfNull = this.mLastResult.booleanValue();
        }
        return Boolean.valueOf(defaultIfNull);
    }
}
