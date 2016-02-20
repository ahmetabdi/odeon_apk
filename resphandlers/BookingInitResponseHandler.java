package uk.co.odeon.androidapp.resphandlers;

import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.co.odeon.androidapp.Constants;
import uk.co.odeon.androidapp.model.BookingPrice;
import uk.co.odeon.androidapp.model.BookingProcess;
import uk.co.odeon.androidapp.model.BookingSeat;
import uk.co.odeon.androidapp.model.BookingSeat.State;
import uk.co.odeon.androidapp.model.BookingSeat.Type;
import uk.co.odeon.androidapp.model.BookingSeatingData;
import uk.co.odeon.androidapp.model.BookingSection;
import uk.co.odeon.androidapp.model.BookingSection.Mode;
import uk.co.odeon.androidapp.provider.OfferContent.OfferColumns;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;
import uk.co.odeon.androidapp.util.http.AbstractTypedJSONResponseHandler;

public class BookingInitResponseHandler extends AbstractTypedJSONResponseHandler<Boolean> {
    private static final String TAG;

    static {
        TAG = BookingInitResponseHandler.class.getSimpleName();
    }

    public boolean handleJSONRepsonse(JSONObject jsonObj, Uri uri) {
        if (jsonObj != null) {
            JSONObject config = jsonObj.optJSONObject("config");
            if (config == null) {
                Log.e(TAG, "Received JSON object without config.");
                return false;
            }
            Log.d(TAG, "Received config: " + config);
            JSONObject data = jsonObj.optJSONObject("data");
            if (data == null) {
                Log.e(TAG, "Received JSON object without data.");
                return false;
            }
            Log.d(TAG, "Received data: " + data);
            BookingProcess bookingProcess = BookingProcess.getInstance();
            if (config.optString("action") == null || !config.optString("action").equals("bookingError")) {
                try {
                    jsonConfigToModel(config, data, bookingProcess);
                    if (bookingProcess.bookingSessionHash == null || bookingProcess.bookingSessionId == null) {
                        Log.e(TAG, "Failed to init booking. No bookingSessionHash and bookingSessionId received.");
                        return false;
                    }
                    bookingProcess.seatingData = jsonDataToModel(data);
                    setResult(Boolean.valueOf(true));
                    return true;
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to init booking. JSON object was not parsable.", e);
                    return false;
                }
            } else if (data.optJSONObject("errorText") == null || data.optJSONObject("errorText").optString("errorText") == null) {
                Log.e(TAG, "Error received by JSON config but no error message available.");
                return false;
            } else {
                bookingProcess.setLastError(data.optJSONObject("errorText").optString("errorText"));
                Log.e(TAG, "Error received by JSON config with message: " + bookingProcess.getLastError());
                return false;
            }
        }
        Log.e(TAG, "Received JSON object is NULL.");
        return false;
    }

    protected BookingProcess jsonConfigToModel(JSONObject config, JSONObject data, BookingProcess bookingProcess) throws JSONException {
        bookingProcess.bookingSessionHash = config.optString("bookingSessionHash");
        bookingProcess.bookingSessionId = config.optString("bookingSessionId");
        JSONObject userDetailsData = data.optJSONObject("userDetails");
        if (userDetailsData != null) {
            bookingProcess.firstname = userDetailsData.optString(Constants.CUSTOMER_PREFS_FIRSTNAME);
            bookingProcess.lastname = userDetailsData.optString(Constants.CUSTOMER_PREFS_LASTNAME);
            bookingProcess.title = userDetailsData.optString(OfferColumns.TITLE);
            bookingProcess.email = userDetailsData.optString(Constants.CUSTOMER_PREFS_EMAIL);
        }
        JSONObject headerData = data.optJSONObject("headerData");
        if (headerData != null) {
            bookingProcess.headerFilmTitle = headerData.optString("filmTitle");
            bookingProcess.headerFormated = headerData.optString("formatedHeader");
        }
        JSONObject feesData = data.optJSONObject("fees");
        if (feesData != null) {
            JSONObject cardHandlingFeeData = feesData.optJSONObject("cardHandlingFee");
            if (cardHandlingFeeData != null) {
                bookingProcess.cardHandlingFeePerTicket = (float) cardHandlingFeeData.optDouble("fee", 0.0d);
                bookingProcess.cardHandlingFeeInfoTextTicketSelection = cardHandlingFeeData.optString("infoTextTicketSelection", "");
                bookingProcess.cardHandlingFeeInfoTextRunningTotal = cardHandlingFeeData.optString("infoTextRunningTotal", "");
            }
        }
        return bookingProcess;
    }

