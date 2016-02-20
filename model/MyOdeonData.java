package uk.co.odeon.androidapp.model;

import java.util.HashMap;
import uk.co.odeon.androidapp.custom.NavigatorBarActivity.RootActivity;
import uk.co.odeon.androidapp.util.amazinglist.AmazingListView;

public class MyOdeonData {
    private static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type = null;
    public static final String DUMMY_TEXT_CINEMA = "You haven't yet chosen any favourites.";
    public static final String DUMMY_TEXT_FILM = "You haven't yet saved any films to later see.";
    public static final String DUMMY_TEXT_OFFER = "Check back later for news and offers.";
    public static final String KEY_DUMMY_TEXT = "dummyText";
    public boolean deletable;
    public Type type;
    public HashMap<String, Object> values;

    public enum Type {
        dummy,
        offer,
        cinema,
        film
    }

    static /* synthetic */ int[] $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type() {
        int[] iArr = $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type;
        if (iArr == null) {
            iArr = new int[Type.values().length];
            try {
                iArr[Type.cinema.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }
            try {
                iArr[Type.dummy.ordinal()] = 1;
            } catch (NoSuchFieldError e2) {
            }
            try {
                iArr[Type.film.ordinal()] = 4;
            } catch (NoSuchFieldError e3) {
            }
            try {
                iArr[Type.offer.ordinal()] = 2;
            } catch (NoSuchFieldError e4) {
            }
            $SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type = iArr;
        }
        return iArr;
    }

    public MyOdeonData() {
        this.values = new HashMap();
        this.deletable = false;
    }

    public static MyOdeonData createDummy(Type type) {
        MyOdeonData dummy = new MyOdeonData();
        dummy.type = Type.dummy;
        switch ($SWITCH_TABLE$uk$co$odeon$androidapp$model$MyOdeonData$Type()[type.ordinal()]) {
            case AmazingListView.PINNED_HEADER_PUSHED_UP /*2*/:
                dummy.values.put(KEY_DUMMY_TEXT, DUMMY_TEXT_OFFER);
                break;
            case RootActivity.TYPE_RIGHT /*3*/:
                dummy.values.put(KEY_DUMMY_TEXT, DUMMY_TEXT_CINEMA);
                break;
            case RootActivity.TYPE_SINGLE /*4*/:
                dummy.values.put(KEY_DUMMY_TEXT, DUMMY_TEXT_FILM);
                break;
            default:
                dummy.values.put(KEY_DUMMY_TEXT, "");
                break;
        }
        return dummy;
    }

    public boolean isDummy() {
        if (this.values.containsKey(KEY_DUMMY_TEXT)) {
            return true;
        }
        return false;
    }
}
