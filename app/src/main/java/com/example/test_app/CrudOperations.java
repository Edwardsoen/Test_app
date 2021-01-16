package com.example.test_app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class CrudOperations {

    public static void save_data(String key, int hour, String Shared_pref_name, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE).edit();
        editor.putInt(key, hour);
        editor.commit();
    }

    public static void delete_data(String Key, String Shared_pref_name, Context context){  //DELETE SPECIFIC KEY
        SharedPreferences.Editor editor = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE).edit();
        editor.remove(Key);
        editor.commit();
    }


    public static int read_data(String key, String Shared_pref_name, Context context){
        SharedPreferences prefs = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE);
        int data = prefs.getInt(key, 0);
        return data;
    }

    public static void delete_file(String Shared_pref_name, Context context){ //DELETE THE WHOLE FILE
        SharedPreferences.Editor editor = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE).edit();
        editor.clear().commit();
    }

    public static Map<String, ?> read_all(String Shared_pref_name, Context context){
        SharedPreferences prefs = context.getSharedPreferences(Shared_pref_name, Context.MODE_PRIVATE);
        return prefs.getAll();

    }
}
