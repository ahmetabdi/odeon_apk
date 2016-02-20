package uk.co.odeon.androidapp.activity.booking;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.activity.WebviewActivity;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingRunningTotalElement;
import uk.co.odeon.androidapp.model.BookingRunningTotalElement.Type;
import uk.co.odeon.androidapp.model.BookingRunningTotalRow;
import uk.co.odeon.androidapp.task.BookingHandlePointsForTicketTask;
import uk.co.odeon.androidapp.task.TaskTarget;

public class BookingRunningTotalActivity extends AbstractODEONBookingBaseActivity implements TaskTarget<ArrayList<BookingRunningTotalRow>> {
    protected static final String TAG;
    protected BookingHandlePointsForTicketTask bookingHandlePointsForTicketTask;
    private final BookingProcess bookingProcess;
    private int columnMargin;
    private boolean isOrientationChanged;
    private OnClickListener pointsForTicketClickListener;
    private int rowMargin;
    protected TableLayout runningTotalLayout;

    public BookingRunningTotalActivity() {
        this.isOrientationChanged = false;
        this.bookingHandlePointsForTicketTask = null;
        this.bookingProcess = BookingProcess.getInstance();
        this.rowMargin = -1;
        this.columnMargin = -1;
        this.runningTotalLayout = null;
        this.pointsForTicketClickListener = new OnClickListener() {
            public void onClick(View v) {
                BookingRunningTotalActivity.this.bookingHandlePointsForTicketTask = new BookingHandlePointsForTicketTask(BookingRunningTotalActivity.this);
                BookingRunningTotalActivity.this.bookingHandlePointsForTicketTask.execute(new String[]{v.getTag().toString(), BookingRunningTotalActivity.this.bookingProcess.bookingSessionHash, BookingRunningTotalActivity.this.bookingProcess.bookingSessionId});
                BookingRunningTotalActivity.this.showProgress(R.string.booking_progress, true);
            }
        };
    }

    static {
        TAG = BookingRunningTotalActivity.class.getSimpleName();
    }

