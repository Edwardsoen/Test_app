package com.example.test_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WeekGraph {
    Context context;
    String title;
    SQLFunctions SQLFunctions;
    Integer daily_target;
    String sharedPreferenceName;
    MutableLiveData<ArrayList<Integer>> monthData;
    Calendar c;


    WeekGraph(final Context context,final String title){
        this.sharedPreferenceName = "graphConfig";
        this.context = context;
        this.title = title;
        this.SQLFunctions = new SQLFunctions(context);
        this.daily_target = CrudOperations.readIntData(title,"Main_data", context);
        this.c = Calendar.getInstance();
        this.monthData = new MutableLiveData<>();
    }

    public MutableLiveData<ArrayList<Integer>> getMonthData() {
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int noOfWeek = (max/7) + 1;
        int currentDate = 0;
        ArrayList<Integer> mainData = new ArrayList<>();
        for(int i = 1; i <= noOfWeek +1 ; i++){
            ArrayList<HashMap<String, Long>> data = getWeekData(currentDate, currentDate +7);
            currentDate +=7;
            int total = 0;
            for(int ii=0; ii<= data.size() -1 ; ii++){
                total += data.get(ii).get("Amount").intValue();
            }
            mainData.add(total);
        }
        monthData.postValue(mainData);
        return monthData;
    }


    private ArrayList<HashMap<String, Long>> getWeekData(int startDate, int maxDate){
        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        HashMap<String, Long> range = DateClass.get_range(c.get(Calendar.YEAR), c.get(Calendar.MONTH), startDate ,c.get(Calendar.YEAR),c.get(Calendar.MONTH),maxDate);
        ArrayList<HashMap<String, Long>> SQLData = null;
        try {
            SQLData = SQLFunctions.readData(title, range.get("Start"), range.get("End"));
        } catch (Exception e ){
            SQLData = new ArrayList<>();
        }
        return SQLData;
    }


    private String getWeekGraphConfig(){
        String text = title + "textWeeklyGraphColor";
        String textConfig =  CrudOperations.readStringData(text, sharedPreferenceName, context);
        if (textConfig == null){
            CrudOperations.SaveStringData(text, "#000000", sharedPreferenceName, context);
            textConfig =  CrudOperations.readStringData(text, sharedPreferenceName, context);
        }
        return textConfig;
    }



    public LinearLayout createWeeklyLayout2(){

        final LinearLayout lin = new LinearLayout(context);
        final ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final ArrayList<Integer> colors = new ArrayList<>(); //TODO: //OPTIMIZE THIS


        colors.add(Color.LTGRAY);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GREEN);

        monthData.observeForever(new Observer<ArrayList<Integer>>() {
            @Override
            public void onChanged(ArrayList<Integer> integers) {
                ArrayList<PieEntry> entries = new ArrayList<>();
                lin.removeAllViews();
                lin.invalidate();
                Button btn = new Button(context);
                final PieChart pieChart = new PieChart(context);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH)-1, c.get(Calendar.DATE));
                        pieChart.clear();
                        getMonthData();
                    }
                });

                pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {

                        Toast.makeText(context, h.toString(), Toast.LENGTH_LONG).show();
                        createWeeklyLayout((int) (h.getX() + 1));
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
                entries.clear();
                Toast.makeText(context, String.valueOf(integers.size()), Toast.LENGTH_LONG).show();
                for(int i =0; i<= integers.size()-1; i++) {
                    entries.add(new PieEntry(integers.get(i), "Week " + i));
                }
                PieDataSet set = new PieDataSet(entries, String.valueOf(c.get(Calendar.YEAR))); //TODO: CHANGE TO UNIT
                set.setColors(colors);
                PieData data = new PieData(set);
                data.setValueTextSize(13f);
                Description d = new Description();
                d.setText(c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
                pieChart.setDescription(d);
                pieChart.getDescription().setTextSize(12f);
                pieChart.setData(data);

                pieChart.setDrawSliceText(false);
                lin.addView(btn);
                lin.addView(pieChart, lp);

            }
        });
        getMonthData();
        return  lin;
    }



    public  void createWeeklyLayout(int week) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout card = (ConstraintLayout) inflater.inflate(R.layout.dialoggraph2, null, false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        HorizontalBarChart barchart = card.findViewById(R.id.dg2);

        int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int startDateOfWeek = (week -1)*7;
        int endDateOfWeek = week *7;
        if(endDateOfWeek>max){
            endDateOfWeek = max;
        }


        ArrayList<HashMap<String, Long>> data =  getWeekData(startDateOfWeek, endDateOfWeek);
        HashMap<String, Integer> mainData = new HashMap<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();

        Calendar cc = Calendar.getInstance();

        ArrayList<Integer> allDatesWithData = new ArrayList<>(); //eg: dates for  week2 = 7,8,9,10,11,12,13,14.....



        for(int i =0; i<= data.size()-1; i++){
            HashMap<String, Long> map = data.get(i);
            int amount = map.get("Amount").intValue();
            Long date = map.get("Date");
            cc.setTimeInMillis(date);
            String day = cc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            mainData.put(day, amount);
            allDatesWithData.add(cc.get(Calendar.DATE));
        }
        for(int i =startDateOfWeek; i <= endDateOfWeek; i++){
            if(!allDatesWithData.contains(i)){ //Dates with missing data
                cc.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), i);
                String day = cc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                mainData.put(day, 0);

            }
        }

        int i =0;




        for(Map.Entry<String, Integer>entry: mainData.entrySet()){
                entries.add(new BarEntry(i, entry.getValue()));
                labels.add(entry.getKey());
                i+=1;
        }



        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.getAxisRight().setDrawGridLines(false);
        barchart.getXAxis().setDrawGridLines(false);
        barchart.setScaleEnabled(false);
        barchart.setTouchEnabled(false);
        barchart.setScaleXEnabled(false);
        barchart.setScaleYEnabled(false);
        barchart.setPinchZoom(false);
        Description desc = new Description();
        String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        desc.setText(monthName + " "+ startDateOfWeek + " to " + endDateOfWeek);
        barchart.setDescription(desc);
        barchart.getDescription().setTextSize(10f);


        BarDataSet barDataSet = new BarDataSet(entries, "Amount");
        BarData barData = new BarData(barDataSet);
        barchart.setData(barData);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels.get((int)value);
            }
        };
        barchart.getXAxis().setValueFormatter(formatter);
        builder.setView(card);
        builder.show();
    }



}
