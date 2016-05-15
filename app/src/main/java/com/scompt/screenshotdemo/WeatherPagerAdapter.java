package com.scompt.screenshotdemo;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.scompt.screenshotdemo.models.WeatherDatum;

import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherPagerAdapter extends PagerAdapter {
    private final LayoutInflater layoutInflater;
    private final List<WeatherDatum> weatherDays;
    private final DateTimeFormatter dateFormat;

    public WeatherPagerAdapter(LayoutInflater layoutInflater, List<WeatherDatum> weatherDays) {
        this.layoutInflater = layoutInflater;
        this.weatherDays = weatherDays;
        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                                      .withZone(ZoneId.systemDefault());
    }

    @Override
    public int getCount() {
        return weatherDays.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        WeatherDatum weatherDatum = weatherDays.get(position);
        View view = layoutInflater.inflate(R.layout.view_weather, container, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.summaryTextView.setText(weatherDatum.summary());
        viewHolder.dateTextView.setText(dateFormat.format(weatherDatum.time()));
        viewHolder.iconImageView.setImageResource(weatherDatum.icon().iconResId);
        viewHolder.iconImageView.setContentDescription(weatherDatum.icon().name);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    class ViewHolder {
        @BindView(R.id.summary)
        TextView summaryTextView;

        @BindView(R.id.date)
        TextView dateTextView;

        @BindView(R.id.icon)
        ImageView iconImageView;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
