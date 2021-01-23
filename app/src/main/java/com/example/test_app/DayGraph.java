package com.example.test_app;

import android.content.Context;
import android.content.DialogInterface;
import android.database.CursorIndexOutOfBoundsException;
import android.graphics.Color;
import android.os.Build;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class DayGraph<data> {
    Context context;
    String title;
    SQLFunctions SQLFunctions;
    Integer daily_target;
    String sharedPreferenceName;
    MutableLiveData<Float> data;
    Calendar c;

    DayGraph(final Context context,final String title){
        this.sharedPreferenceName = "Config";
        this.context = context;
        this.title = title;
        this.SQLFunctions = new SQLFunctions(context);
        this.daily_target = CrudOperations.readIntData(title,"Main_data", context);
        this.data = new MutableLiveData<>();
        this.c = Calendar.getInstance();
    }

    private HashMap<String, Integer> readConfig(){
        HashMap<String, Integer> data = new HashMap<>();
        String accentKey = title + "_accent";
        final String pgBarBackgroundSizeKey = title + "_pgBarBackgroundSize";
        final String pgbarSizeKey = title +"_pgbarSize";

        final String inputMaxKey = title + "_inputMax";
        final String inputStepKey = inputMaxKey + "Step";
        final String inputTypeKey = title + "_inputType";

        String accentHex = CrudOperations.readStringData(accentKey, sharedPreferenceName, context);
        if(accentHex!= null){
            data.put(accentKey ,Color.parseColor(accentHex));
        }
        String backgroundPgBarSize = CrudOperations.readStringData(pgBarBackgroundSizeKey, sharedPreferenceName, context);
        if(backgroundPgBarSize != null){
            Float f = Float.parseFloat(backgroundPgBarSize);
            data.put(pgBarBackgroundSizeKey, f.intValue());
        }
        String pgbarSize = CrudOperations.readStringData(pgbarSizeKey, sharedPreferenceName, context);
        if(pgbarSize != null){
            Float f1 = Float.parseFloat(pgbarSize);
            data.put(pgbarSizeKey, f1.intValue());
        }
        String inputType = CrudOperations.readStringData(inputTypeKey, sharedPreferenceName, context);
        if(inputType != null){
            Float f1 = Float.parseFloat(inputType);
            data.put(inputTypeKey, f1.intValue());
        }
        String inputMax = CrudOperations.readStringData(inputMaxKey, sharedPreferenceName, context);
        if(inputMax != null){
            Float f1 = Float.parseFloat(inputMax);
            data.put(inputMaxKey, f1.intValue());
        }
        return data;
    }



    private MutableLiveData<Float> getData(Integer year, Integer month, Integer date) {
       final HashMap<String, Long> current_date_range = DateClass.get_range(year, month, date, year, month, date);
        Float amount;
        try {
            ArrayList<HashMap<String, Long>> progress = SQLFunctions.readData(title, current_date_range.get("Start"), current_date_range.get("End"));
            amount = Float.valueOf(progress.get(0).get("Amount"));

        }catch (CursorIndexOutOfBoundsException e ){ //if not already in DB
            amount = 0F;
        }
        data.postValue(amount);
        return data;
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LinearLayout createDailyLayout(){

        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout card = (ConstraintLayout) inflater.inflate(R.layout.card_daily, null, false);
        LinearLayout main = card.findViewById(R.id.main);

        //PARAMS
        final TextView progressText = main.findViewById(R.id.textView3);
        final TextView dateText = main.findViewById(R.id.dateText);
        final  TextView descText = main.findViewById(R.id.textView6);
        //CIRBAR
        final CircularProgressBar progressBar = main.findViewById(R.id.circularProgressBar);
        progressBar.setProgressMax(daily_target);
        Button bot_button = main.findViewById(R.id.button2);
        Button leftArrowButton = card.findViewById(R.id.buttonn);
        final Button rightArrowButton = card.findViewById(R.id.nextButton);


        final int dateToday = c.get(Calendar.DATE);
        final int month = c.get(Calendar.MONTH);
        final int year = c.get(Calendar.YEAR);

        final HashMap<String, Integer> config = readConfig();
        String accentKey = title + "_accent";
        final String pgBarBackgroundSizeKey = title + "_pgBarBackgroundSize";
        final String pgbarSizeKey = title +"_pgbarSize";

        final String inputMaxKey = title + "_inputMax";
        final String inputTypeKey = title + "_inputType";


        if(config.containsKey(pgbarSizeKey)){
            progressBar.setProgressBarWidth(config.get(pgbarSizeKey));
        }
        if(config.containsKey(pgBarBackgroundSizeKey)){
            progressBar.setBackgroundProgressBarWidth(config.get(pgBarBackgroundSizeKey));
        }if(config.containsKey(accentKey)){
            progressBar.setBackgroundColor(config.get(accentKey)-100); //TODO:::: THISSSSSSS <<<<
            progressBar.setProgressBarColor(config.get(accentKey));
        }







        Long amount;
        final HashMap<String, Long> current_date_range = DateClass.get_range(year, month, dateToday, year, month, dateToday); //get range from dateToday x 0.0AM to 23:59PM
        try { //if no data
            ArrayList<HashMap<String, Long>> progress = SQLFunctions.readData(title, current_date_range.get("Start"), current_date_range.get("End"));
            amount = progress.get(0).get("Amount");
        }catch (CursorIndexOutOfBoundsException e ){ //if not already in DB
            SQLFunctions.insertData(c.getTime().getTime(), title, 0);
            amount = Long.valueOf(0);
        }





        HashMap<String, String> dateString = DateClass.date_to_string(year, month, dateToday);
        dateText.setText(dateString.get("Day") + " " + dateToday + " "+  dateString.get("Month"));
        progressText.setText(amount.toString());
        progressBar.setProgress(amount.intValue());
        int progressPercentage = (int) ((amount.floatValue()/daily_target.floatValue())*100);
        descText.setText(amount + " of " + daily_target + ", " + progressPercentage + "% Complete");

        if(amount.intValue() >= daily_target){
            progressBar.setProgressBarColor(Color.GREEN);
        }






        bot_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(config.containsKey(inputTypeKey)){
                    switch (config.get(inputTypeKey)){
                        case 0:
                            editTextInput();
                            break;
                        case 1:
                            sliderInput(config.get(inputMaxKey));
                            break;
                    }
                }else {
                    editTextInput();
                }
            }
        });


        data.observeForever(new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                progressBar.setProgress(0);
                int no = aFloat.intValue();
                progressBar.setProgress(no);
                int color = progressBar.getProgressBarColor();
                int progressPercentage = (int) ((aFloat/daily_target.floatValue())*100);
                HashMap<String, String> dateString = DateClass.date_to_string(c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DATE));
                dateText.setText(dateString.get("Day") + " " + c.get(Calendar.DATE) + " "+  dateString.get("Month"));
                progressText.setText(String.valueOf(no));
                descText.setText(no + " of " + daily_target + ", " + progressPercentage + "% Complete");
                if(progressPercentage >= 100){
                    progressBar.setProgressBarColor(Color.GREEN);
                }else {
                    progressBar.setProgressBarColor(color);

                }

            }
        });



        leftArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)-1); //get the day before
                getData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            }
        });

        rightArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE)+ 1); //get the day before
                if(c.get(Calendar.DATE) >= dateToday && c.get(Calendar.YEAR) >= year && c.get(Calendar.MONTH) >= month  ){
                }
                getData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            }
        });






        card.removeAllViews();
        return main;

    }




    private void sliderInput(int max){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        final Slider slider = new Slider(context);
        slider.setValue(1f);

        slider.setValueFrom(0f);
        slider.setValueTo((float) max);
        slider.setStepSize(1f);

        builder.setTitle("Enter Amount");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newData = (int) slider.getValue();
                final HashMap<String, Long> dateRange = DateClass.get_range(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE),c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

                try{
                    ArrayList<HashMap<String, Long>> progress = SQLFunctions.readData(title, dateRange.get("Start"), dateRange.get("End"));
                    Long pgData = progress.get(0).get("Amount");
                    int currentProgress = pgData.intValue() + newData;
                    SQLFunctions.updateData(title, currentProgress, dateRange.get("Start"), dateRange.get("End"));
                }
                catch (Exception e){
                    int currentProgress = newData;
                    SQLFunctions.insertData(c.getTime().getTime(),title, currentProgress );
                }
                getData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
            }
        });
        builder.setView(slider);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    private void editTextInput(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(editText);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newData = Integer.valueOf(String.valueOf(editText.getText()));
                final HashMap<String, Long> dateRange = DateClass.get_range(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE),c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

                try{
                    ArrayList<HashMap<String, Long>> progress = SQLFunctions.readData(title, dateRange.get("Start"), dateRange.get("End"));
                    Long pgData = progress.get(0).get("Amount");
                    int currentProgress = pgData.intValue() + newData;
                    SQLFunctions.updateData(title, currentProgress, dateRange.get("Start"), dateRange.get("End"));
                }
                catch (Exception e){
//                            Long pgData = Long.valueOf(0);
                    int currentProgress = newData;
                    SQLFunctions.insertData(c.getTime().getTime(),title, currentProgress );
                }
                getData(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

            }
        });
        builder.show();

    }



}
