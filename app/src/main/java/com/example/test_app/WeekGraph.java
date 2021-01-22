package com.example.test_app;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
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
import java.util.LinkedHashMap;
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
        int currentDate = 1;
        int maxDate = 7;
        ArrayList<Integer> mainData = new ArrayList<>();
        for(int i = 1; i <= noOfWeek +1 ; i++){
            if (maxDate >= max){
                maxDate = max;
            }
            ArrayList<HashMap<String, Long>> data = getWeekData(currentDate, maxDate);
            currentDate +=7;
            maxDate +=7;
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



    public LinearLayout createWeekChart(){
        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout card = (ConstraintLayout) inflater.inflate(R.layout.card_week, null, false);
        LinearLayout lin = card.findViewById(R.id.linearL);
        final RelativeLayout re = lin.findViewById(R.id.re);
        card.removeAllViews();




//        final LinearLayout lin = new LinearLayout(context);
        final ArrayList<Integer> colors = new ArrayList<>(); //TODO: //OPTIMIZE THIS
        final ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        final Button btn = lin.findViewById(R.id.btnLeft);
        final Button btn2 = lin.findViewById(R.id.btnRight);
        final TextView textView = lin.findViewById(R.id.weekTextView);
        colors.add(Color.LTGRAY);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GREEN);

        monthData.observeForever(new Observer<ArrayList<Integer>>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onChanged(ArrayList<Integer> integers) {
                String month = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
                textView.setText(month);
                ArrayList<PieEntry> entries = new ArrayList<>();
                re.removeAllViews();
                re.invalidate();

//                Button btn = new Button(context);
//                Button btn2 = new Button(context);
                final HashMap<Integer, Integer> weekXMap = new HashMap<>(); //eg {0: week1, 1:week 2}
                final PieChart pieChart = new PieChart(context);
                int total =0;
                entries.clear();
                for(int i =0; i<= integers.size()-1; i++) {
                    int week = i+1;
                    if (integers.get(i) !=0) {
                        weekXMap.put(entries.size(),week);
                        entries.add(new PieEntry(integers.get(i), "Week " + week));
                        total += integers.get(i);
                    }
                }
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH)-1, c.get(Calendar.DATE));
                        pieChart.clear();
                        getMonthData();
                    }
                });
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DATE));
                        pieChart.clear();
                        getMonthData();
                    }
                });

                pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        Toast.makeText(context, String.valueOf(h.getX()), Toast.LENGTH_LONG).show();
                        createWeekPopupChart((weekXMap.get((int)h.getX())));
                    }

                    @Override
                    public void onNothingSelected() {

                    }
                });
                PieDataSet set = new PieDataSet(entries, String.valueOf(c.get(Calendar.YEAR))); //TODO: CHANGE TO UNIT
                set.setColors(colors);
                PieData data = new PieData(set);
                data.setValueTextSize(13f);
                Description d = new Description();


                d.setText("Total: " + total);
                pieChart.setDescription(d);
                pieChart.getDescription().setTextSize(12f);
                pieChart.setData(data);
                pieChart.setHoleRadius(0f);
                int color = getColor();
                pieChart.getLegend().setTextColor(color);
                pieChart.getDescription().setTextColor(color);
                pieChart.setDrawSliceText(false);
                re.addView(pieChart, lp);
//                lin.addView(btn);
//                lin.addView(pieChart, lp);
//                lin.addView(btn2);
            }
        });
        getMonthData();
        return  lin;
    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    public  void createWeekPopupChart(int week) {
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
//        HashMap<String, Integer> mainData = new HashMap<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();
        LinkedHashMap<String, Integer> mainData = new LinkedHashMap<>();
        mainData.put("Sunday", 0 );
        mainData.put("Saturday", 0 );
        mainData.put("Friday", 0 );
        mainData.put("Thursday", 0 );
        mainData.put("Wednesday", 0 );
        mainData.put("Tuesday", 0 );
        mainData.put("Monday", 0 );

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
        for(int i =startDateOfWeek; i <= endDateOfWeek+1; i++){
            if(!allDatesWithData.contains(i)){ //Dates with missing data
                String day = cc.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
                cc.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), i);
                if (!mainData.containsKey(day)){
                mainData.put(day, 0);}

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
        int color = getColor();
        barchart.getDescription().setTextColor(color);
        barchart.getLegend().setTextColor(color);
        barData.setValueTextColor(color);
        barchart.getXAxis().setTextColor(color);
        barchart.getAxisLeft().setTextColor(color);
        barchart.getAxisRight().setTextColor(color);


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
