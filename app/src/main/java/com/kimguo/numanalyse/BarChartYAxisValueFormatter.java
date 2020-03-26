package com.kimguo.numanalyse;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

public class BarChartYAxisValueFormatter extends ValueFormatter
{

    private final DecimalFormat mFormat;
    private final DecimalFormat mFormat2;
    private String suffix;

    public BarChartYAxisValueFormatter(String suffix) {
        mFormat = new DecimalFormat("###,###,###,##0");
        mFormat2 = new DecimalFormat("###,###,###,##0.0");
        this.suffix = suffix;
    }

    @Override
    public String getFormattedValue(float value) {
        if(suffix.length() > 0)
            return mFormat2.format(value) + suffix;
        return mFormat.format(value);
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        if (value > 0) {
            return mFormat.format(value) + suffix;
        } else {
            return mFormat.format(value);
        }
    }
}
