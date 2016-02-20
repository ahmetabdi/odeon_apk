package uk.co.odeon.androidapp.updateservice;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;
import uk.co.odeon.androidapp.Constants;

public abstract class AbstractUpdateService<T extends Parcelable> extends IntentService {
    public static String TAG;
    protected Thread waitThread;

    public enum UpdateServiceSimpleStatus implements Parcelable {
        RUNNING,
        DONE_NOTREQ,
        DONE_NOCHANGES,
        DONE_UPDATED,
        SLOW,
        FAILED;
        
        public static final Creator CREATOR;
        private Uri uri;

        static {
            CREATOR = new Creator() {
                public UpdateServiceSimpleStatus createFromParcel(Parcel in) {
                    return UpdateServiceSimpleStatus.fromParcel(in);
                }

                public UpdateServiceSimpleStatus[] newArray(int size) {
                    return new UpdateServiceSimpleStatus[size];
                }
            };
        }

        public Uri getURI() {
            return this.uri;
        }

        public UpdateServiceSimpleStatus setURI(Uri uri) {
            this.uri = uri;
            return this;
        }

        public int describeContents() {
            return 0;
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(toString());
            dest.writeString(this.uri == null ? null : this.uri.toString());
        }

        public static UpdateServiceSimpleStatus fromParcel(Parcel p) {
            UpdateServiceSimpleStatus st = valueOf(p.readString());
            if (p.dataAvail() > 0) {
                String uriStr = p.readString();
                if (uriStr != null) {
                    st.setURI(Uri.parse(uriStr));
                }
            }
            return st;
        }
    }

    public class WaitThread extends Thread {
        public static final int DEFAULT_WAIT_MS = 500;
        private int waitMS;

        public WaitThread() {
            this.waitMS = DEFAULT_WAIT_MS;
        }

        public WaitThread(int waitMS) {
            this.waitMS = DEFAULT_WAIT_MS;
            this.waitMS = waitMS;
        }

        public int getWaitMS() {
            return this.waitMS;
        }

        public void setWaitMS(int waitMS) {
            this.waitMS = waitMS;
        }

        public void run() {
            Log.i(AbstractUpdateService.TAG, "Starting wait thread");
            try {
                Thread.sleep((long) getWaitMS());
                if (isInterrupted()) {
                    Log.i(AbstractUpdateService.TAG, getWaitMS() + "ms have passed, but request seems to be complete");
                    return;
                }
                Log.i(AbstractUpdateService.TAG, "More than " + getWaitMS() + "ms have passed, flagging as slow");
                AbstractUpdateService.this.sendStatusNotifyBroadcastIntent(AbstractUpdateService.this.getSlowStatus());
            } catch (InterruptedException e) {
                Log.i(AbstractUpdateService.TAG, "Wait thread interrupted");
            }
        }
    }

    protected abstract void doHandleIntent(Intent intent);

    public abstract String getStatusNotifiyActionName();

    static {
        TAG = AbstractUpdateService.class.getSimpleName();
    }

    public AbstractUpdateService(String name) {
        super(name);
        this.waitThread = null;
    }

    public T getSlowStatus() {
        return UpdateServiceSimpleStatus.SLOW;
    }

    protected void sendStatusNotifyBroadcastIntent(T status) {
        sendStatusNotifyBroadcastIntent(status, null, null, null);
    }

    protected void sendStatusNotifyBroadcastIntent(T status, String msgHeader, String msgText) {
        sendStatusNotifyBroadcastIntent(status, null, msgHeader, msgText);
    }

    protected void sendStatusNotifyBroadcastIntent(T status, Intent originalIntent, String msgHeader, String msgText) {
        Log.d(TAG, "Sending status notification broadcast intent: " + status);
        Intent i = new Intent().setAction(getStatusNotifiyActionName());
        Bundle extras = new Bundle();
        extras.putParcelable(Constants.EXTRA_UPDATESERVICE_STATUS, status);
        if (!(msgHeader == null || msgText == null)) {
            extras.putString(Constants.EXTRA_UPDATESERVICE_MSG_HEADER, msgHeader);
            extras.putString(Constants.EXTRA_UPDATESERVICE_MSG_TEXT, msgText);
        }
        if (originalIntent != null) {
            extras.putParcelable(Constants.EXTRA_UPDATESERVICE_ORIGINTENT, originalIntent);
        }
        i.putExtras(extras);
        Log.d(TAG, i.toString());
        sendBroadcast(i);
    }

    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Starting: " + intent.getData());
        doHandleIntent(intent);
    }
}
