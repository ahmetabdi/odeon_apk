package uk.co.odeon.androidapp.model;

public class BookingPrice implements Comparable<BookingPrice> {
    public float amount;
    public int chunk;
    public int count;
    public String id;
    public boolean is3d;
    public String name;
    public int sectionId;
    public int selected;
    public String subscription;

    public BookingPrice() {
        this.selected = 0;
    }

    public int compareTo(BookingPrice another) {
        if (this.id == null || another.id == null) {
            return -1;
        }
        if (this.id.equals(another.id)) {
            return 0;
        }
        return 1;
    }

    public int selectTicket() {
        if (this.count == 0 || (this.selected + 1) * this.chunk <= this.count) {
            this.selected++;
        } else {
            this.selected = this.count;
        }
        return this.selected;
    }

    public int unselectTicket() {
        if ((this.selected * this.chunk) - this.chunk > 0) {
            this.selected--;
        } else {
            this.selected = 0;
        }
        return this.selected;
    }
}
