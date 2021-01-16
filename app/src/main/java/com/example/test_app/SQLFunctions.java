package com.example.test_app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;


public class SQLFunctions {
    private String DATABASE_NAME = "Data";
    private String TABLE_NAME = "Main_data";
    private Context context;
    private SQLiteDatabase db;
    private String DATE_FIELD = "Date";
    private String ACTIVITY_FIELD =  "Activity";
    private String AMOUNT_FIELD = "Amount";


    SQLFunctions(Context context) {
        this.context = context;
        this.db = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null  );
    }



    public void createTable(){
        String command = "CREATE TABLE IF NOT EXISTS " +  TABLE_NAME + "(" + DATE_FIELD + " DATETIME," + ACTIVITY_FIELD + " VARCHAR," + AMOUNT_FIELD +  " INT(3));";
        db.execSQL(command);
    }

    public void insertData(Long Date, String Activity, Integer Amount){
        String Activity_data = "'" + Activity + "'";
        String insert_command = "INSERT INTO " + TABLE_NAME + "(" + DATE_FIELD + "," +  ACTIVITY_FIELD  + "," +  AMOUNT_FIELD + ")" +  "VALUES" +  "(" + Date + ", "+ Activity_data + ", " +  Amount + ");";
        db.execSQL(insert_command);
    }



    public ArrayList<HashMap<String, Long>> readData(String Activity, Long starting_period, Long end_period){
        String read_command = String.format("SELECT * FROM %s WHERE %s = '%s' AND %s > %d AND %s < %d", TABLE_NAME, ACTIVITY_FIELD, Activity, DATE_FIELD, starting_period, DATE_FIELD, end_period);
        Cursor c = db.rawQuery(read_command, null);
        int amount = c.getColumnIndex(AMOUNT_FIELD);
        int date = c.getColumnIndex(DATE_FIELD);

        c.moveToFirst();
        ArrayList<HashMap<String, Long>> data = new ArrayList<>();


        if (c!= null){
            HashMap<String, Long> entry = new HashMap();
            Long longDate = c.getLong(date);
            Integer amountInt = c.getInt(amount);
            entry.put("Date", longDate);
            entry.put("Amount", amountInt.longValue());
            data.add(entry);
            while(c.moveToNext()) {
                entry = new HashMap();
                longDate = c.getLong(date);
                amountInt = c.getInt(amount);
                entry.put("Date", longDate);
                entry.put("Amount", amountInt.longValue());
                data.add(entry);
            }
        }
        c.close();
        return data;
    }

    public void updateData(String Activity, Integer Amount, Long Starting_period, Long End_period){
        String command = String.format("UPDATE %s SET %s = %d WHERE %s > %d AND %s < %d", TABLE_NAME, AMOUNT_FIELD, Amount, DATE_FIELD, Starting_period, DATE_FIELD, End_period);
        db.execSQL(command);
    }

    public void renameSQL(String oldName, String newName) {
        String command = String.format("UPDATE %s SET %s = '%s' WHERE %s = '%s'", TABLE_NAME, ACTIVITY_FIELD, newName, ACTIVITY_FIELD, oldName);
        db.execSQL(command);

    }

    public void DeleteData(String title){
        String command = String.format("DELETE FROM %s WHERE %s = '%s'", TABLE_NAME, ACTIVITY_FIELD, title);
        db.execSQL(command);
    }





}
