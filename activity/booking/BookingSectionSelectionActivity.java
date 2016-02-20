package uk.co.odeon.androidapp.activity.booking;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.ZoomControls;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.AutofitTextView;
import uk.co.odeon.androidapp.custom.SeatingPlanView;
import uk.co.odeon.androidapp.custom.SeatingPlanView.OnDoubleTapListener;
import uk.co.odeon.androidapp.custom.SeatingPlanView.OnPlanTouchedListener;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingSeat;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.model.BookingSection.Mode;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.task.BookingUnselectSeatsTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class BookingSectionSelectionActivity extends AbstractODEONBookingBaseActivity implements TaskTarget<Boolean> {
    protected static final int DIALOG_UNRESERVED = 1;
    protected static final int DIALOG_WARNING = 2;
    protected static final String TAG;
    private final BookingProcess bookingProcess;
    protected BookingUnselectSeatsTask bookingUnselectSeatsTask;
    private boolean isOrientationChanged;
    private final OnDoubleTapListener onDoubleTap;
    private final OnPlanTouchedListener onPlanTouched;
    private SeatingPlanView seatingPlanView;
    private AutofitTextView sectionNameView;
    private ZoomControls zoomView;

    /* renamed from: uk.co.odeon.androidapp.activity.booking.BookingSectionSelectionActivity.6 */
    class AnonymousClass6 implements OnClickListener {
        private final /* synthetic */ int val$id;

        AnonymousClass6(int i) {
            this.val$id = i;
        }

        public void onClick(View v) {
            BookingSectionSelectionActivity.this.removeDialog(this.val$id);
        }
    }

    public BookingSectionSelectionActivity() {
        this.isOrientationChanged = false;
        this.bookingProcess = BookingProcess.getInstance();
        this.bookingUnselectSeatsTask = null;
        this.onPlanTouched = new OnPlanTouchedListener() {
            public boolean onSeatTouched(BookingSeat seat) {
                if (seat != null) {
                    BookingSection section = BookingSectionSelectionActivity.this.bookingProcess.seatingData.getSectionById(seat.sectionId);
                    BookingSectionSelectionActivity.this.setActiveSection(section);
                    if (section.mode.equals(Mode.unreserved)) {
                        BookingSectionSelectionActivity.this.showDialog(BookingSectionSelectionActivity.DIALOG_UNRESERVED);
                    } else {
                        BookingSectionSelectionActivity.this.removeDialog(BookingSectionSelectionActivity.DIALOG_UNRESERVED);
                    }
                    if (section.hasWarning()) {
                        Bundle dialogData = new Bundle();
                        dialogData.putString(OfferColumns.TEXT, section.warning);
                        BookingSectionSelectionActivity.this.showDialog(BookingSectionSelectionActivity.DIALOG_WARNING, dialogData);
                        return true;
                    }
                    BookingSectionSelectionActivity.this.removeDialog(BookingSectionSelectionActivity.DIALOG_WARNING);
                    return true;
                }
                BookingSectionSelectionActivity.this.removeDialog(BookingSectionSelectionActivity.DIALOG_UNRESERVED);
                BookingSectionSelectionActivity.this.removeDialog(BookingSectionSelectionActivity.DIALOG_WARNING);
                return false;
            }

            public void onSeatTouchedLong(BookingSeat seat) {
            }
        };
        this.onDoubleTap = new OnDoubleTapListener() {
            public void onDoubleTapIn() {
                BookingSectionSelectionActivity.this.zoomView.setIsZoomInEnabled(false);
                BookingSectionSelectionActivity.this.zoomView.setIsZoomOutEnabled(true);
            }

            public void onDoubleTapOut() {
                BookingSectionSelectionActivity.this.zoomView.setIsZoomInEnabled(true);
                BookingSectionSelectionActivity.this.zoomView.setIsZoomOutEnabled(false);
            }
        };
    }

    static {
        TAG = BookingSectionSelectionActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_section_selection);
        configureNavigationHeader();
        this.sectionNameView = (AutofitTextView) findViewById(R.id.subHeaderSectionName);
        this.seatingPlanView = (SeatingPlanView) findViewById(R.id.seatingPlanView);
        this.seatingPlanView.setOnPlanTouchedListener(this.onPlanTouched);
        this.seatingPlanView.setOnDoubleTapListener(this.onDoubleTap);
        this.seatingPlanView.setSeatingData(this.bookingProcess.seatingData, false);
        this.zoomView = (ZoomControls) findViewById(R.id.seatingPlanZoom);
        this.seatingPlanView.setZoomCtl(this.zoomView);
        this.bookingUnselectSeatsTask = (BookingUnselectSeatsTask) getLastNonConfigurationInstance();
        if (this.bookingUnselectSeatsTask != null) {
            showProgress(R.string.booking_progress, true);
            this.bookingUnselectSeatsTask.attach(this);
        }
    }

    public void onResume() {
        super.onResume();
        if (getIntent().getAction() != null && getIntent().getAction().equals(Constants.ACTION_RESTART_BOOKING)) {
            this.bookingUnselectSeatsTask = new BookingUnselectSeatsTask(this);
            BookingUnselectSeatsTask bookingUnselectSeatsTask = this.bookingUnselectSeatsTask;
            String[] strArr = new String[DIALOG_WARNING];
            strArr[0] = this.bookingProcess.bookingSessionHash;
            strArr[DIALOG_UNRESERVED] = this.bookingProcess.bookingSessionId;
            bookingUnselectSeatsTask.execute(strArr);
            showProgress(R.string.booking_progress, true);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        this.isOrientationChanged = true;
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.bookingUnselectSeatsTask != null) {
            this.bookingUnselectSeatsTask.detach();
        }
        return this.bookingUnselectSeatsTask;
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (!(this.isOrientationChanged || this.bookingProcess.seatingData == null)) {
            this.bookingProcess.seatingData.clearSectionSelection();
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        if (ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            performEndOfBooking();
        } else {
            super.onBackPressed();
        }
    }

    public void setTaskResult(Boolean taskResult) {
        hideProgress(true);
        if (taskResult != null) {
            this.seatingPlanView.setSeatingData(this.bookingProcess.seatingData, false);
        } else {
            Log.w(TAG, "Unselect seats: " + (this.bookingProcess.hasError() ? this.bookingProcess.getLastError(true) : "Unknown error."));
        }
    }

    private void setActiveSection(BookingSection section) {
        this.bookingProcess.seatingData.selectSection(section);
        this.sectionNameView.setText(section.name);
        redrawPlan();
    }

    private void redrawPlan() {
        this.seatingPlanView.invalidate();
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_section_selection);
        configureSubHeaderFilm(this.bookingProcess.headerFilmTitle, this.bookingProcess.headerFormated);
        configureNavigationHeaderNext(new OnClickListener() {
            public void onClick(View v) {
                if (BookingSectionSelectionActivity.this.bookingProcess.seatingData == null || BookingSectionSelectionActivity.this.bookingProcess.seatingData.getSelectedSection() == null) {
                    BookingSectionSelectionActivity.this.showBookingWarning(BookingSectionSelectionActivity.this.getResources().getString(R.string.seatplan_no_section_selection_title), BookingSectionSelectionActivity.this.getResources().getString(R.string.seatplan_no_section_selection_message));
                    return;
                }
                ODEONApplication.trackEvent("Showtimes-book now Section Selection", "Click", "");
                BookingSectionSelectionActivity.this.startActivity(new Intent(v.getContext(), BookingTicketSelectionActivity.class));
            }
        });
        if (ODEONApplication.getInstance().hasCustomerLoginInPrefs()) {
            configureNavigationHeaderCancel(getString(R.string.booking_header_button_cancel), R.drawable.nav_bar_btn_4_round, new OnClickListener() {
                public void onClick(View v) {
                    BookingSectionSelectionActivity.this.performEndOfBooking();
                }
            });
        } else {
            configureNavigationHeaderCancel(new OnClickListener() {
                public void onClick(View v) {
                    BookingSectionSelectionActivity.this.finish();
                }
            });
        }
    }

    protected Dialog onCreateDialog(int id, Bundle bundle) {
        Dialog dialog = new Dialog(this);
        TextView message = new TextView(this);
        switch (id) {
            case DIALOG_UNRESERVED /*1*/:
                message.setText(R.string.seatplan_unreserved_message);
                break;
            case DIALOG_WARNING /*2*/:
                message.setText(bundle.getString(OfferColumns.TEXT));
                break;
            default:
                dialog = null;
                break;
        }
        if (dialog != null) {
            message.setPadding(10, 10, 10, 10);
            message.setOnClickListener(new AnonymousClass6(id));
            message.setMovementMethod(new ScrollingMovementMethod());
            dialog.requestWindowFeature(DIALOG_UNRESERVED);
            dialog.setContentView(message);
            Window window = dialog.getWindow();
            window.setFlags(32, 32);
            window.clearFlags(DIALOG_WARNING);
            window.setGravity(17);
        }
        return dialog;
    }
}