    public TableLayout getRunningTotalLayoutView() {
        if (this.runningTotalLayout == null) {
            this.runningTotalLayout = (TableLayout) findViewById(R.id.runningTotalLayout);
        }
        return this.runningTotalLayout;
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_running_total);
        configureNavigationHeader();
        setDataInView();
        this.bookingHandlePointsForTicketTask = (BookingHandlePointsForTicketTask) getLastNonConfigurationInstance();
        if (this.bookingHandlePointsForTicketTask != null) {
            showProgress(R.string.booking_progress, true);
            this.bookingHandlePointsForTicketTask.attach(this);
        }
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        this.isOrientationChanged = true;
    }

    public Object onRetainNonConfigurationInstance() {
        if (this.bookingHandlePointsForTicketTask != null) {
            this.bookingHandlePointsForTicketTask.detach();
        }
        return this.bookingHandlePointsForTicketTask;
    }

    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        if (!this.isOrientationChanged) {
            this.bookingProcess.runningTotalRows = null;
        }
        super.onDestroy();
    }

    public void onBackPressed() {
        performRestartOfBooking();
    }

    protected void configureNavigationHeader() {
        configureNavigationHeaderTitle((int) R.string.booking_header_running_total);
        Button nextButton = (Button) findViewById(R.id.nextButton);
        if (nextButton != null) {
            nextButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    ODEONApplication.trackEvent("Showtimes-book now Proceed To Payment", "Click", "");
                    BookingRunningTotalActivity.this.startActivity(new Intent(v.getContext(), BookingCheckUserDetailsActivity.class));
                }
            });
        }
        configureNavigationHeaderCancel(new OnClickListener() {
            public void onClick(View v) {
                BookingRunningTotalActivity.this.performRestartOfBooking();
            }
        });
    }

    private void setDataInView() {
        if (this.bookingProcess.runningTotalRows != null) {
            if (this.rowMargin < 0) {
                this.rowMargin = (int) TypedValue.applyDimension(1, (float) getResources().getInteger(R.integer.running_total:row_gap), getResources().getDisplayMetrics());
            }
            if (this.columnMargin < 0) {
                this.columnMargin = (int) TypedValue.applyDimension(1, (float) getResources().getInteger(R.integer.running_total:column_gap), getResources().getDisplayMetrics());
            }
            if (getRunningTotalLayoutView().getChildCount() == 0) {
                inflateHeader();
                inflateFooter();
            }
            int runningTotalRowsInView = getRunningTotalLayoutView().getChildCount() - 2;
            int runningTotalRowsInData = this.bookingProcess.runningTotalRows.size();
            if (runningTotalRowsInView > runningTotalRowsInData) {
                while (runningTotalRowsInView > runningTotalRowsInData) {
                    removeRow();
                    runningTotalRowsInView--;
                }
            } else if (runningTotalRowsInView < runningTotalRowsInData) {
                while (runningTotalRowsInView < runningTotalRowsInData) {
                    inflateRow(this.rowMargin);
                    runningTotalRowsInView++;
                }
            }
            int index = 1;
            Iterator it = this.bookingProcess.runningTotalRows.iterator();
            while (it.hasNext()) {
                inflateRowContent((TableRow) getRunningTotalLayoutView().getChildAt(index), (BookingRunningTotalRow) it.next(), this.columnMargin);
                index++;
            }
        }
    }

    private void inflateHeader() {
        getRunningTotalLayoutView().addView(getLayoutInflater().inflate(R.layout.booking_running_total_header, null));
    }

    private void inflateFooter() {
        getRunningTotalLayoutView().addView(getLayoutInflater().inflate(R.layout.booking_running_total_footer, null));
        TextView footer = (TextView) getRunningTotalLayoutView().findViewById(R.id.runningTotalFooterText);
        String tcLinkStr = getResources().getString(R.string.running_total_t_and_c);
        SpannableString tcText = new SpannableString(new StringBuilder(String.valueOf(this.bookingProcess.cardHandlingFeePerTicket > 0.0f ? new StringBuilder(String.valueOf(this.bookingProcess.cardHandlingFeeInfoTextRunningTotal)).append("\n\n").toString() : "")).append(getResources().getString(R.string.running_total_footer, new Object[]{tcLinkStr})).toString());
        ClickableSpan clickableSpan = new ClickableSpan() {
            public void onClick(View view) {
                Intent webviewIntent = new Intent(BookingRunningTotalActivity.this, WebviewActivity.class);
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_HEADER_TITLE, BookingRunningTotalActivity.this.getResources().getString(R.string.booking_header_t_and_c));
                webviewIntent.putExtra(Constants.EXTRA_WEBVIEW_URL, Constants.formatLocationUrl(Constants.URL_BOOKING_TERMS_AND_CONDITIONS));
                BookingRunningTotalActivity.this.startActivity(webviewIntent);
            }
        };
        int from = tcText.toString().indexOf(tcLinkStr);
        int to = from + tcLinkStr.length();
        tcText.setSpan(clickableSpan, from, to, 0);
        tcText.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.t_and_c_link)), from, to, 0);
        footer.setMovementMethod(LinkMovementMethod.getInstance());
        footer.setHighlightColor(0);
        footer.setText(tcText);
    }

    private void inflateRow(int rowMarging) {
        TableRow rowView = new TableRow(this);
        LayoutParams rowParams = new LayoutParams(-1, -2);
        rowParams.setMargins(0, rowMarging, 0, rowMarging);
        rowView.setLayoutParams(rowParams);
        rowView.setGravity(16);
        getRunningTotalLayoutView().addView(rowView, getRunningTotalLayoutView().getChildCount() - 1);
    }

    private void removeRow() {
        getRunningTotalLayoutView().removeViewAt(1);
    }

    private void inflateRowContent(TableRow rowView, BookingRunningTotalRow rowData, int columnMarging) {
        if (rowData.runningTotalElements != null) {
            rowView.removeAllViews();
            Iterator it = rowData.runningTotalElements.iterator();
            while (it.hasNext()) {
                BookingRunningTotalElement element = (BookingRunningTotalElement) it.next();
                if (element.type.equals(Type.button)) {
                    Button elementAsButton = new Button(this);
                    TableRow.LayoutParams buttonParams = new TableRow.LayoutParams(-2, -2);
                    buttonParams.setMargins(columnMarging, 0, columnMarging, 0);
                    elementAsButton.setLayoutParams(buttonParams);
                    elementAsButton.setText(element.text);
                    elementAsButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_bar_btn_4_round));
                    elementAsButton.setTextColor(-1);
                    if (element.action != null && element.action.length() > 0) {
                        elementAsButton.setTag(element.action);
                        elementAsButton.setOnClickListener(this.pointsForTicketClickListener);
                    }
                    rowView.addView(elementAsButton);
                } else if (element.type.equals(Type.longLabel)) {
                    TextView elementAsLongLabel = new TextView(this);
                    elementAsLongLabel.setLayoutParams(new TableRow.LayoutParams(-1, -2));
                    elementAsLongLabel.setTextColor(-1);
                    elementAsLongLabel.setGravity(element.gravity);
                    if (element.bold) {
                        elementAsLongLabel.setTypeface(null, 1);
                    }
                    elementAsLongLabel.setText(element.text);
                    elementAsLongLabel.setBackgroundDrawable(getResources().getDrawable(R.drawable.nav_bar_btn_4_round));
                    ((TableRow.LayoutParams) elementAsLongLabel.getLayoutParams()).span = getResources().getInteger(R.integer.running_total:column_large_span);
                    rowView.addView(elementAsLongLabel);
                } else {
                    TextView elementAsText = new TextView(this);
                    elementAsText.setLayoutParams(new TableRow.LayoutParams(-2, -2));
                    elementAsText.setTextColor(-16777216);
                    elementAsText.setGravity(element.gravity);
                    if (element.bold) {
                        elementAsText.setTypeface(null, 1);
                    }
                    elementAsText.setText(element.text);
                    if (rowData.runningTotalElements.size() == 1) {
                        ((TableRow.LayoutParams) elementAsText.getLayoutParams()).span = getResources().getInteger(R.integer.running_total:column_large_span);
                    } else if (rowData.runningTotalElements.size() == 2 && rowData.runningTotalElements.indexOf(element) == 0) {
                        ((TableRow.LayoutParams) elementAsText.getLayoutParams()).span = getResources().getInteger(R.integer.running_total:column_small_span);
                    }
                    rowView.addView(elementAsText);
                }
            }
        }
    }

    public void setTaskResult(ArrayList<BookingRunningTotalRow> taskResult) {
        hideProgress(true);
        if (taskResult != null) {
            this.bookingProcess.runningTotalRows = taskResult;
            setDataInView();
            return;
        }
        showBookingAlert(this.bookingProcess.hasError() ? this.bookingProcess.getLastError(true) : null);
    }
}
