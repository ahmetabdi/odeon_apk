package uk.co.odeon.androidapp.task;

import android.os.AsyncTask;
import android.util.Pair;
import java.io.File;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

public class TwitterTask extends AsyncTask<Object, Void, Pair<Boolean, Exception>> {
    private TaskTarget<Pair<Boolean, Exception>> target;

    public TwitterTask(TaskTarget<Pair<Boolean, Exception>> target) {
        this.target = target;
    }

    protected Pair<Boolean, Exception> doInBackground(Object... params) {
        try {
            File twitterImage = params[1];
            Twitter twitter = params[2];
            StatusUpdate statusUpdate = new StatusUpdate(params[0]);
            statusUpdate.setMedia(twitterImage);
            twitter.updateStatus(statusUpdate);
            return new Pair(Boolean.valueOf(true), null);
        } catch (Exception e) {
            return new Pair(Boolean.valueOf(false), e);
        }
    }

    protected void onPostExecute(Pair<Boolean, Exception> result) {
        if (this.target != null) {
            this.target.setTaskResult(result);
        }
    }
}
