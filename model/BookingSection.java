package uk.co.odeon.androidapp.model;

import java.util.ArrayList;
import java.util.List;
import uk.co.odeon.androidapp.model.BookingSeat.Type;

public class BookingSection {
    public int highestTicketChunk;
    public int id;
    public Mode mode;
    public String name;
    public final List<BookingPrice> prices;
    public String rawColor;
    public final List<BookingSeat> seats;
    public boolean selected;
    public String warning;

    public enum Mode {
        unreserved,
        reserved,
        bestAvailable
    }

    public BookingSection() {
        this.prices = new ArrayList();
        this.seats = new ArrayList();
    }

    public String getSelectedColor() {
        return "#FF" + this.rawColor;
    }

    public String getUnselectedColor() {
        return "#44" + this.rawColor;
    }

    public void clearTicketSelection() {
        for (BookingPrice price : this.prices) {
            price.selected = 0;
        }
    }

    public String getSelectedTicketsAsString() {
        String ticketsString = "";
        for (BookingPrice sp : this.prices) {
            if (sp.selected > 0) {
                ticketsString = new StringBuilder(String.valueOf(ticketsString)).append(ticketsString == "" ? "" : ";").append(sp.id).append("___").append(sp.selected).toString();
            }
        }
        return ticketsString;
    }

    public int getSelectedTicketCount() {
        int count = 0;
        for (BookingPrice sp : this.prices) {
            if (sp.selected > 0) {
                count += sp.selected * sp.chunk;
            }
        }
        return count;
    }

    public int selectSeats(BookingSeat seat) {
        int count = getSelectedTicketCount() - getSelectedSeatsCount();
        if (this.id != seat.sectionId) {
            return -1;
        }
        int numberFreeSeatsLeft = 0;
        int numberFreeSeatsRight = 0;
        BookingSeat seatCursor = seat;
        while (seatCursor != null && !seatCursor.isBookedOrReserved() && !seatCursor.selected) {
            numberFreeSeatsLeft++;
            if (seatCursor.neighbourLeft > 0) {
                seatCursor = getSeatByNumber(seatCursor.neighbourLeft);
            } else {
                seatCursor = null;
            }
        }
        numberFreeSeatsLeft--;
        seatCursor = seat;
        while (seatCursor != null && !seatCursor.isBookedOrReserved() && !seatCursor.selected) {
            numberFreeSeatsRight++;
            if (seatCursor.neighbourRight > 0) {
                seatCursor = getSeatByNumber(seatCursor.neighbourRight);
            } else {
                seatCursor = null;
            }
        }
        numberFreeSeatsRight -= count;
        if (numberFreeSeatsRight == 1 && numberFreeSeatsLeft > 1) {
            return -1;
        }
        if (numberFreeSeatsLeft == 1 && numberFreeSeatsRight > 1) {
            return -1;
        }
        seatCursor = seat;
        int seatsSelectedCount = 0;
        while (seatsSelectedCount < count && seatCursor != null && !seatCursor.isBookedOrReserved() && !seatCursor.selected) {
            seatCursor.selected = true;
            if (seatCursor.neighbourRight <= 0) {
                break;
            }
            seatCursor = getSeatByNumber(seatCursor.neighbourRight);
            seatsSelectedCount++;
        }
        if (seatsSelectedCount > 0) {
            return 1;
        }
        return 0;
    }

    public void clearSeatSelection() {
        for (BookingSeat seat : this.seats) {
            seat.selected = false;
        }
    }

    public List<BookingSeat> getSelectedSeats() {
        List<BookingSeat> selectedSeats = new ArrayList();
        for (BookingSeat seat : this.seats) {
            if (seat.selected) {
                selectedSeats.add(seat);
            }
        }
        return selectedSeats;
    }

    public int getSelectedSeatsCount() {
        int count = 0;
        for (BookingSeat seat : this.seats) {
            if (seat.selected) {
                count++;
            }
        }
        return count;
    }

    public boolean isWheelchairSelected() {
        return isWheelchairSelected(null);
    }

    public boolean isWheelchairSelected(List<BookingSeat> selectedSeats) {
        if (selectedSeats == null) {
            selectedSeats = getSelectedSeats();
        }
        for (BookingSeat seat : selectedSeats) {
            if (seat.type.equals(Type.wheelchair)) {
                return true;
            }
        }
        return false;
    }

    public String getSelectedSeatsAsString() {
        String seatsString = "";
        for (BookingSeat ss : this.seats) {
            if (ss.selected) {
                seatsString = new StringBuilder(String.valueOf(seatsString)).append(seatsString == "" ? "" : ";").append(ss.number).toString();
            }
        }
        return seatsString;
    }

    public BookingSeat getSeatByNumber(int number) {
        for (BookingSeat seat : this.seats) {
            if (seat.number == number) {
                return seat;
            }
        }
        return null;
    }

    public boolean hasWarning() {
        if (this.warning == null || this.warning.length() <= 0) {
            return false;
        }
        return true;
    }
}
