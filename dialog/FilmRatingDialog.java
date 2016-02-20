package uk.co.odeon.androidapp.dialog;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.task.RateAFilmTask;
import uk.co.odeon.androidapp.task.RateAFilmTaskParams;
import uk.co.odeon.androidapp.task.TaskTarget;

public class FilmRatingDialog extends ODEONBaseDialog implements TaskTarget<Void> {
    protected static final String TAG;
    private OnClickListener cancelClickListener;
    private int filmId;
    private OnRatingListener onRatingListener;
    private RatingBar ratingBar;
    private OnRatingBarChangeListener ratingClickListener;

    /* renamed from: uk.co.odeon.androidapp.dialog.FilmRatingDialog.3 */
    class AnonymousClass3 implements OnCancelListener {
        private final /* synthetic */ RateAFilmTask val$rt;

        AnonymousClass3(RateAFilmTask rateAFilmTask) {
            this.val$rt = rateAFilmTask;
        }

        public void onCancel(DialogInterface dialog) {
            FilmRatingDialog.this.dismiss();
            this.val$rt.detach();
            this.val$rt.cancel(true);
        }
    }

    public interface OnRatingListener {
        void onRatingDone(int i);
    }

    static {
        TAG = FilmRatingDialog.class.getSimpleName();
    }

    public FilmRatingDialog(Context ctx, int filmId, OnRatingListener onRatingListener) {
        super(ctx, R.style.dialogSlidingInFromBottom);
        this.cancelClickListener = new OnClickListener() {
            public void onClick(View v) {
                FilmRatingDialog.this.dismiss();
            }
        };
        this.ratingClickListener = new OnRatingBarChangeListener() {

            /* renamed from: uk.co.odeon.androidapp.dialog.FilmRatingDialog.2.1 */
            class AnonymousClass1 implements DialogInterface.OnClickListener {
                private final /* synthetic */ float val$rating;

                AnonymousClass1(float f) {
                    this.val$rating = f;
                }

                public void onClick(DialogInterface dialog, int which) {
                    Log.i(FilmRatingDialog.TAG, "Storing rating: " + this.val$rating);
                    int rating = FilmRatingDialog.this.storeRatingValue();
                    if (FilmRatingDialog.this.onRatingListener != null) {
                        FilmRatingDialog.this.onRatingListener.onRatingDone(rating);
                    }
                    FilmRatingDialog.this.dismiss();
                }
            }

            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    Log.i(FilmRatingDialog.TAG, "Showing confirmation dialog for rating: " + rating);
                    new Builder(FilmRatingDialog.this.getContext()).setIcon(17301543).setTitle(R.string.film_rating_confirm_title).setMessage(FilmRatingDialog.this.getContext().getResources().getString(R.string.film_rating_confirm, new Object[]{Integer.valueOf((int) rating)})).setPositiveButton(R.string.film_rating_confirm_ok, new AnonymousClass1(rating)).setNegativeButton(R.string.film_rating_btn_cancel, null).show();
                }
            }
        };
        this.filmId = filmId;
        this.onRatingListener = onRatingListener;
        getWindow().setGravity(80);
    }

    public void setOnRatingListener(OnRatingListener onRatingListener) {
        this.onRatingListener = onRatingListener;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        setContentView(R.layout.film_rating_dialog);
        this.ratingBar = (RatingBar) findViewById(R.id.filmRatingBar);
        this.ratingBar.setRating((float) getExistingRatingValue());
        findViewById(R.id.filmRatingBtnCancel).setOnClickListener(this.cancelClickListener);
        this.ratingBar.setOnRatingBarChangeListener(this.ratingClickListener);
    }

    private int getExistingRatingValue() {
        Log.i(TAG, "Existing rating: " + ODEONApplication.getInstance().getCustomerDataPrefs().getInt("rating_" + this.filmId, -1));
        return ODEONApplication.getInstance().getCustomerDataPrefs().getInt("rating_" + this.filmId, 0);
    }

    private int storeRatingValue() {
        return storeRatingValue(Math.round(this.ratingBar.getRating()));
    }

    private int storeRatingValue(int rating) {
        ODEONApplication.getInstance().getCustomerDataPrefs().edit().putInt("rating_" + this.filmId, rating).commit();
        ODEONApplication a = ODEONApplication.getInstance();
        RateAFilmTaskParams params = new RateAFilmTaskParams(a.getCustomerLoginEmail(), a.getCustomerLoginPassword(), rating, this.filmId);
        RateAFilmTask rt = new RateAFilmTask(this);
        showCancelableProgress(R.string.film_rating_store_progress, new AnonymousClass3(rt));
        rt.execute(new RateAFilmTaskParams[]{params});
        return rating;
    }

    public void setTaskResult(Void taskResult) {
        hideProgress(true);
    }
}
