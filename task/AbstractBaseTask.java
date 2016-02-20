package uk.co.odeon.androidapp.task;

import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.util.Log;
import java.util.concurrent.ExecutionException;

public abstract class AbstractBaseTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    private static final String TAG;
    private TaskTarget<Result> target;

    static {
        TAG = AbstractBaseTask.class.getSimpleName();
    }

    public AbstractBaseTask(TaskTarget<Result> target) {
        this.target = target;
    }

    protected void onPostExecute(Result result) {
        if (this.target != null) {
            this.target.setTaskResult(result);
        }
    }

    public void detach() {
        this.target = null;
    }

    public boolean attach(TaskTarget<Result> target) {
        this.target = target;
        if (getStatus() == Status.FINISHED && target != null) {
            try {
                target.setTaskResult(get());
                return true;
            } catch (InterruptedException e) {
                Log.e(TAG, "Error getting task result!", e);
            } catch (ExecutionException e2) {
                Log.e(TAG, "Error getting task result!", e2);
            }
        }
        return false;
    }
}
