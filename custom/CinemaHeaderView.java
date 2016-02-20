package uk.co.odeon.androidapp.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import uk.co.odeon.androidapp.Constants.APP_LOCATION;
import uk.co.odeon.androidapp.ODEONApplication;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.provider.SiteContent.SiteColumns;

public class CinemaHeaderView extends RelativeLayout {
    private OnClickListener callClickListener;
    protected int cinemaId;
    protected String cinemaName;
    private OnClickListener favouriteClickListener;
    protected boolean folded;
    private OnClickListener infoClickListener;
    protected String phoneNumber;

    public CinemaHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.cinemaId = -1;
        this.cinemaName = null;
        this.phoneNumber = null;
        this.folded = true;
        this.infoClickListener = new OnClickListener() {
            public void onClick(View v) {
                boolean z = false;
                ODEONApplication.trackEvent("Information", "Click", CinemaHeaderView.this.cinemaName);
                View infoLayout = CinemaHeaderView.this.findViewById(R.id.cinemaHeaderInfoLayout);
                if (infoLayout != null) {
                    infoLayout.setVisibility(CinemaHeaderView.this.folded ? 0 : 8);
                    CinemaHeaderView cinemaHeaderView = CinemaHeaderView.this;
                    if (!CinemaHeaderView.this.folded) {
                        z = true;
                    }
                    cinemaHeaderView.folded = z;
                }
            }
        };
        this.callClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Information- Call", "Click", CinemaHeaderView.this.cinemaName);
                if (CinemaHeaderView.this.phoneNumber != null && CinemaHeaderView.this.phoneNumber.length() > 0) {
                    v.getContext().startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + CinemaHeaderView.this.phoneNumber)));
                }
            }
        };
        this.favouriteClickListener = new OnClickListener() {
            public void onClick(View v) {
                boolean isFavourite;
                boolean z;
                boolean z2 = false;
                if (v.getTag() instanceof Boolean) {
                    isFavourite = ((Boolean) v.getTag()).booleanValue();
                } else {
                    isFavourite = false;
                }
                if (isFavourite) {
                    ODEONApplication.trackEvent("Information- Remove from favourite Cinema", "Click", CinemaHeaderView.this.cinemaName);
                } else {
                    ODEONApplication.trackEvent("Information- Add to favourite Cinema", "Click", CinemaHeaderView.this.cinemaName);
                }
                ODEONApplication instance = ODEONApplication.getInstance();
                int i = CinemaHeaderView.this.cinemaId;
                if (isFavourite) {
                    z = false;
                } else {
                    z = true;
                }
                instance.changeCinemaFavouriteInDatabase(i, z);
                CinemaHeaderView cinemaHeaderView = CinemaHeaderView.this;
                ViewGroup viewGroup = (ViewGroup) v;
                if (!isFavourite) {
                    z2 = true;
                }
                cinemaHeaderView.setFavoriteDataInView(viewGroup, z2);
            }
        };
        init();
    }

    public CinemaHeaderView(Context context) {
        super(context);
        this.cinemaId = -1;
        this.cinemaName = null;
        this.phoneNumber = null;
        this.folded = true;
        this.infoClickListener = new OnClickListener() {
            public void onClick(View v) {
                boolean z = false;
                ODEONApplication.trackEvent("Information", "Click", CinemaHeaderView.this.cinemaName);
                View infoLayout = CinemaHeaderView.this.findViewById(R.id.cinemaHeaderInfoLayout);
                if (infoLayout != null) {
                    infoLayout.setVisibility(CinemaHeaderView.this.folded ? 0 : 8);
                    CinemaHeaderView cinemaHeaderView = CinemaHeaderView.this;
                    if (!CinemaHeaderView.this.folded) {
                        z = true;
                    }
                    cinemaHeaderView.folded = z;
                }
            }
        };
        this.callClickListener = new OnClickListener() {
            public void onClick(View v) {
                ODEONApplication.trackEvent("Information- Call", "Click", CinemaHeaderView.this.cinemaName);
                if (CinemaHeaderView.this.phoneNumber != null && CinemaHeaderView.this.phoneNumber.length() > 0) {
                    v.getContext().startActivity(new Intent("android.intent.action.CALL", Uri.parse("tel:" + CinemaHeaderView.this.phoneNumber)));
                }
            }
        };
        this.favouriteClickListener = new OnClickListener() {
            public void onClick(View v) {
                boolean isFavourite;
                boolean z;
                boolean z2 = false;
                if (v.getTag() instanceof Boolean) {
                    isFavourite = ((Boolean) v.getTag()).booleanValue();
                } else {
                    isFavourite = false;
                }
                if (isFavourite) {
                    ODEONApplication.trackEvent("Information- Remove from favourite Cinema", "Click", CinemaHeaderView.this.cinemaName);
                } else {
                    ODEONApplication.trackEvent("Information- Add to favourite Cinema", "Click", CinemaHeaderView.this.cinemaName);
                }
                ODEONApplication instance = ODEONApplication.getInstance();
                int i = CinemaHeaderView.this.cinemaId;
                if (isFavourite) {
                    z = false;
                } else {
                    z = true;
                }
                instance.changeCinemaFavouriteInDatabase(i, z);
                CinemaHeaderView cinemaHeaderView = CinemaHeaderView.this;
                ViewGroup viewGroup = (ViewGroup) v;
                if (!isFavourite) {
                    z2 = true;
                }
                cinemaHeaderView.setFavoriteDataInView(viewGroup, z2);
            }
        };
        init();
    }

    private void init() {
        ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.cinema_header_row, this);
        setBackgroundColor(-16777216);
        setGravity(16);
        setPadding((int) getResources().getDimension(R.dimen.padding_large), (int) getResources().getDimension(R.dimen.padding_small), (int) getResources().getDimension(R.dimen.padding_large), (int) getResources().getDimension(R.dimen.padding_small));
        View infoButton = findViewById(R.id.cinemaHeaderImage);
        if (infoButton != null) {
            infoButton.setOnClickListener(this.infoClickListener);
        }
        LinearLayout callLayout = (LinearLayout) findViewById(R.id.cinemaHeaderInfoCallLayout);
        if (callLayout != null) {
            callLayout.setOnClickListener(this.callClickListener);
        }
        LinearLayout favouriteLayout = (LinearLayout) findViewById(R.id.cinemaHeaderInfoFavoriteLayout);
        if (favouriteLayout != null) {
            favouriteLayout.setOnClickListener(this.favouriteClickListener);
        }
    }

    public void setDataInView(Activity activity, Cursor cursor) {
        if (cursor != null && cursor.getCount() > 0) {
            this.cinemaId = cursor.getInt(cursor.getColumnIndex("_id"));
            this.cinemaName = cursor.getString(cursor.getColumnIndex(SiteColumns.NAME));
            String name = cursor.getString(cursor.getColumnIndex(SiteColumns.NAME));
            TextView nameView = (TextView) activity.findViewById(R.id.cinemaHeaderText);
            if (nameView != null) {
                nameView.setText(name);
            }
            String addr = cursor.getString(cursor.getColumnIndex(SiteColumns.ADDR));
            if (addr != null) {
                addr = addr.replace("\n", ", ");
            }
            String postcode = cursor.getString(cursor.getColumnIndex(SiteColumns.POSTCODE));
            if (addr == null || addr.length() <= 0) {
                addr = postcode;
            } else {
                addr = new StringBuilder(String.valueOf(addr)).append(", ").append(postcode).toString();
            }
            TextView detailsView = (TextView) activity.findViewById(R.id.cinemaHeaderInfoDetailsText);
            if (nameView != null) {
                detailsView.setText(addr);
            }
            String phone = cursor.getString(cursor.getColumnIndex(SiteColumns.PHONE));
            this.phoneNumber = phone;
            TextView asteriskView = (TextView) activity.findViewById(R.id.cinemaHeaderInfoAsteriskText);
            boolean isPremiumRateNumber = false;
            for (String premiumRateNumberPrefix : ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? getResources().getStringArray(R.array.cinema_header_call_premium_rate_number_prefixes_ire) : getResources().getStringArray(R.array.cinema_header_call_premium_rate_number_prefixes)) {
                if (phone.startsWith(premiumRateNumberPrefix)) {
                    isPremiumRateNumber = true;
                    break;
                }
            }
            if (isPremiumRateNumber) {
                if (phone != null && phone.length() > 0) {
                    phone = new StringBuilder(String.valueOf(phone)).append(" *").toString();
                }
                if (asteriskView != null) {
                    asteriskView.setText(ODEONApplication.getInstance().getChoosenLocation().equals(APP_LOCATION.ire) ? R.string.cinema_header_cost_info_ire : R.string.cinema_header_cost_info);
                    asteriskView.setVisibility(0);
                }
            } else if (asteriskView != null) {
                asteriskView.setVisibility(4);
            }
            TextView callView = (TextView) activity.findViewById(R.id.cinemaHeaderInfoCallText);
            if (nameView != null) {
                callView.setText(phone);
            }
            setFavoriteDataInView((ViewGroup) findViewById(R.id.cinemaHeaderInfoFavoriteLayout), !cursor.isNull(cursor.getColumnIndex("favID")));
            RelativeLayout layout = (RelativeLayout) activity.findViewById(R.id.cinemaHeaderLayout);
            if (layout != null) {
                layout.setVisibility(0);
            }
        }
    }

    protected void setFavoriteDataInView(ViewGroup viewGroup, boolean isFavourite) {
        viewGroup.setTag(Boolean.valueOf(isFavourite));
        ImageView icon = (ImageView) findViewById(R.id.cinemaHeaderInfoFavoriteImage);
        TextView text = (TextView) findViewById(R.id.cinemaHeaderInfoFavoriteText);
        if (isFavourite) {
            if (icon != null) {
                icon.setImageResource(R.drawable.icon_tick);
            }
            if (text != null) {
                text.setText(R.string.film_info_btn_saved_favourite_cinema);
                return;
            }
            return;
        }
        if (icon != null) {
            icon.setImageResource(R.drawable.icon_add);
        }
        if (text != null) {
            text.setText(R.string.film_info_btn_save_favourite_cinema);
        }
    }
}