    protected BookingSeatingData jsonDataToModel(JSONObject data) throws JSONException {
        BookingSeatingData bsd = new BookingSeatingData();
        JSONObject seatPlanSizeData = data.optJSONObject("seatPlanSize");
        if (seatPlanSizeData != null) {
            bsd.seatingPlanWidth = seatPlanSizeData.optInt("seatingPlanWidth");
            bsd.seatingPlanHeight = seatPlanSizeData.optInt("seatingPlanHeight");
        }
        JSONArray sectionsData = data.optJSONArray("sections");
        if (sectionsData != null) {
            for (int n = 0; n < sectionsData.length(); n++) {
                JSONObject sectionObject = sectionsData.optJSONObject(n);
                if (sectionObject != null) {
                    bsd.sections.add(jsonDataToSection(sectionObject, n));
                }
            }
        }
        return bsd;
    }

    protected BookingSection jsonDataToSection(JSONObject sectionsData, int count) throws JSONException {
        BookingSection section = new BookingSection();
        section.id = count;
        section.name = sectionsData.optString(SiteColumns.NAME);
        section.rawColor = sectionsData.optJSONObject("color") != null ? sectionsData.optJSONObject("color").getString("html") : "FFFFFF";
        section.mode = Mode.valueOf(sectionsData.optString("mode"));
        section.warning = sectionsData.optString("warning");
        section.seats.addAll(jsonDataToSeats(sectionsData.optString("seatsString"), section));
        section.prices.addAll(jsonDataToPrices(sectionsData.optJSONObject("tickets"), section));
        return section;
    }

    protected List<BookingSeat> jsonDataToSeats(String seatsData, BookingSection section) throws JSONException {
        List<BookingSeat> seats = new ArrayList();
        if (seatsData != null) {
            String[] seatData = seatsData.split(";");
            for (String split : seatData) {
                BookingSeat seat = new BookingSeat();
                String[] seatFields = split.split("\\|");
                try {
                    seat.sectionId = section.id;
                    seat.number = Integer.parseInt(seatFields[0]);
                    seat.xPosition = Integer.parseInt(seatFields[1]);
                    seat.yPosition = Integer.parseInt(seatFields[2]);
                    seat.width = Integer.parseInt(seatFields[3]);
                    seat.height = Integer.parseInt(seatFields[4]);
                    seat.type = Type.valueOf(Integer.parseInt(seatFields[5]));
                    seat.neighbourLeft = Integer.parseInt(seatFields[6]);
                    seat.neighbourRight = Integer.parseInt(seatFields[7]);
                    seat.state = State.valueOf(Integer.parseInt(seatFields[8]));
                    seats.add(seat);
                } catch (ArrayIndexOutOfBoundsException eob) {
                    Log.e(TAG, "Failed to parse seat: Seat string is to short.", eob);
                } catch (NumberFormatException enf) {
                    Log.e(TAG, "Failed to parse seat: Seat string part is not a int as expected.", enf);
                }
            }
        }
        return seats;
    }

    protected List<BookingPrice> jsonDataToPrices(JSONObject pricesData, BookingSection section) throws JSONException {
        List<BookingPrice> prices = new ArrayList();
        if (pricesData != null) {
            JSONArray p3ds = pricesData.optJSONArray("3d");
            for (int n = 0; n < p3ds.length(); n++) {
                BookingPrice price3d = jsonDataToPrice(p3ds.optJSONObject(n), section);
                if (price3d != null) {
                    prices.add(price3d);
                }
            }
            JSONArray pds = pricesData.optJSONArray("default");
            for (int m = 0; m < pds.length(); m++) {
                BookingPrice priceDefault = jsonDataToPrice(pds.optJSONObject(m), section);
                if (priceDefault != null) {
                    prices.add(priceDefault);
                }
            }
        }
        return prices;
    }

    protected BookingPrice jsonDataToPrice(JSONObject priceData, BookingSection section) throws JSONException {
        boolean z = true;
        if (priceData == null) {
            return null;
        }
        BookingPrice price = new BookingPrice();
        price.id = priceData.optString("id");
        price.sectionId = section.id;
        price.name = priceData.optString(SiteColumns.NAME);
        price.subscription = priceData.optString("subscription");
        price.amount = (float) priceData.optDouble("amount");
        price.chunk = priceData.optInt("chunk");
        price.count = priceData.optInt("count");
        if (priceData.optInt("is3d") != 1) {
            z = false;
        }
        price.is3d = z;
        section.highestTicketChunk = Math.max(section.highestTicketChunk, price.chunk);
        return price;
    }
}
