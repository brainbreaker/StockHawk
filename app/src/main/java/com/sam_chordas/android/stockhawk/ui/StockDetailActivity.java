package com.sam_chordas.android.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.view.AxisController;
import com.db.chart.view.ChartView;
import com.db.chart.view.LineChartView;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockIntentService;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by brainbreaker on 20/9/16.
 */

public class StockDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        LineChartView lineChartView = (LineChartView) findViewById(R.id.linechart);
        String stockSymbol = getIntent().getStringExtra("name");
        Intent stockIntent = new Intent(this,StockIntentService.class);
        stockIntent.putExtra("tag","historicalData");
        stockIntent.putExtra("name",getIntent().getStringExtra("name"));
        stockIntent.putExtra("currentDate",getIntent().getStringExtra("currentDate"));
        stockIntent.putExtra("weekBefore",getIntent().getStringExtra("weekBefore"));
        startService(stockIntent);
        Cursor c = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,
                null,
                QuoteColumns.SYMBOL + " = ? ",
                new String[]{stockSymbol},
                null);
        if (c != null) {
            c.moveToFirst();
        }
        ArrayList<Float> values = new ArrayList<>();
        if (c != null) {
            values.add(c.getFloat(c.getColumnIndex(QuoteColumns.BIDPRICE)));
            while (c.moveToNext()) {
                values.add(c.getFloat(c.getColumnIndex(QuoteColumns.BIDPRICE)));
            }
        }
        String[] label = new String[values.size()];
        float[] stockArr = new float[values.size()];
        Log.e("size", String.valueOf(values.size()));
        for (int i = 0; i < values.size(); i++) {
            stockArr[i] = values.get(i);
            label[i] = "";
        }
        /** In case the number of entries stored in database is greater than 15,
         * trim it to use the last 15 entries
         **/
        float[] trimmedStock = new float[15];
        String[] trimmedlabel = {"", "", "", "", "", "", "", "", "", "", "", "", "", "", "",};
        int j = values.size() - 15;
        if (values.size() > 15) {
            int i = 0;
            while (i < 15) {
                trimmedStock[i] = stockArr[j];
                i++;
                j++;
            }

        }
        LineSet dataset;
        if (values.size() > 15) {
            dataset = new LineSet(trimmedlabel, trimmedStock);
            dataset.setColor(getResources().getColor(R.color.colorPrimary))
                    .setDotsColor(getResources().getColor(R.color.colorPrimary))
                    .setThickness(4);
        } else {
            dataset = new LineSet(label, stockArr);
            dataset.setColor(getResources().getColor(R.color.colorPrimary))
                    .setDotsColor(getResources().getColor(R.color.colorPrimary))
                    .setThickness(4);
        }
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.parseColor("#ffffff"));
        gridPaint.setStyle(Paint.Style.FILL);
        gridPaint.setAntiAlias(true);
        gridPaint.setStrokeWidth(Tools.fromDpToPx(.1f));
        lineChartView
                .setGrid(ChartView.GridType.FULL, gridPaint)
                .setAxisBorderValues(Collections.min(values).intValue(), Collections.max(values).intValue() + 1)
                .setLabelsColor(getResources().getColor(R.color.accent))
                .setXAxis(false)
                .setYLabels(AxisController.LabelPosition.INSIDE)
                .setYAxis(false);
        lineChartView.addData(dataset);
        lineChartView.show();
        TextView stockId = (TextView) findViewById(R.id.stockName);
        c.moveToLast();
        stockId.append(c.getString(c.getColumnIndex(QuoteColumns.NAME)));
        TextView lastTrade = (TextView) findViewById(R.id.lastTrade);
        lastTrade.append(c.getString(c.getColumnIndex(QuoteColumns.LASTTRADE)));
        TextView todayMax = (TextView) findViewById(R.id.maxPrice);
        todayMax.append(c.getString(c.getColumnIndex(QuoteColumns.DAYSHIGH)));
        TextView todayMin = (TextView) findViewById(R.id.minPrice);
        todayMin.append(c.getString(c.getColumnIndex(QuoteColumns.DAYSLOW)));
        TextView annualMin = (TextView) findViewById(R.id.annual_min);
        annualMin.append(c.getString(c.getColumnIndex(QuoteColumns.YEARLOW)));
        TextView annualMax = (TextView) findViewById(R.id.annual_max);
        annualMax.append(c.getString(c.getColumnIndex(QuoteColumns.DAYSHIGH)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(c.getString(c.getColumnIndex(QuoteColumns.NAME)).toUpperCase());
        c.close();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_github) {
            Uri uriUrl = Uri.parse("https://github.com/brainbreaker/StockHawk");
            Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

}
