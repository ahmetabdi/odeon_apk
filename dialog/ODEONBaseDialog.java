package uk.co.odeon.androidapp.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;

public class ODEONBaseDialog extends Dialog {
    private ProgressDialog progressDialog;

    public ODEONBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public ODEONBaseDialog(Context context, int theme) {
        super(context, theme);
    }

    public ODEONBaseDialog(Context context) {
        super(context);
    }

    protected void showProgress(int msgResource) {
        if (this.progressDialog == null) {
            this.progressDialog = ProgressDialog.show(getContext(), null, getContext().getResources().getString(msgResource), true);
        }
    }

    protected void showCancelableProgress(int msgResource, OnCancelListener cancelListener) {
        if (this.progressDialog == null) {
            this.progressDialog = ProgressDialog.show(getContext(), null, getContext().getResources().getString(msgResource), true, true, cancelListener);
        }
    }

    protected void hideProgress() {
        hideProgress(false);
    }

    protected void hideProgress(boolean ignoreExceptions) {
        if (this.progressDialog != null) {
            if (ignoreExceptions) {
                try {
                    this.progressDialog.dismiss();
                } catch (Throwable e) {
                    Log.e("BaseActivity", "Failed to hide progress dialog", e);
                }
            } else {
                this.progressDialog.dismiss();
            }
            this.progressDialog = null;
        }
    }
}
