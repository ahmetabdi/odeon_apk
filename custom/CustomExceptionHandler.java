package uk.co.odeon.androidapp.custom;

import android.os.Build;
import android.os.Build.VERSION;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class CustomExceptionHandler implements UncaughtExceptionHandler {
    private String appVer;
    private UncaughtExceptionHandler defaultUEH;
    private String localPath;
    private String url;

    public CustomExceptionHandler(String localPath, String url, String appVer) {
        this.localPath = localPath;
        this.url = url;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.appVer = appVer;
    }

    public void uncaughtException(Thread t, Throwable e) {
        String androidId = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.DISPLAY.length() % 10) + (Build.HOST.length() % 10) + (Build.ID.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10) + (Build.TAGS.length() % 10) + (Build.TYPE.length() % 10) + (Build.USER.length() % 10);
        String timestamp = new SimpleDateFormat("yyyy-MM-dd-HHmmss").format(new Date());
        Writer result = new StringWriter();
        PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = new StringBuilder(String.valueOf(timestamp)).append(".stacktrace").toString();
        if (this.localPath != null) {
            writeToFile(stacktrace, filename);
        }
        if (this.url != null) {
            sendToServer(stacktrace, timestamp, androidId);
        }
        this.defaultUEH.uncaughtException(t, e);
    }

    private void writeToFile(String stacktrace, String filename) {
        try {
            BufferedWriter bos = new BufferedWriter(new FileWriter(this.localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendToServer(String stacktrace, String timestamp, String androidId) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(this.url);
        List<NameValuePair> nvps = new ArrayList();
        nvps.add(new BasicNameValuePair("timestamp", timestamp));
        nvps.add(new BasicNameValuePair("brand", Build.BRAND));
        nvps.add(new BasicNameValuePair("manufacturer", Build.MANUFACTURER));
        nvps.add(new BasicNameValuePair("model", Build.MODEL));
        nvps.add(new BasicNameValuePair("androidVer", String.valueOf(VERSION.SDK_INT)));
        nvps.add(new BasicNameValuePair("androidId", androidId));
        nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
        nvps.add(new BasicNameValuePair("appVer", this.appVer));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
