package uk.co.odeon.androidapp.model;

import java.util.ArrayList;

public class BookingProcess {
    private static BookingProcess bookingProcess;
    public String bookingSessionHash;
    public String bookingSessionId;
    public String cardHandlingFeeInfoTextRunningTotal;
    public String cardHandlingFeeInfoTextTicketSelection;
    public float cardHandlingFeePerTicket;
    public String contactNumber;
    public String email;
    public String firstname;
    public String headerFilmTitle;
    public String headerFormated;
    private String lastError;
    public String lastname;
    public ArrayList<BookingRunningTotalRow> runningTotalRows;
    public BookingSeatingData seatingData;
    public String title;

    public BookingProcess() {
        this.bookingSessionHash = null;
        this.bookingSessionId = null;
        this.firstname = null;
        this.lastname = null;
        this.title = null;
        this.email = null;
        this.contactNumber = null;
        this.cardHandlingFeePerTicket = 0.0f;
        this.cardHandlingFeeInfoTextTicketSelection = "";
        this.cardHandlingFeeInfoTextRunningTotal = "";
        this.seatingData = null;
        this.runningTotalRows = null;
        this.lastError = null;
    }

    static {
        bookingProcess = null;
    }

    public static synchronized BookingProcess getInstance() {
        BookingProcess bookingProcess;
        synchronized (BookingProcess.class) {
            if (bookingProcess == null) {
                bookingProcess = new BookingProcess();
                bookingProcess = bookingProcess;
            } else {
                bookingProcess = bookingProcess;
            }
        }
        return bookingProcess;
    }

    public boolean hasError() {
        return this.lastError != null;
    }

    public String getLastError() {
        return getLastError(false);
    }

    public String getLastError(boolean clear) {
        if (!clear) {
            return this.lastError;
        }
        String error = new String(this.lastError);
        this.lastError = null;
        return error;
    }

    public void setLastError(String lastError) {
        this.lastError = lastError;
    }
}
