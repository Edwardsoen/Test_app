package com.example.test_app;

import android.content.Context;
import android.content.res.Configuration;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GraphLayout {
    Context context;
    String title;
    SQLFunctions SQLFunctions;
    DateClass dateClass;
    Integer daily_target;
    String sharedPreferenceName;


    GraphLayout(final Context context,final String title){
        this.sharedPreferenceName = "graphConfig";
        this.context = context;
        this.title = title;
        this.SQLFunctions = new SQLFunctions(context);
        this.dateClass = new DateClass();
        this.daily_target = CrudOperations.readIntData(title,"Main_data", context);
    }

    public  void createWeeklyLayout(Integer year, Integer month, Integer startDate, Integer endDate) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout card = (ConstraintLayout) inflater.inflate(R.layout.dialoggraph2, null, false);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        LinearLayout lin = card.findViewById(R.id.lin2);
        HorizontalBarChart barchart = card.findViewById(R.id.dg2);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//        HashMap<String, String> date =  DateClass.date_to_string(new DateClass().CURRENT_YEAR, new DateClass().CURRENT_MONTH, new DateClass().CURRENT_DATE);
        setChartTextColor(barchart);
        final ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();

        HashMap<String, Long> range = DateClass.get_range(year, month, startDate , year, month, endDate);

        ArrayList<HashMap<String, Long>>  data = SQLFunctions.readData(title, range.get("Start"), range.get("End"));


        Collections.reverse(data);
        for(int i=0; i < data.size(); i++) {
            HashMap<String, Long> dataEntry = data.get(i);
            Integer amount = dataEntry.get("Amount").intValue();
            Long Date = dataEntry.get("Date");
            Calendar c = Calendar.getInstance();
            entries.add(new BarEntry(i, amount));
            c.setTimeInMillis(Date);
            String day = c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            labels.add(day);
        }


        barchart.getXAxis().setLabelCount(data.size());
        barchart.getAxisLeft().setDrawGridLines(false);
        barchart.getAxisRight().setDrawGridLines(false);
        barchart.getXAxis().setDrawGridLines(false);
        barchart.setScaleEnabled(false);
        barchart.setTouchEnabled(false);
        barchart.setScaleXEnabled(false);
        barchart.setScaleYEnabled(false);
        barchart.setPinchZoom(false);




        BarDataSet barDataSet = new BarDataSet(entries, "labels");
        BarData barData = new BarData(barDataSet);
        barchart.setData(barData);
        ValueFormatter formatter = new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return labels.get((int)value);
            }
        };
        barchart.getXAxis().setValueFormatter(formatter);
        //lin.addView(barchart, lp);
        builder.setView(card);
        builder.show();
    }


    public  LinearLayout createDailyLayout(Integer year, Integer month, Integer date){
        DateClass dC = new DateClass(); //call new Dateclass to get new date when layout is called
        if(year == null) {
            year = dC.CURRENT_YEAR;
        }
        if(month == null) {
            month = dC.CURRENT_MONTH;
        }
        if(date == null) {
            date = dC.CURRENT_DATE;
        }



       LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout card = (ConstraintLayout) inflater.inflate(R.layout.card_daily, null, false);
        LinearLayout main = card.findViewById(R.id.main);
        //PARAMS
        final TextView Main_mid = main.findViewById(R.id.textView2);
        final TextView progressText = main.findViewById(R.id.textView3);
        final TextView dateText = main.findViewById(R.id.textView);
        //CIRBAR
        final CircularProgressBar Main_cir = main.findViewById(R.id.circularProgressBar);
        Main_cir.setProgressMax(daily_target);
        //BUTTONS
        Button bot_button = main.findViewById(R.id.button2);
        //Slider
        final Slider slider = main.findViewById(R.id.slider);
        //title
        //Calendar
        Calendar c = Calendar.getInstance();


        final HashMap<String, Long> current_date_range = DateClass.get_range(year, month, date, year, month, date); //get range from date x 0.0AM to 23:59PM

        try {
            ArrayList<HashMap<String, Long>> progress = SQLFunctions.readData(title, current_date_range.get("Start"), current_date_range.get("End"));
            Long amount = progress.get(0).get("Amount");
            progressText.setText(amount.toString());
            Long dateData = progress.get(0).get("Date");
            Main_cir.setProgress(amount.intValue());
            HashMap<String, String> dateString = DateClass.date_to_string(year, month, date);
            dateText.setText(dateString.get("Day") + " " + date + " "+  dateString.get("Month"));


            Integer Progress = (amount.intValue()/daily_target) * 100;
            Main_mid.setText(Progress.toString() + "%"); //TODO: DATE
//            Main_title.setText(date.toString());//

        }catch (CursorIndexOutOfBoundsException e ){ //if not already in DB
            c.set(year, month, date);
            progressText.setText(String.valueOf(0));
            SQLFunctions.insertData(c.getTime().getTime(), title, 0);
        }


        final int finalDate = date;
        bot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<HashMap<String, Long>> progress = SQLFunctions.readData(title, current_date_range.get("Start"), current_date_range.get("End"));
                float current = progress.get(0).get("Amount").floatValue();
                float slider_prog = slider.getValue();
                float total = current + slider_prog;
                Main_cir.setProgress(total);
                Calendar c = Calendar.getInstance();
                if(finalDate == dateClass.CURRENT_DATE) {
                    SQLFunctions.updateData(title, (int) total, current_date_range.get("Start"), current_date_range.get("End"));
                }

                Integer progress_percentage = (int) (total/daily_target) *100;
                Main_mid.setText(progress_percentage.toString() + "%");
            }
        });
        card.removeAllViews();
        return main;

    }


    public  LinearLayout createMonthlyLayout(){
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

        String lineColor = getMonthlyGraphgCongfig();

        lineChart.getAxisLeft().setTextColor(Color.parseColor(lineColor)); // left y-axis
        lineChart.getXAxis().setTextColor(Color.parseColor(lineColor));
        lineChart.getLegend().setTextColor(Color.parseColor(lineColor));
        lineChart.getAxisRight().setTextColor(Color.parseColor(lineColor));
        lineChart.getDescription().setTextColor(Color.parseColor(lineColor));
        lineChart.getLegend().setTextColor(Color.parseColor(lineColor));



        LineDataSet lineDataSet = new LineDataSet(entries, "labels");
        final LineData lineData = new LineData(lineDataSet);

        lineData.setValueTextColor(Color.parseColor(lineColor));
        lineData.setValueTextSize(12f);
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

    private String getMonthlyGraphgCongfig(){
        String text = title + "textMonthlyGraphColor";
        String textConfig =  CrudOperations.readStringData(text, sharedPreferenceName, context);
        if (textConfig == null){
            CrudOperations.SaveStringData(text, "#000000", sharedPreferenceName, context);
            textConfig =  CrudOperations.readStringData(text, sharedPreferenceName, context);
        }
        return textConfig;
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




            String lineColor = getMonthlyGraphgCongfig();

            lineChart.getAxisLeft().setTextColor(Color.parseColor(lineColor)); // left y-axis
            lineChart.getXAxis().setTextColor(Color.parseColor(lineColor));
            lineChart.getLegend().setTextColor(Color.parseColor(lineColor));
            lineChart.getAxisRight().setTextColor(Color.parseColor(lineColor));
            lineChart.getDescription().setTextColor(Color.parseColor(lineColor));
            lineChart.getLegend().setTextColor(Color.parseColor(lineColor));

            LineDataSet lineDataSet = new LineDataSet(entries, "labels");
            final LineData lineData = new LineData(lineDataSet);

            lineData.setValueTextSize(12f);
            lineData.setValueTextColor(Color.parseColor(lineColor));

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

    public LinearLayout createWeeklyLayout2( Integer year, Integer month){
        DateClass dC = new DateClass();
        if(month == null){
            month = dC.CURRENT_MONTH;
        }
        if(year == null){
            year = dC.CURRENT_YEAR;
        }
        Calendar c = Calendar.getInstance();
        c.set(year, month, 1);
        String monthName = c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        final int dayInAMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        c.setMinimalDaysInFirstWeek(7);
        LinearLayout lin = new LinearLayout(context);
        ViewGroup.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        PieChart pieChart = new PieChart(context);
        pieChart.setHoleRadius(0);

        pieChart.setDrawSliceText(false);
        ArrayList<PieEntry> entries = new ArrayList<>();

        HashMap<String, Long> range = DateClass.get_range(year, month, 1, year, month, dayInAMonth);


        ArrayList<HashMap<String, Long>> SQLData = null;
        try {

            SQLData = SQLFunctions.readData(title, range.get("Start"), range.get("End"));
        } catch (Exception e ){
            SQLData = new ArrayList<>();
        }




        ArrayList<Integer> colors = new ArrayList<>(); //TODO: //OPTIMIZE THIS
        colors.add(Color.LTGRAY);
        colors.add(Color.CYAN);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.GREEN);


        final ArrayList<HashMap<String, Integer>> dateMap = new ArrayList<>();
        HashMap<Integer, Integer> mainData = new HashMap<>();
        HashMap<Integer, Integer> weekOfMonthHash = DateClass.getWeekOfMonth(dayInAMonth); //week of month hash  //{date: week1...... }

        for(int i =0; i < SQLData.size(); i++ ){
            HashMap<String, Long> entryMap = SQLData.get(i);
            int amount = entryMap.get("Amount").intValue();
            Long date = entryMap.get("Date");
            c.setTimeInMillis(date);
            int weekOfMonth = weekOfMonthHash.get(c.get(Calendar.DATE));
            if (mainData.containsKey(weekOfMonth)){
                mainData.put(weekOfMonth, amount + mainData.get(weekOfMonth) ); //if exist add current amount
            }else{
                mainData.put(weekOfMonth,amount);
            }
        }


        String textColor = getWeekGraphConfig();
        pieChart.getDescription().setTextColor(Color.parseColor(textColor));
        pieChart.getLegend().setTextColor(Color.parseColor(textColor));



        int ii=0;
        for(Map.Entry<Integer, Integer> entry: mainData.entrySet()){
            entries.add(new PieEntry(entry.getValue(), "Week " + entry.getKey()));

            Integer end = entry.getKey()*7;
            HashMap<String, Integer> entryMap = new HashMap<>();
            Integer start;
            if(end >= dayInAMonth){
                start = (entry.getKey()-1)*7;
                end = dayInAMonth;

            }else {
                start = end -7;
            }
            entryMap.put("Start", start);
            entryMap.put("End", end);
            dateMap.add(ii, entryMap);
            ii+=1;
        }

        final Integer finalMonth = month;
        final Integer finalYear = year;
        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
//                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                HashMap<String, Integer> t = dateMap.get((int)h.getX());
//                Toast.makeText(context, String.valueOf(h.getX()), Toast.LENGTH_LONG).show();
                createWeeklyLayout(finalYear, finalMonth, t.get("Start"), t.get("End"));
            }

            @Override
            public void onNothingSelected() {

            }
        });





        PieDataSet set = new PieDataSet(entries, monthName); //TODO: CHANGE TO UNIT
        set.setColors(colors);
        PieData data = new PieData(set);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.parseColor(textColor));
        pieChart.setData(data);


        lin.addView(pieChart, lp);
        return  lin;
    }








    private void setChartTextColor(BarChart barChart){
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                barChart.getAxisLeft().setTextColor(Color.WHITE);
                barChart.getXAxis().setTextColor(Color.WHITE);
                barChart.getLegend().setTextColor(Color.WHITE);
                break;

            case Configuration.UI_MODE_NIGHT_NO:

                break;

        }


    }

}
