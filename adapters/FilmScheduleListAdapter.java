package uk.co.odeon.androidapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Iterator;
import uk.co.odeon.androidapp.R;
import uk.co.odeon.androidapp.custom.ReallySimpleFlowLayout;
import uk.co.odeon.androidapp.json.ScheduleAttribute;
import uk.co.odeon.androidapp.json.ScheduleDate;
import uk.co.odeon.androidapp.json.ScheduleDates;
import uk.co.odeon.androidapp.json.SchedulePerformance;

public class FilmScheduleListAdapter extends BaseAdapter {
    protected static final String TAG = "FilmScheduleListAdapter";
    private Context mContext;
    private OnClickListener mOnClickTime;
    private ScheduleDates mScheduleDates;

    public FilmScheduleListAdapter(Context context, ScheduleDates scheduleDates, OnClickListener onClickTime) {
        this.mContext = context;
        this.mScheduleDates = scheduleDates;
        this.mOnClickTime = onClickTime;
    }

    public int getCount() {
        return this.mScheduleDates.size();
    }

    public Object getItem(int position) {
        return this.mScheduleDates.get(position);
    }

    public long getItemId(int position) {
        return (long) position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout dayLayout;
        LayoutInflater inflater = (LayoutInflater) this.mContext.getSystemService("layout_inflater");
        if (convertView == null) {
            dayLayout = (LinearLayout) inflater.inflate(R.layout.film_schedule_day_element, null);
        } else {
            dayLayout = (LinearLayout) convertView;
        }
        ScheduleDate scheduleDate = (ScheduleDate) this.mScheduleDates.get(position);
        ((TextView) dayLayout.findViewById(R.id.scheduleDateText)).setText(scheduleDate.getName());
        LinearLayout attributesLayout = (LinearLayout) dayLayout.findViewById(R.id.scheduleDateAttributesLayout);
        while (attributesLayout.getChildCount() < scheduleDate.attributes.size()) {
            attributesLayout.addView((LinearLayout) inflater.inflate(R.layout.film_schedule_attribute_element, null));
        }
        while (attributesLayout.getChildCount() > scheduleDate.attributes.size()) {
            attributesLayout.removeViewAt(scheduleDate.attributes.size() - 1);
        }
        Iterator it = scheduleDate.attributes.iterator();
        while (it.hasNext()) {
            ScheduleAttribute scheduleAttribute = (ScheduleAttribute) it.next();
            LinearLayout attributeLayout = (LinearLayout) attributesLayout.getChildAt(scheduleDate.attributes.indexOf(scheduleAttribute));
            ((TextView) attributeLayout.findViewById(R.id.scheduleDateAttributeText)).setText(scheduleAttribute.getName());
            ReallySimpleFlowLayout attributeBoxLayout = (ReallySimpleFlowLayout) attributeLayout.findViewById(R.id.scheduleDateAttributeBox);
            while (attributeBoxLayout.getChildCount() < scheduleAttribute.performances.size()) {
                attributeBoxLayout.addView((LinearLayout) inflater.inflate(R.layout.film_schedule_time_element, null));
            }
            while (attributeBoxLayout.getChildCount() > scheduleAttribute.performances.size()) {
                attributeBoxLayout.removeViewAt(scheduleAttribute.performances.size() - 1);
            }
            Iterator it2 = scheduleAttribute.performances.iterator();
            while (it2.hasNext()) {
                SchedulePerformance schedulePerformance = (SchedulePerformance) it2.next();
                LinearLayout scheduleAttributeTimeLayout = (LinearLayout) attributeBoxLayout.getChildAt(scheduleAttribute.performances.indexOf(schedulePerformance));
                TextView scheduleAttributeTimeText = (TextView) scheduleAttributeTimeLayout.findViewById(R.id.timeElementTime);
                scheduleAttributeTimeText.setText(schedulePerformance.getTime());
                scheduleAttributeTimeText.setTag(scheduleDate.getName() + " " + schedulePerformance.getTime());
                scheduleAttributeTimeLayout.setTag(schedulePerformance.getID());
                scheduleAttributeTimeLayout.setOnClickListener(this.mOnClickTime);
            }
        }
        return dayLayout;
    }
}
