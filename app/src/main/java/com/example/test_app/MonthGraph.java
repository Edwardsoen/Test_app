package com.example.test_app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MonthGraph {
    Context context;
    String title;
    SQLFunctions SQLFunctions;
    DateClass dateClass;
    Integer daily_target;
    String sharedPreferenceName;


    MonthGraph(final Context context,final String title){
        this.sharedPreferenceName = "graphConfig";
        this.context = context;
        this.title = title;
        this.SQLFunctions = new SQLFunctions(context);
        this.dateClass = new DateClass();
        this.daily_target = CrudOperations.readIntData(title,"Main_data", context);
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private int getColor(){
        int blackColor = context.getColor(R.color.textBlack);
        int whiteColor = context.getColor(R.color.textWhite);

        int currentNightMode = context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                return blackColor;
            case Configuration.UI_MODE_NIGHT_YES:
                return whiteColor;
        }
        return blackColor;
    }






    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createPopupGraph(ArrayList<String> monthLabel, Integer xPressed){
        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout card = (ConstraintLayout) inflater.inflate(R.layout.dialoggraph, null, false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LineChart lineChart = card.findViewById(R.id.chart);



        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisRight().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.setScaleEnabled(false);
        lineChart.setTouchEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setPinchZoom(false);
        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList();
        String month = monthLabel.get(xPressed);
        Integer monthInt = DateClass.parseMonth(month);
        if(monthInt != null) {
            Calendar c = Calendar.getInstance();
            c.set(dateClass.CURRENT_YEAR, monthInt, 1);
            int dayInAMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            HashMap<String, Long> range = DateClass.get_range(dateClass.CURRENT_YEAR, monthInt, 1, dateClass.CURRENT_YEAR, monthInt, dayInAMonth);
            Toast.makeText(context, String.valueOf(dayInAMonth), Toast.LENGTH_LONG).show();
            ArrayList<HashMap<String, Long>> data = null;
            try {
                data = SQLFunctions.readData(title, range.get("Start"), range.get("End"));
            } catch (Exception e){
                data = new ArrayList<>();
                Toast.makeText(context, "Popupgraph error SQL", Toast.LENGTH_LONG).show();
            }


            lineChart.getXAxis().setLabelCount(data.size(), true);
            for(int i =0; i < data.size(); i++ ){
                HashMap<String, Long> hashEntry = data.get(i);
                Long amount = hashEntry.get("Amount");
                Toast.makeText(context, String.valueOf(amount.intValue()), Toast.LENGTH_LONG).show();
                entries.add(new Entry(i,amount.intValue()));

                c.setTimeInMillis(hashEntry.get("Date"));
                int intDate = c.get(Calendar.DATE);
                labels.add(String.valueOf(intDate));
            }



            LineDataSet lineDataSet = new LineDataSet(entries, "Amount x Date");
            final LineData lineData = new LineData(lineDataSet);

            lineData.setValueTextSize(12f);
            int color = getColor();
            lineData.setValueTextColor(color);
            Description desc = new Description();
            desc.setText(month);
            lineChart.setDescription(desc);
            lineChart.getDescription().setTextColor(color);
            lineChart.getLegend().setTextColor(color);
            lineChart.getXAxis().setTextColor(color);

            lineChart.getAxisRight().setEnabled(false);
            lineChart.getAxisLeft().setTextColor(color);


            lineChart.setData(lineData);
            ValueFormatter formatter = new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    try {
                        return labels.get((int) value);
                    } catch (Exception e) {
                        return "";

                    }
                }
            };
            lineChart.getXAxis().setValueFormatter(formatter);
            builder.setView(card);
            builder.show();

        } else

        {
            Toast.makeText(context, "No Data", Toast.LENGTH_LONG).show();
        }
    }


    private HashMap<String, Integer> readMonthlyData(){
        HashMap<String, Long> dateRange = DateClass.get_range(dateClass.CURRENT_YEAR - 1, dateClass.CURRENT_MONTH, dateClass.CURRENT_DATE,dateClass.CURRENT_YEAR, dateClass.CURRENT_MONTH, dateClass.CURRENT_DATE); //Get 1 year time range
        ArrayList<HashMap<String, Long>> SQLdata =null;
        try {
            SQLdata = SQLFunctions.readData(title, dateRange.get("Start"), dateRange.get("End"));
        } catch (Exception e ){
            SQLdata = new ArrayList<>();
        }


        HashMap<String, Integer> data = new HashMap<>();
        for(int i =0; i < SQLdata.size(); i++) {
            HashMap<String, Long> entries = SQLdata.get(i);
            Integer amount = entries.get("Amount").intValue();
            Long date = entries.get("Date");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(date);
            String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            if(data.containsKey(monthName)){
                Integer ii  = data.get(monthName);
                Integer total = ii + amount;
                data.put(monthName, total);
            }
            else {
                data.put(monthName, amount);
            }
        }
        if(data.size() >= 1){
            data.put("No data", 0);
        }
        return data;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public LinearLayout createMonthlyLayout(){
        final LinearLayout lin = new LinearLayout(context);
        final ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final LineChart lineChart = new LineChart(context);
        final TextView tv = new TextView(context);
        final ArrayList<Entry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList();
        HashMap<String, Integer> data = readMonthlyData();
        lineChart.getXAxis().setLabelCount(data.size(), true);


        int i = 0;
        for(Map.Entry<String, Integer> entry: data.entrySet() ) {
            String monthName = entry.getKey();
            Integer amount = entry.getValue();
            labels.add(monthName);
            entries.add(new Entry(i, amount));
            i +=1;
        }

        lineChart.setScaleEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setPinchZoom(false);


        LineDataSet lineDataSet = new LineDataSet(entries, "Amount");
        final LineData lineData = new LineData(lineDataSet);

        lineData.setValueTextSize(12f);
        int color = getColor();
        lineData.setValueTextColor(color);
        lineChart.getLegend().setTextColor(color);
        lineChart.getXAxis().setTextColor(color);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setTextColor(color);
        Description description = new Description();
        description.setText("Monthly Data");
        lineChart.setDescription(description);
        lineChart.getDescription().setTextColor(color);


        lineChart.setData(lineData);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels.get((int) value);
            }
        };
        lineChart.getXAxis().setValueFormatter(formatter);




        lin.addView(tv, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        lin.addView(lineChart, lp);
        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Toast.makeText(context, String.valueOf((int)e.getX()), Toast.LENGTH_LONG).show();
                createPopupGraph(labels, (int)e.getX());
//
            }

            @Override
            public void onNothingSelected() {
            }

        });
        return lin;
    }


}
