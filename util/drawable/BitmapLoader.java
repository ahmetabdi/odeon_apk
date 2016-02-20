package uk.co.odeon.androidapp.util.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.util.http.FileHandler;
import uk.co.odeon.androidapp.util.http.FlushedInputStream;
import uk.co.odeon.androidapp.util.http.ResponseHandler;
import uk.co.odeon.androidapp.util.http.UriRequestTask;

public class BitmapLoader implements Runnable {
    private static final String TAG;
    private final Handler imageHandler;
    private File targetFile;
    private final String url;

    /* renamed from: uk.co.odeon.androidapp.util.drawable.BitmapLoader.2 */
    class AnonymousClass2 extends FileHandler {
        AnonymousClass2(File $anonymous0) {
            super($anonymous0);
        }

        public boolean handleResponse(HttpResponse response, Uri uri) throws IOException {
            super.handleResponse(response, uri);
            Log.v(BitmapLoader.TAG, "Stored image: " + BitmapLoader.this.url + " in file: " + BitmapLoader.this.targetFile);
            BitmapLoader.this.postBitmap(BitmapFactory.decodeStream(new FileInputStream(getTargetFile())));
            return true;
        }
    }

    static {
        TAG = BitmapLoader.class.getSimpleName();
    }

    public BitmapLoader(String url, Handler handler) {
        this.targetFile = null;
        this.url = url;
        this.imageHandler = handler;
    }

    public BitmapLoader(String url, File targetFile, Handler imageHandler) {
        this.targetFile = null;
        this.url = url;
        this.imageHandler = imageHandler;
        this.targetFile = targetFile;
    }

    public void run() {
        try {
            Log.d(TAG, "Loading bitmap: " + this.url + " to file: " + this.targetFile);
            if (!tryLoadBitmapFromFile()) {
                new UriRequestTask(new HttpGet(this.url), buildResponseHandler(), ODEONApplication.getInstance()).run();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading bitmap " + this.url, e);
            this.imageHandler.sendEmptyMessage(Constants.MSG_BITMAP_ERROR);
        }
    }

    private ResponseHandler buildResponseHandler() {
        if (this.targetFile == null) {
            return new ResponseHandler() {
                public boolean handleResponse(HttpResponse response, Uri uri) throws IOException {
                    BitmapLoader.this.postBitmap(BitmapFactory.decodeStream(new FlushedInputStream(response.getEntity().getContent())));
                    return true;
                }
            };
        }
        return new AnonymousClass2(this.targetFile);
    }

    private boolean tryLoadBitmapFromFile() throws FileNotFoundException {
        if (this.targetFile == null || !this.targetFile.exists()) {
            return false;
        }
        if (this.targetFile.length() == 0) {
            Log.w(TAG, "Found 0-byte image file, deleting file " + this.targetFile);
            try {
                this.targetFile.delete();
                return false;
            } catch (Throwable e) {
                Log.w(TAG, "Failed to delete 0-byte image file " + this.targetFile, e);
                return false;
            }
        }
        Log.v(TAG, "Using bitmap from file: " + this.targetFile);
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(this.targetFile));
        if (bitmap == null) {
            Log.w(TAG, "Failed to decode file, bitmap is null, deleting file " + this.targetFile);
            try {
                this.targetFile.delete();
                return false;
            } catch (Throwable e2) {
                Log.w(TAG, "Failed to delete 0-byte image file " + this.targetFile, e2);
                return false;
            }
        }
        postBitmap(bitmap);
        return true;
    }

    private void postBitmap(Bitmap bitmap) {
        Log.v(TAG, "Posting loaded image " + this.url);
        Message message = new Message();
        message.what = Constants.MSG_BITMAP_LOADED;
        message.obj = bitmap;
        this.imageHandler.sendMessage(message);
    }
}
