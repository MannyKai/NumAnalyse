package com.kimguo.numanalyse;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AnalyseActivity extends AppCompatActivity {

    private static final int[] DIGITALS= new int[]{0,1,2,3,4,5,6,7,8,9};

    private PieChart pieChart;
    private BarChart barChart;
    private String sNumbers;
    protected Typeface tfRegular;
    protected Typeface tfLight;

    private int[] numCounts = new int[10];
    private ArrayList<Integer> numColors = new ArrayList<>();
    private List<BarEntry> barchartArray = new ArrayList<>();
    private List<PieEntry> piechartArray = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        sNumbers = getIntent().getStringExtra("Numbers");
        tfRegular = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf");
        count(sNumbers);
        initialCharts();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private int count(String numbers)
    {
        char[] numberArray = numbers.toCharArray();
        for(char num : numberArray)
        {
            numCounts[Integer.valueOf(num) - 48]++;
        }
        return numberArray.length;
    }

    private void initialCharts()
    {
        barChart = findViewById(R.id.barchart);
        barChart.setOnChartValueSelectedListener(null);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.getDescription().setEnabled(false);

        // if more than 20 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(20);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        barChart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new BarChartXAxisValueFormatter(barChart);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfRegular);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new BarChartYAxisValueFormatter("");

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        l.setEnabled(false);

        XYMarkerView mv = new XYMarkerView(this, xAxisFormatter);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the chart
        setBarChartData(60);

        pieChart = findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setExtraOffsets(0.f, 0.f, 0.f, 0.f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(false);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);

        // add a selection listener
        pieChart.setOnChartValueSelectedListener(null);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);

        Legend piel = pieChart.getLegend();
        piel.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        piel.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        piel.setOrientation(Legend.LegendOrientation.VERTICAL);
        piel.setDrawInside(false);
        piel.setEnabled(false);

        setPieChartData(100);


//        for (int c : ColorTemplate.VORDIPLOM_COLORS)
//            numColors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            numColors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            numColors.add(c);
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
        return s;
    }

    private void setBarChartData(float range) {

        BarDataSet set1;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setValues(barchartArray);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();

        }
        else
            {
                for(int i=0; i<DIGITALS.length; i++)
                {
                    barchartArray.add(new BarEntry(DIGITALS[i],numCounts[i]));
                }
                set1 = new BarDataSet(barchartArray, "");

                set1.setDrawIcons(false);

                set1.setColors(numColors);
                ArrayList<IBarDataSet> dataSets = new ArrayList<>();
                dataSets.add(set1);

                BarData data = new BarData(dataSets);
                data.setValueTextSize(10f);
                data.setValueFormatter(new BarChartYAxisValueFormatter(""));
                data.setValueTypeface(tfLight);
                data.setBarWidth(0.9f);

                barChart.setData(data);
        }
    }

    private void setPieChartData(float range) {


        if(pieChart.getData() == null)
        {
            for (int i = 0; i < DIGITALS.length; i++) {
                piechartArray.add(new PieEntry(numCounts[i], String.valueOf(DIGITALS[i])));
            }
        }

        PieDataSet dataSet = new PieDataSet(piechartArray, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(15f);

        dataSet.setColors(numColors);
        //dataSet.setSelectionShift(0f);

        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.5f);
        dataSet.setValueLinePart2Length(0.8f);

        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new BarChartYAxisValueFormatter("%"));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tfRegular);
        pieChart.setData(data);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
