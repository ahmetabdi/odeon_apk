package uk.co.odeon.androidapp.util.calendar;

public class CalendarEntry {
    private Boolean allDay;
    private long beginTimeMillis;
    private String description;
    private long endTimeMillis;
    private String location;
    private boolean markAsAvailabilityFree;
    private boolean privateEntry;
    private String title;

    public CalendarEntry() {
        this.title = null;
        this.description = null;
        this.location = null;
        this.beginTimeMillis = System.currentTimeMillis();
        this.endTimeMillis = System.currentTimeMillis() + 3600000;
        this.allDay = null;
        this.privateEntry = true;
        this.markAsAvailabilityFree = true;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getBeginTimeMillis() {
        return this.beginTimeMillis;
    }

    public void setBeginTimeMillis(long beginTimeMillis) {
        this.beginTimeMillis = beginTimeMillis;
    }

    public long getEndTimeMillis() {
        return this.endTimeMillis;
    }

    public void setEndTimeMillis(long endTimeMillis) {
        this.endTimeMillis = endTimeMillis;
    }

    public Boolean getAllDay() {
        return this.allDay;
    }

    public void setAllDay(Boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isPrivateEntry() {
        return this.privateEntry;
    }

    public void setPrivateEntry(boolean privateEntry) {
        this.privateEntry = privateEntry;
    }

    public boolean isMarkAsAvailabilityFree() {
        return this.markAsAvailabilityFree;
    }

    public void setMarkAsAvailabilityFree(boolean markAsAvailabilityFree) {
        this.markAsAvailabilityFree = markAsAvailabilityFree;
    }

    public String toString() {
        String str = "{Title=%s;Desc=%s;begin=%d;end=%d;%s%s%s}";
        Object[] objArr = new Object[7];
        objArr[0] = this.title;
        objArr[1] = this.description;
        objArr[2] = Long.valueOf(this.beginTimeMillis);
        objArr[3] = Long.valueOf(this.endTimeMillis);
        objArr[4] = this.allDay.booleanValue() ? "allDay" : "";
        objArr[5] = this.privateEntry ? "private" : "";
        objArr[6] = this.markAsAvailabilityFree ? "availFree" : "";
        return String.format(str, objArr);
    }
}
