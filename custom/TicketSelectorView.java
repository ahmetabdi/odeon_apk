package uk.co.odeon.androidapp.custom;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.model.BookingPrice;
import uk.co.odeon.androidapp.model.BookingSection;

public class TicketSelectorView extends RelativeLayout {
    protected TextView amountView;
    protected TextView countView;
    public final DecimalFormat decimalFormat;
    public Handler handler;
    protected Button minusButton;
    private OnClickListener minusClickListener;
    protected Button plusButton;
    private OnClickListener plusClickListener;
    protected BookingPrice priceData;
    protected BookingSection sectionData;

    public TicketSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.decimalFormat = new DecimalFormat(getResources().getString(R.string.app_decimal_format));
        this.plusClickListener = new OnClickListener() {
            public void onClick(View v) {
                TicketSelectorView.this.priceData.selectTicket();
                TicketSelectorView.this.handler.sendEmptyMessage(Constants.TICKET_LIST_RELOAD);
            }
        };
        this.minusClickListener = new OnClickListener() {
            public void onClick(View v) {
                TicketSelectorView.this.priceData.unselectTicket();
                TicketSelectorView.this.handler.sendEmptyMessage(Constants.TICKET_LIST_RELOAD);
            }
        };
        init();
    }

    public TicketSelectorView(Context context) {
        super(context);
        this.decimalFormat = new DecimalFormat(getResources().getString(R.string.app_decimal_format));
        this.plusClickListener = new OnClickListener() {
            public void onClick(View v) {
                TicketSelectorView.this.priceData.selectTicket();
                TicketSelectorView.this.handler.sendEmptyMessage(Constants.TICKET_LIST_RELOAD);
            }
        };
        this.minusClickListener = new OnClickListener() {
            public void onClick(View v) {
                TicketSelectorView.this.priceData.unselectTicket();
                TicketSelectorView.this.handler.sendEmptyMessage(Constants.TICKET_LIST_RELOAD);
            }
        };
        init();
    }

    private void init() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        this.decimalFormat.setDecimalFormatSymbols(dfs);
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.ticket_selector, this);
        this.countView = (TextView) findViewById(R.id.bookingTicketCount);
        this.amountView = (TextView) findViewById(R.id.bookingTicketAmount);
        this.plusButton = (Button) findViewById(R.id.bookingTicketPlusButton);
        if (this.plusButton != null) {
            this.plusButton.setOnClickListener(this.plusClickListener);
        }
        this.minusButton = (Button) findViewById(R.id.bookingTicketMinusButton);
        if (this.minusButton != null) {
            this.minusButton.setOnClickListener(this.minusClickListener);
        }
    }

    public void setDataInView(BookingSection section, BookingPrice price) {
        this.sectionData = section;
        this.priceData = price;
        refreshDataInView();
    }

    public void refreshDataInView() {
        int i;
        int i2 = R.integer.ticket_selection:alpha_enabled;
        boolean z = true;
        if (this.countView != null) {
            this.countView.setText(new StringBuilder(String.valueOf(this.priceData.selected)).append(getResources().getString(R.string.tickets_count_text)).toString());
        }
        if (this.amountView != null) {
            int currencyLocaleInt = ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? R.string.tickets_currency_ire : R.string.tickets_currency;
            TextView textView = this.amountView;
            StringBuilder stringBuilder = new StringBuilder(String.valueOf(getResources().getString(currencyLocaleInt)));
            DecimalFormat decimalFormat = this.decimalFormat;
            float f = this.priceData.amount;
            if (this.priceData.selected > 0) {
                i = this.priceData.selected;
            } else {
                i = 1;
            }
            textView.setText(stringBuilder.append(decimalFormat.format((double) (((float) Math.round(((((float) i) * f) * ((float) this.priceData.chunk)) * 100.0f)) / 100.0f))).toString());
        }
        if (this.plusButton != null) {
            boolean isTicketPossible = isTicketPossible();
            this.plusButton.setEnabled(isTicketPossible);
            Drawable background = this.plusButton.getBackground();
            Resources resources = getResources();
            if (isTicketPossible) {
                i = R.integer.ticket_selection:alpha_enabled;
            } else {
                i = R.integer.ticket_selection:alpha_disabled;
            }
            background.setAlpha(resources.getInteger(i));
        }
        if (this.minusButton != null) {
            Button button = this.minusButton;
            if (this.priceData.selected <= 0) {
                z = false;
            }
            button.setEnabled(z);
            Drawable background2 = this.minusButton.getBackground();
            Resources resources2 = getResources();
            if (this.priceData.selected <= 0) {
                i2 = R.integer.ticket_selection:alpha_disabled;
            }
            background2.setAlpha(resources2.getInteger(i2));
        }
    }

    private boolean isTicketPossible() {
        return this.sectionData.getSelectedTicketCount() + this.priceData.chunk <= 9;
    }
}
