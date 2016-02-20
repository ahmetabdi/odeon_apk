package uk.co.odeon.androidapp.activity.booking;

import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ZoomControls;
import java.util.ArrayList;
import java.util.List;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.SeatIconImageView;
import uk.co.odeon.androidapp.custom.SeatingPlanView;
import uk.co.odeon.androidapp.custom.SeatingPlanView.OnDoubleTapListener;
import uk.co.odeon.androidapp.custom.SeatingPlanView.OnPlanTouchedListener;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingRunningTotalRow;
import uk.co.odeon.androidapp.model.BookingSeat;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.task.BookingSeatSelectionTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class BookingSeatSelectionActivity extends AbstractODEONBookingBaseActivity implements TaskTarget<ArrayList<BookingRunningTotalRow>> {
    protected static final String TAG;
    private final BookingProcess bookingProcess;
    protected BookingSeatSelectionTask bookingSeatSelectionTask;
    private boolean isOrientationChanged;
    private final OnDoubleTapListener onDoubleTap;
    private final OnPlanTouchedListener onPlanTouched;
    private final OnClickListener onResetClickListener;
    private SeatingPlanView seatingPlanView;
    protected BookingSection section;
    private LinearLayout subHeaderSeatLayout;
    private ZoomControls zoomView;

    public BookingSeatSelectionActivity() {
        this.isOrientationChanged = false;
        this.bookingProcess = BookingProcess.getInstance();
        this.bookingSeatSelectionTask = null;
        this.section = null;
        this.onPlanTouched = new OnPlanTouchedListener() {
            public boolean onSeatTouched(BookingSeat seat) {
                if (BookingSeatSelectionActivity.this.section == null || seat == null) {
                    return false;
                }
                if (BookingSeatSelectionActivity.this.section.selectSeats(seat) >= 0) {
                    BookingSeatSelectionActivity.this.redrawPlan();
                } else {
                    BookingSeatSelectionActivity.this.showBookingWarning(BookingSeatSelectionActivity.this.getResources().getString(R.string.seatplan_impossible_seat_selection_title), BookingSeatSelectionActivity.this.getResources().getString(R.string.seatplan_impossible_seat_selection_message));
                }
                return true;
            }

            public void onSeatTouchedLong(BookingSeat seat) {
            }
        };
        this.onDoubleTap = new OnDoubleTapListener() {
            public void onDoubleTapIn() {
                BookingSeatSelectionActivity.this.zoomView.setIsZoomInEnabled(false);
                BookingSeatSelectionActivity.this.zoomView.setIsZoomOutEnabled(true);
            }

            public void onDoubleTapOut() {
                BookingSeatSelectionActivity.this.zoomView.setIsZoomInEnabled(true);
                BookingSeatSelectionActivity.this.zoomView.setIsZoomOutEnabled(false);
            }
        };
        this.onResetClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (BookingSeatSelectionActivity.this.section != null) {
                    BookingSeatSelectionActivity.this.section.clearSeatSelection();
                }
                BookingSeatSelectionActivity.this.redrawPlan();
            }
        };
    }

    static {
        TAG = BookingSeatSelectionActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_seat_selection);
        configureNavigationHeader();
        if (this.bookingProcess.seatingData == null) {
            showBookingAlert(getResources().getString(R.string.booking_error_no_data));
        } else {
            this.section = this.bookingProcess.seatingData.getSelectedSection();
            this.seatingPlanView = (SeatingPlanView) findViewById(R.id.seatingPlanView);
            this.seatingPlanView.setOnPlanTouchedListener(this.onPlanTouched);
            this.seatingPlanView.setOnDoubleTapListener(this.onDoubleTap);
            this.seatingPlanView.setSeatingData(this.bookingProcess.seatingData, false);
            this.zoomView = (ZoomControls) findViewById(R.id.seatingPlanZoom);
            this.seatingPlanView.setZoomCtl(this.zoomView);
        }
        this.subHeaderSeatLayout = (LinearLayout) findViewById(R.id.subHeaderSeatLayout);
        drawSubHeader();
        ((ImageView) findViewById(R.id.subHeaderReset)).setOnClickListener(this.onResetClickListener);
        this.bookingSeatSelectionTask = (BookingSeatSelectionTask) getLastNonConfigurationInstance();
        if (this.bookingSeatSelectionTask != null) {
            showProgress(R.string.booking_progress, true);
            this.bookingSeatSelectionTask.attach(this);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        this.isOrientationChanged = true;
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (!(this.isOrientationChanged || this.section == null)) {
            this.section.clearSeatSelection();
        }
        super.onDestroy();
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.bookingSeatSelectionTask != null) {
            this.bookingSeatSelectionTask.detach();
        }
        return this.bookingSeatSelectionTask;
    }

    public void setTaskResult(ArrayList<BookingRunningTotalRow> taskResult) {
        hideProgress(true);
        if (taskResult != null) {
            this.bookingProcess.runningTotalRows = taskResult;
            startActivity(new Intent(this, BookingRunningTotalActivity.class));
            return;
        }
        showBookingAlert(this.bookingProcess.hasError() ? this.bookingProcess.getLastError(true) : null);
    }

    private void drawSubHeader() {
        int selectedTicketCount;
        int selectedSeatsCount;
        if (this.section != null) {
            selectedTicketCount = this.section.getSelectedTicketCount();
        } else {
            selectedTicketCount = 0;
        }
        if (this.section != null) {
            selectedSeatsCount = this.section.getSelectedSeatsCount();
        } else {
            selectedSeatsCount = 0;
        }
        if (this.subHeaderSeatLayout.getChildCount() - 1 != selectedTicketCount) {
            if (this.subHeaderSeatLayout.getChildCount() > 1) {
                this.subHeaderSeatLayout.removeViews(0, this.subHeaderSeatLayout.getChildCount() - 1);
            }
            for (int i = 0; i < selectedTicketCount; i++) {
                this.subHeaderSeatLayout.addView(new SeatIconImageView(this), 0);
            }
        }
        for (int j = 0; j < selectedTicketCount; j++) {
            SeatIconImageView seatIcon = (SeatIconImageView) this.subHeaderSeatLayout.getChildAt(j);
            if (seatIcon != null) {
                if (j < selectedSeatsCount) {
                    seatIcon.color = SeatIconImageView.COLOR_PLACED;
                } else {
                    seatIcon.color = SeatIconImageView.COLOR_UNPLACED;
                }
                seatIcon.invalidate();
            }
        }
    }

    private void redrawPlan() {
        drawSubHeader();
        if (this.seatingPlanView != null) {
            this.seatingPlanView.invalidate();
        }
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_seat_selection);
        configureSubHeaderFilm(this.bookingProcess.headerFilmTitle, this.bookingProcess.headerFormated);
        configureNavigationHeaderNext(new OnClickListener() {
            public void onClick(View v) {
                if (BookingSeatSelectionActivity.this.section != null) {
                    List<BookingSeat> selectedSeats = BookingSeatSelectionActivity.this.section.getSelectedSeats();
                    if (BookingSeatSelectionActivity.this.section.getSelectedTicketCount() == selectedSeats.size()) {
                        ODEONApplication.trackEvent("Showtimes-book now Seat Selection", "Click", "");
                        if (BookingSeatSelectionActivity.this.section.isWheelchairSelected(selectedSeats)) {
                            Builder builder = new Builder(BookingSeatSelectionActivity.this);
                            builder.setTitle(R.string.booking_cea_card_title).setMessage(R.string.booking_cea_card_message).setCancelable(false).setNegativeButton(BookingSeatSelectionActivity.this.getResources().getString(R.string.booking_no_cea_card_button_label), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    BookingSeatSelectionActivity.this.performSelectSeats(false);
                                }
                            }).setPositiveButton(BookingSeatSelectionActivity.this.getResources().getString(R.string.booking_yes_cea_card_button_label), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    BookingSeatSelectionActivity.this.performSelectSeats(true);
                                }
                            });
                            builder.create().show();
                            return;
                        }
                        BookingSeatSelectionActivity.this.performSelectSeats(false);
                        return;
                    }
                    BookingSeatSelectionActivity.this.showBookingWarning(BookingSeatSelectionActivity.this.getResources().getString(R.string.seatplan_no_seat_selection_title), BookingSeatSelectionActivity.this.getResources().getString(R.string.seatplan_no_seat_selection_message));
                    return;
                }
                BookingSeatSelectionActivity.this.showBookingAlert(BookingSeatSelectionActivity.this.getResources().getString(R.string.booking_error_no_data));
            }
        });
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                BookingSeatSelectionActivity.this.finish();
            }
        });
    }

    private void performSelectSeats(boolean freeCarer) {
        this.bookingSeatSelectionTask = new BookingSeatSelectionTask(this);
        BookingSeatSelectionTask bookingSeatSelectionTask = this.bookingSeatSelectionTask;
        String[] strArr = new String[6];
        strArr[0] = this.bookingProcess.bookingSessionHash;
        strArr[1] = this.bookingProcess.bookingSessionId;
        strArr[2] = this.section.name;
        strArr[3] = this.section.getSelectedSeatsAsString();
        strArr[4] = this.section.getSelectedTicketsAsString();
        strArr[5] = freeCarer ? "yes" : null;
        bookingSeatSelectionTask.execute(strArr);
        showProgress(R.string.booking_progress, true);
    }
}
