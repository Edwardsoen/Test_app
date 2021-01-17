package com.example.test_app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class CrudOperations {

    public static void saveIntData(String key, int value, String Shared_pref_name, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void deleteData(String Key, String Shared_pref_name, Context context){  //DELETE SPECIFIC KEY
        SharedPreferences.Editor editor = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE).edit();
        editor.remove(Key);
        editor.commit();
    }


    public static int readIntData(String key, String Shared_pref_name, Context context){
        SharedPreferences prefs = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE);
        int data = prefs.getInt(key, 0);
        return data;
    }

    public static void deleteFile(String Shared_pref_name, Context context){ //DELETE THE WHOLE FILE
        SharedPreferences.Editor editor = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE).edit();
        editor.clear().commit();
    }

    public static Map<String, ?> readAll(String Shared_pref_name, Context context){
        SharedPreferences prefs = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE);
        return prefs.getAll();
    }


    public static void SaveStringData(String key, String Value, String sharedPrefName, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE).edit();
        editor.putString(key, Value);
        editor.commit();
    }
    public static String readStringData(String key, String Shared_pref_name, Context context){
        SharedPreferences prefs = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE);
        String data = prefs.getString(key, null);
        return data;
    }

}
