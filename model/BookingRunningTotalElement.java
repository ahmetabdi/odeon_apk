package uk.co.odeon.androidapp.model;

public class BookingRunningTotalElement {
    public String action;
    public boolean bold;
    public int gravity;
    public String text;
    public Type type;

    public enum Type {
        label,
        longLabel,
        button
    }
}
