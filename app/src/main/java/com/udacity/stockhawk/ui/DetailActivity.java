package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.FloatProperty;
import android.util.Log;

import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utility;
import com.udacity.stockhawk.data.Contract;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, OnChartGestureListener, OnChartValueSelectedListener {

    private static final int DETAIL_LOADER = 1;

    public static final String[] DETAIL_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_OPEN,
            Contract.Quote.COLUMN_DAYHIGH,
            Contract.Quote.COLUMN_DAYLOW,
            Contract.Quote.COLUMN_YEARHIGH,
            Contract.Quote.COLUMN_YEARLOW,
            Contract.Quote.COLUMN_VOLUME,
            Contract.Quote.COLUMN_AVGVOLUME,
            Contract.Quote.COLUMN_MKTCAP,
            Contract.Quote.COLUMN_EPS,
            Contract.Quote.COLUMN_PE,
            Contract.Quote.COLUMN_HISTORY
    };

    public static final int COL_SYMBOL = 0;
    public static final int COL_PRICE = 1;
    public static final int COL_CHANGE_ABS = 2;
    public static final int COL_CHANGE_PC = 3;
    public static final int COL_OPEN = 4;
    public static final int COL_DAYHIGH = 5;
    public static final int COL_DAYLOW = 6;
    public static final int COL_YRHIGH = 7;
    public static final int COL_YRLOW = 8;
    public static final int COL_VOLUME = 9;
    public static final int COL_AVGVOLUME = 10;
    public static final int COL_MKTCAP = 11;
    public static final int COL_EPS = 12;
    public static final int COL_PE = 13;
    public static final int COL_HISTORY = 14;

    private LineChart mChart;
    private Uri mUri;
    private String mSymbol;
    private String mHistory;
    private float mPrice;
    private float mChangeAbs;
    private float mChangePerc;
    private float mOpen;
    private float mDayhigh;
    private float mDaylow;
    private float mYrhigh;
    private float mYrlow;
    private int mVolume;
    private int mAvgVolume;
    private float mMktCap;
    private float mEPS;
    private float mPE;

    private List<String[]> mHistoryList;

    @BindView(R.id.detail_price)
    TextView mPriceText;
    @BindView(R.id.detail_change_abs)
    TextView mChangeabsText;
    @BindView(R.id.detail_change_pc)
    TextView mChangepctText;
    @BindView(R.id.open_value_text)
    TextView mOpenText;
    @BindView(R.id.mktcap_value_text)
    TextView mMktcapText;
    @BindView(R.id.dayhigh_value_text)
    TextView mDayhighText;
    @BindView(R.id.daylow_value_text)
    TextView mDaylowText;
    @BindView(R.id.yrhigh_value_text)
    TextView mYrhighText;
    @BindView(R.id.yrlow_value_text)
    TextView mYrlowText;
    @BindView(R.id.vol_value_text)
    TextView mVolText;
    @BindView(R.id.avgvol_value_text)
    TextView mAvgvolText;
    @BindView(R.id.eps_value_text)
    TextView mEpsText;
    @BindView(R.id.pe_value_text)
    TextView mPeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        mUri = getIntent().getData();
        getSupportLoaderManager().initLoader(DETAIL_LOADER, null, this);

        mChart = (LineChart) findViewById(R.id.linechart);
        setUpChart(mChart);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    private void setData(List<Entry> values) {

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values, "DataSet 1");

            set1.setColor(Color.GRAY);
            set1.setDrawCircles(false);
            //set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(0f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                Drawable drawable = ContextCompat.getDrawable(this, R.drawable.chart_fade);
                set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(dataSets);

            // set data
            mChart.setData(data);
        }
    }

    private List<Entry> getHistoryDataFromString(String history){
        List<Entry> entries = new ArrayList<Entry>();
        mHistoryList = new ArrayList<String[]>();

        String[] result = history.split("\n");
        for (int i =0; i<result.length ; i++){
            String data = result[result.length - i-1];
            String[] xydata = data.split(", ");
            mHistoryList.add(xydata);
            Entry entry = new Entry(i, (float)(Double.parseDouble(xydata[1])));
            entries.add(entry);
        }

        return entries;
    }


    private void setUpChart(LineChart chart){
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        mChart.getDescription().setEnabled(false);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new DateAxisFormatter());

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setTextColor(Color.WHITE);


        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);

        mChart.animateX(2500);

        mChart.getLegend().setEnabled(false);


    }

    private void setTodayLimitLine(){
        LimitLine todayLine = new LimitLine(mPrice, "Today");
        todayLine.setLineColor(Color.RED);
        todayLine.setLineWidth(4f);
        todayLine.setTextSize(10f);
        todayLine.setTextColor(Color.WHITE);
        todayLine.enableDashedLine(10f, 10f, 0f);
        todayLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        mChart.getAxisLeft().addLimitLine(todayLine);
    }


    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            mChart.highlightValues(null); // or highlightTouch(null) for callback to onNothingSelected(...)
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: " + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleX() + ", high: " + mChart.getHighestVisibleX());
        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin() + ", xmax: " + mChart.getXChartMax() + ", ymin: " + mChart.getYChartMin() + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    this,
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if(data != null && data.moveToFirst()){
            mSymbol = data.getString(COL_SYMBOL);
            setTitle(mSymbol);


            mChangeAbs = data.getFloat(COL_CHANGE_ABS);
            String change = Utility.getDollarFormatWithPlus().format(mChangeAbs);
            if(mChangeAbs > 0)
                mChangeabsText.setTextColor(getResources().getColor(R.color.material_green_700));
            else
                mChangeabsText.setTextColor(getResources().getColor(R.color.material_red_700));
            mChangeabsText.setText(change);
            mChangeabsText.setContentDescription(change);

            mChangePerc = data.getFloat(COL_CHANGE_PC);
            String percentage = Utility.getPercentageFormat().format(mChangePerc / 100f);
            if(mChangePerc > 0)
                mChangepctText.setTextColor(getResources().getColor(R.color.material_green_700));
            else
                mChangepctText.setTextColor(getResources().getColor(R.color.material_red_700));
            mChangepctText.setText(percentage);
            mChangepctText.setContentDescription(percentage);

            mPrice = data.getFloat(COL_PRICE);
            String price = Utility.getDollarFormat().format(mPrice);
            mPriceText.setText(price);
            mPriceText.setContentDescription(price);

            mOpen = data.getFloat(COL_OPEN);
            String open = Utility.getDollarFormat().format(mOpen);
            mOpenText.setText(open);
            mOpenText.setContentDescription(open);

            mDayhigh = data.getFloat(COL_DAYHIGH);
            String dayhigh = Utility.getDollarFormat().format(mDayhigh);
            mDayhighText.setText(dayhigh);
            mDayhighText.setContentDescription(dayhigh);

            mDaylow = data.getFloat(COL_DAYLOW);
            String daylow = Utility.getDollarFormat().format(mDaylow);
            mDaylowText.setText(daylow);
            mDaylowText.setContentDescription(daylow);

            mYrhigh = data.getFloat(COL_YRHIGH);
            String yrhigh = Utility.getDollarFormat().format(mYrhigh);
            mYrhighText.setText(yrhigh);
            mYrhighText.setContentDescription(yrhigh);

            mYrlow = data.getFloat(COL_YRLOW);
            String yrlow = Utility.getDollarFormat().format(mYrlow);
            mYrlowText.setText(yrlow);
            mYrlowText.setContentDescription(yrlow);

            mVolume = data.getInt(COL_VOLUME);
            String vol = Utility.getFormattedTextFromtNumber(mVolume);
            mVolText.setText(vol);
            mVolText.setContentDescription(vol);

            mAvgVolume = data.getInt(COL_AVGVOLUME);
            String avgvol = Utility.getFormattedTextFromtNumber(mAvgVolume);
            mAvgvolText.setText(avgvol);
            mAvgvolText.setContentDescription(avgvol);

            mMktCap = data.getFloat(COL_MKTCAP);
            String mktcap = Utility.getFormattedTextFromtNumber(mMktCap);
            mMktcapText.setText(mktcap);
            mMktcapText.setContentDescription(mktcap);

            mEPS = data.getFloat(COL_EPS);
            mEpsText.setText(String.valueOf(mEPS));
            mEpsText.setContentDescription(String.valueOf(mEPS));

            mPE = data.getFloat(COL_PE);
            mPeText.setText(String.valueOf(mPE));
            mPeText.setContentDescription(String.valueOf(mPE));

            mHistory = data.getString(COL_HISTORY);
            setData(getHistoryDataFromString(mHistory));

            setTodayLimitLine();

        }
    }



    @Override
    public void onLoaderReset(Loader loader) {

    }

    public class DateAxisFormatter implements IAxisValueFormatter {

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String xlabel = "";
            if(value == 0 || value == axis.mEntries[axis.mEntryCount-1]) {
                Date date;
                if (value == 0) {
                    date = new Date((long) (Double.parseDouble(mHistoryList.get(0)[0])));
                } else {
                    date = new Date((long) (Double.parseDouble(mHistoryList.get(mHistoryList.size() - 1)[0])));
                }
                SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yy");

                try {
                    xlabel = dateformat.format(date);
                } catch (Exception e) {
                    Timber.e(e.getMessage());
                }
            }
            return xlabel;
        }
    }
}