package uk.co.odeon.androidapp.model;

import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class BookingSeat {
    public int height;
    public int neighbourLeft;
    public int neighbourRight;
    public int number;
    public int sectionId;
    public boolean selected;
    public State state;
    public Type type;
    public int width;
    public int xPosition;
    public int yPosition;

    public enum State {
        broken,
        blocked,
        free,
        reserved,
        sold,
        prepaid,
        lockedForSale,
        lockedForReservations,
        lockedForSaleAndReservations,
        unknown;

        public static State valueOf(int i) {
            switch (i) {
                case AmazingListView.PINNED_HEADER_GONE /*0*/:
                    return broken;
                case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    return blocked;
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    return free;
                case RootActivity.TYPE_RIGHT /*3*/:
                    return reserved;
                case RootActivity.TYPE_SINGLE /*4*/:
                    return sold;
                case R.styleable.com_deezapps_widget_PagerControl_useCircles /*5*/:
                    return prepaid;
                case R.styleable.com_deezapps_widget_PagerControl_circlePadding /*6*/:
                    return lockedForSale;
                case 7:
                    return lockedForReservations;
                case 13:
                    return lockedForSaleAndReservations;
                default:
                    return unknown;
            }
        }
    }

    public enum Type {
        regular,
        love,
        wheelchair,
        house,
        removable,
        unknown;

        public static Type valueOf(int i) {
            switch (i) {
                case AmazingListView.PINNED_HEADER_VISIBLE /*1*/:
                    return regular;
                case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                    return love;
                case RootActivity.TYPE_RIGHT /*3*/:
                    return wheelchair;
                case RootActivity.TYPE_SINGLE /*4*/:
                    return house;
                case R.styleable.com_deezapps_widget_PagerControl_useCircles /*5*/:
                    return removable;
                default:
                    return unknown;
            }
        }
    }

    public boolean isOccupied() {
        return isSpecialSeat() || isBookedOrReserved();
    }

    public boolean isSpecialSeat() {
        return (this.type.equals(Type.regular) || this.type.equals(Type.love) || this.type.equals(Type.wheelchair)) ? false : true;
    }

    public boolean isBookedOrReserved() {
        return this.state != State.free;
    }
}
