package uk.co.odeon.androidapp.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.booking.BookingLoginActivity;
import uk.co.odeon.androidapp.adapters.FilmScheduleListAdapter;
import uk.co.odeon.androidapp.custom.CinemaHeaderView;
import uk.co.odeon.androidapp.custom.FilmHeaderView;
import uk.co.odeon.androidapp.custom.NavigatorBarSubActivity;
import uk.co.odeon.androidapp.json.ScheduleDates;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.task.FilmScheduleTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class FilmScheduleActivity extends NavigatorBarSubActivity implements TaskTarget<ScheduleDates> {
    protected static final String TAG;
    protected Cursor cinemaData;
    private CinemaHeaderView cinemaHeader;
    protected int cinemaId;
    protected ScheduleDates data;
    protected Cursor filmData;
    protected int filmId;
    private OnClickListener onClickTime;

    public FilmScheduleActivity() {
        this.filmData = null;
        this.filmId = 0;
        this.cinemaData = null;
        this.cinemaId = 0;
        this.data = null;
        this.cinemaHeader = null;
        this.onClickTime = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Showtimes-book now Booking Time", "Click", new StringBuilder(String.valueOf(FilmScheduleActivity.this.filmData.getString(FilmScheduleActivity.this.filmData.getColumnIndex(OfferColumns.TITLE)))).append(": ").append(((TextView) v.findViewById(R.id.timeElementTime)).getTag()).toString());
                Intent bookingLogin = new Intent(v.getContext(), BookingLoginActivity.class);
                bookingLogin.putExtra(Constants.EXTRA_PERFORMANCE_ID, v.getTag().toString());
                bookingLogin.putExtra(Constants.EXTRA_CINEMA_ID, FilmScheduleActivity.this.cinemaId);
                FilmScheduleActivity.this.startActivity(bookingLogin);
            }
        };
    }

    static {
        TAG = FilmScheduleActivity.class.getSimpleName();
    }

    public CinemaHeaderView getCinemaHeader() {
        if (this.cinemaHeader == null) {
            this.cinemaHeader = (CinemaHeaderView) findViewById(R.id.cinemaHeaderLayout);
        }
        return this.cinemaHeader;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.film_schedule);
        this.filmId = getIntent().getIntExtra(Constants.EXTRA_FILM_ID, 0);
        this.cinemaId = getIntent().getIntExtra(Constants.EXTRA_CINEMA_ID, 0);
        readScheduleData();
        this.filmData = ODEONApplication.getInstance().getFilmDataCursor(this, this.filmId);
        FilmHeaderView filmHeader = (FilmHeaderView) findViewById(R.id.filmHeaderLayout);
        if (filmHeader != null) {
            filmHeader.setDataInView(this, this.filmData);
        }
        this.cinemaData = ODEONApplication.getInstance().getCinemaDataCursor((Activity) this, this.cinemaId);
    }

    public void onResume() {
        super.onResume();
        if (!(getCinemaHeader() == null || this.cinemaData == null)) {
            this.cinemaData.requery();
            if (this.cinemaData.moveToFirst()) {
                getCinemaHeader().setDataInView(this, this.cinemaData);
            }
        }
        if (this.filmData != null) {
            this.filmData.requery();
            this.filmData.moveToFirst();
        }
    }

    public void onRecycled() {
        Log.d(TAG, "onRecycled " + getIntent());
        super.onRecycled();
        if (getCinemaHeader() != null && this.cinemaData != null) {
            this.cinemaData.requery();
            if (this.cinemaData.moveToFirst()) {
                getCinemaHeader().setDataInView(this, this.cinemaData);
            }
        }
    }

    private void readScheduleData() {
        new FilmScheduleTask(this).execute(new String[]{String.valueOf(this.filmId), String.valueOf(this.cinemaId)});
        showProgress(R.string.film_schedule_progress, true);
    }

    public void setTaskResult(ScheduleDates taskResult) {
        hideProgress(true);
        if (taskResult == null) {
            return;
        }
        if (taskResult.errorText != null) {
            showAlert(taskResult.errorText);
            return;
        }
        this.data = taskResult;
        ListView listView = (ListView) findViewById(R.id.scheduleDays);
        if (listView != null) {
            listView.setAdapter(new FilmScheduleListAdapter(this, this.data, this.onClickTime));
        }
    }

    protected void showAlert(String msg) {
        Builder builder = new Builder(getDialogContext());
        builder.setMessage(msg).setCancelable(false).setNeutralButton(getResources().getString(R.string.film_schedule_error_button_label), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                FilmScheduleActivity.this.onBackPressed();
            }
        });
        builder.create().show();
    }
}
