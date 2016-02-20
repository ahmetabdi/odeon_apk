package uk.co.odeon.androidapp.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import java.util.ArrayList;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.adapters.BookingTicketListAdapter;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingRunningTotalRow;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.model.BookingSection.Mode;
import uk.co.odeon.androidapp.task.BookingSeatSelectionTask;
import uk.co.odeon.androidapp.task.TaskTarget;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class BookingTicketSelectionActivity extends AbstractODEONBookingBaseActivity implements TaskTarget<ArrayList<BookingRunningTotalRow>> {
    protected static final String TAG;
    protected final BookingProcess bookingProcess;
    protected BookingSeatSelectionTask bookingSeatSelectionTask;
    private boolean isOrientationChanged;
    protected BookingSection section;

    public BookingTicketSelectionActivity() {
        this.isOrientationChanged = false;
        this.bookingProcess = BookingProcess.getInstance();
        this.bookingSeatSelectionTask = null;
        this.section = null;
    }

    static {
        TAG = BookingTicketSelectionActivity.class.getSimpleName();
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_ticket_selection);
        configureNavigationHeader();
        if (this.bookingProcess.seatingData == null) {
            showBookingAlert(getResources().getString(R.string.booking_error_no_data));
        } else {
            this.section = this.bookingProcess.seatingData.getSelectedSection();
            AmazingListView ticketList = (AmazingListView) findViewById(R.id.bookingTicketList);
            TextView feeTextView = (TextView) findViewById(R.id.bookingTicketListFeeText);
            if (feeTextView != null) {
                if (this.bookingProcess.cardHandlingFeePerTicket > 0.0f) {
                    feeTextView.setVisibility(0);
                    feeTextView.setText(this.bookingProcess.cardHandlingFeeInfoTextTicketSelection);
                } else {
                    feeTextView.setVisibility(8);
                    feeTextView.setText("");
                }
            }
            ticketList.setAdapter(new BookingTicketListAdapter(this, R.layout.booking_ticket_list_item, this.section.prices, this.bookingProcess.headerFilmTitle, this.bookingProcess.headerFormated));
            ticketList.setFastScrollEnabled(true);
            ticketList.setPinnedHeaderView(LayoutInflater.from(this).inflate(R.layout.booking_header_sub_info, ticketList, false));
        }
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
            this.section.clearTicketSelection();
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

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_ticket_selection);
        configureNavigationHeaderNext(new OnClickListener() {
            public void onClick(View v) {
                if (BookingTicketSelectionActivity.this.section == null || BookingTicketSelectionActivity.this.section.getSelectedTicketCount() <= 0) {
                    BookingTicketSelectionActivity.this.showBookingWarning(BookingTicketSelectionActivity.this.getResources().getString(R.string.tickets_no_selection_message));
                    return;
                }
                ODEONApplication.trackEvent("Showtimes-book now Ticket Type", "Click", "");
                if (BookingTicketSelectionActivity.this.section.mode.equals(Mode.reserved)) {
                    BookingTicketSelectionActivity.this.startActivity(new Intent(v.getContext(), BookingSeatSelectionActivity.class));
                    return;
                }
                BookingTicketSelectionActivity.this.bookingSeatSelectionTask = new BookingSeatSelectionTask(BookingTicketSelectionActivity.this);
                BookingTicketSelectionActivity.this.bookingSeatSelectionTask.execute(new String[]{BookingTicketSelectionActivity.this.bookingProcess.bookingSessionHash, BookingTicketSelectionActivity.this.bookingProcess.bookingSessionId, BookingTicketSelectionActivity.this.section.name, "", BookingTicketSelectionActivity.this.section.getSelectedTicketsAsString(), null});
                BookingTicketSelectionActivity.this.showProgress(R.string.booking_progress, true);
            }
        });
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                BookingTicketSelectionActivity.this.finish();
            }
        });
    }
}
