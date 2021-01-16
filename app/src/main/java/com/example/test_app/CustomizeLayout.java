package com.example.test_app;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class CustomizeLayout {
    private  String title;
    private Context context;
    private ConstraintLayout card;
    private String sharedPrefName;
    private View rootView;
    private MaterialCardView graphCard;
    CustomizeLayout(Context context, String title){
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        this.card = (ConstraintLayout) inflater.inflate(R.layout.customize, null, false);
        this.sharedPrefName = "cardIdMap";
        this.title = title;
        this.rootView  = inflater.inflate(R.layout.fragment_first, null, false).getRootView();
        int id = CrudOperations.read_data(title, sharedPrefName, context);
        Toast.makeText(context, String.valueOf(rootView.findViewById(id)),Toast.LENGTH_LONG ).show();
//        MaterialCardView c = lin.findViewById(id);
//        c.setBackgroundColor(Color.RED);
//        this.graphCard = c;
    }

//    private void getCard(){
//        int id = CrudOperations.read_data(title, sharedPrefName, context);
//        MaterialCardView card = rootView.findViewById(id);
//        card.setBackgroundColor(Color.RED);
//        graphCard = card;
////        return card;
//    }

    private void createMonthlyCustomization(){
        Button text = card.findViewById(R.id.textColor3);
        Button line = card.findViewById(R.id.LineColorButton);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> colorList = createColorPicker();


                graphCard.setBackgroundColor(Color.RED);
            }
        });

        line.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> colorList = createColorPicker();
            }
        });
    }


    private void createWeeklyCustomization(){
        Button text = card.findViewById(R.id.textColorButton2);
        Button pieChart = card.findViewById(R.id.PieChartColorButton);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> colorList = createColorPicker();
            }
        });

        pieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> colorList = createColorPicker();
            }
        });
    }





    public ArrayList<String> createColorPicker(){
        final ArrayList<String> colorsPicked = new ArrayList<>();
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setPositiveButton("Ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                       Toast.makeText(context, Integer.toHexString(selectedColor), Toast.LENGTH_LONG).show();
                       colorsPicked.add(Integer.toHexString(selectedColor));
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
        createMonthlyCustomization();
        builder.setView(sc);
        builder.show();
    }


}
