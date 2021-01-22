package com.example.test_app;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class CustomizeLayout {
    private  String title;
    private Context context;
    private ConstraintLayout card;

    CustomizeLayout(Context context, String title){
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        this.card = (ConstraintLayout) inflater.inflate(R.layout.customize, null, false);
        this.title = title;
    }




    private void createWeekCustomization(){
        final String sharedPreferenceName = "graphConfig";
        final String textKey = title + "textWeeklyGraphColor";
        Button pieColor = card.findViewById(R.id.PieChartColorButton);

        pieColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    private void createCardCustomization(){
        final String bgKey = title + "_bg";
        final String textKey = title + "_text";
        final String accentKey = title + "_accent";
        final String sharedPref = "cardConfig";

        Button accent = card.findViewById(R.id.cardAccentButton);
        Button bg = card.findViewById(R.id.CardBackgroundButton);
        accent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(accentKey, sharedPref);
            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(bgKey, sharedPref);
            }
        });
    }

    private void crateDayCustomization(){

    }





    public ArrayList<String> createColorPicker(final String key, final String sharedPref){
        final ArrayList<String> colorsPicked = new ArrayList<>();
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("Ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                      String colorHex = "#"+Integer.toHexString(selectedColor);
                      CrudOperations.SaveStringData(key, colorHex, sharedPref, context);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();

        return colorsPicked;
    }



    public void generateLayout(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        ScrollView sc = new ScrollView(context);
        sc.addView(card);
        createCardCustomization();
        createWeekCustomization();
        builder.setView(sc);
        builder.show();
    }


}
