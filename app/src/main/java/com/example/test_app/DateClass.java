package com.example.test_app;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DateClass extends FirstFragment {
    Calendar c;
    int CURRENT_DATE;
    int CURRENT_MONTH;
    int CURRENT_YEAR;



    DateClass(){
        //DATE FORMAT                               !!!!!!!!!!!!!!!!YEAR/MONTH/DAY!!!!!!!!!!!!
        c = Calendar.getInstance();
        CURRENT_DATE = c.get(Calendar.DATE);
        CURRENT_MONTH = c.get(Calendar.MONTH);
        CURRENT_YEAR = c.get(Calendar.YEAR);

    }

    public static HashMap<String, Long> get_range(int start_year, int  start_month, int  start_date, int end_year, int end_month, int end_date) {
        HashMap<String, Long> data = new HashMap<>();
        Calendar c = Calendar.getInstance();
        c.set(start_year, start_month, start_date, 0,0,0);
        Long start_date_long = c.getTime().getTime();
        c.set(end_year, end_month, end_date,23,59,59);
        Long end_date_long = c.getTime().getTime();
        data.put("Start", start_date_long);
        data.put("End", end_date_long);
        return  data;
    }




    public static HashMap<String, String> date_to_string(int year, int month, int date){ //RETURN NUMBER-FORMMATED DATE TO STRING eg, SUNDAY, MARH, 2020
        HashMap<String, String> map = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date);
        String Year = calendar.getDisplayName(Calendar.YEAR, Calendar.LONG, Locale.getDefault());
        String Month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        String Day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());

        map.put("Year", Year);
        map.put("Month", Month);
        map.put("Day", Day);
       return map;
    }

    public static HashMap<Integer, Integer> getWeekOfMonth(int range){
        HashMap<Integer, Integer> data = new HashMap<>();
        int week =1;
        for(int i =1; i < range + 1; i ++){
            if(i%7 == 0 ){ //if last day of the week
                data.put(i, week);
                week +=1;
            }
            else{
                data.put(i, week);
            }
        }
        return  data;
    }

    public static Integer parseMonth(String monthName){
        HashMap<String, Integer> data = new HashMap();
        data.put("January", 0 );
        data.put("February", 1 );
        data.put("March", 2 );
        data.put("April", 3 );
        data.put("May", 4 );
        data.put("June", 5 );
        data.put("July", 6 );
        data.put("August", 7 );
        data.put("September", 8 );
        data.put("October", 9 );
        data.put("November", 10 );
        data.put("December", 11 );
        try {
            return data.get(monthName);
        }catch (Exception e){
            return  null;
        }

    }




}
