package com.kimguo.numanalyse;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

/**
 * Created by philipp on 02/06/16.
 */
public class BarChartXAxisValueFormatter extends ValueFormatter
{

    private final BarLineChartBase<?> chart;

    public BarChartXAxisValueFormatter(BarLineChartBase<?> chart) {
        this.chart = chart;
    }

    @Override
    public String getFormattedValue(float value) {

        return String.valueOf((int)value );
    }
}
