package com.example.test_app;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.slider.Slider;

public class CustomizeLayout {
    private  String title;
    private Context context;
    private ConstraintLayout card;
    private String sharedprefName;

    CustomizeLayout(Context context, String title){
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        this.card = (ConstraintLayout) inflater.inflate(R.layout.customize, null, false);
        this.title = title;
        this.sharedprefName = "Config";
    }



    private void createWeekCustomization(){ //TODO: OPTIMIZE THIS FUNCTION <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        final String pieColorKey1 = title + "_pieColor1";
        final String pieColorKey2 = title + "_pieColor2";
        final String pieColorKey3 = title + "_pieColor3";
        final String pieColorKey4 = title + "_pieColor4";
        final String pieColorKey5 = title + "_pieColor5";



        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflater.inflate(R.layout.piechartcolor, null, false);
        LinearLayout lin = constraintLayout.findViewById(R.id.lin3);
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);



        Button btn1 = lin.findViewById(R.id.color1);
        Button btn2 = lin.findViewById(R.id.color2);
        Button btn3 = lin.findViewById(R.id.color3);
        Button btn4 = lin.findViewById(R.id.color4);
        Button btn5 = lin.findViewById(R.id.color5);


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(pieColorKey1, sharedprefName);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(pieColorKey2, sharedprefName);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(pieColorKey3, sharedprefName);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(pieColorKey4, sharedprefName);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(pieColorKey5, sharedprefName);
            }
        });
        constraintLayout.removeAllViews();

        builder.setView(lin);

        final AlertDialog alertDialog = builder.create();


        Button pieColor = card.findViewById(R.id.PieChartColorButton);
        pieColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();

            }
        });
    }

    private void createCardCustomization(){
        final String bgKey = title + "_bg";
        final String accentKey = title + "_accent";

        Button accent = card.findViewById(R.id.cardAccentButton);
        Button bg = card.findViewById(R.id.CardBackgroundButton);
        accent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(accentKey, sharedprefName);
            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createColorPicker(bgKey, sharedprefName);
            }
        });
    }


    private void createDayCustomization() {
        final String pgBarBackgroundSizeKey = title + "_pgBarBackgroundSize";
        final String pgbarSizeKey = title + "_pgbarSize";
        final String inputMaxKey = title + "_inputMax";
        final String inputTypeKey = title + "_inputType";


        Button bgSizeButton = card.findViewById(R.id.bgProgressBarButton);
        Button pgbarSizeButton = card.findViewById(R.id.progressBarWidthButton);
        Spinner inputTypeSpinner = card.findViewById(R.id.spinner2);


        bgSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSlider(sharedprefName, pgBarBackgroundSizeKey, 25f, 50f, "Select Size");
            }
        });
        pgbarSizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSlider(sharedprefName, pgbarSizeKey, 25f, 50f, "Select Size");
            }
        });

        inputTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               switch (position){
                   case 0:
                       CrudOperations.SaveStringData(inputTypeKey, "0", sharedprefName, context);
                       break;
                   case 1:
                       createNumberEditTextDialog("Select Max", sharedprefName, inputMaxKey);
                       CrudOperations.SaveStringData(inputTypeKey, "1", sharedprefName, context);
                       break;
                   case 2:
                       createNumberEditTextDialog("Select Max", sharedprefName, inputMaxKey);
                       CrudOperations.SaveStringData(inputTypeKey, "2", sharedprefName, context);
                       break;

               }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void createSlider(final String sharedPrefName, final String key, float val, float max, String title){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        final Slider slider = new Slider(context);

        String defaultValue = CrudOperations.readStringData(key, sharedPrefName, context);
        if(defaultValue != null){
            Float f = Float.parseFloat(defaultValue);
            try{
            slider.setValue(f);
            } catch (Exception e){
                slider.setValue(f + 1f);
            }
        }else {
            slider.setValue(val);
        }

        slider.setValueFrom(1.0f);
        slider.setValueTo(max);
        slider.setStepSize(1);

        builder.setTitle(title);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CrudOperations.SaveStringData(key, String.valueOf((int) slider.getValue()), sharedPrefName, context);
            }
        });
        builder.setView(slider);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void createNumberEditTextDialog(String title, final String sharedprefName, final String key){
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(title);
        final EditText text = new EditText(context);
        text.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setPositiveButton("Confiirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editable data = text.getText();
                try {
                    int i = Integer.parseInt(data.toString());
                    if(i ==0){
                        Toast.makeText(context, "Cannot be 0 ", Toast.LENGTH_LONG).show();
                    }else {
                        float f = Float.parseFloat(String.valueOf(i));
                        CrudOperations.SaveStringData(key, data.toString(), sharedprefName, context);
                        dialog.dismiss();

                    }
                }catch (Exception e ){
                    Toast.makeText(context, "Cannot be Empty", Toast.LENGTH_LONG).show();
                }

        }
        });
        builder.setView(text);
        builder.show();


    }


    private void createColorPicker(final String key, final String sharedPref){
        ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
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

    }


    private  void createResetConfig(){ //TODO: ASUHDJASBLJKDHSKJLDHASJKLDHnKLHJ
        Button reset = card.findViewById(R.id.restoreDefaultButton);
        final String pgBarBackgroundSizeKey = title + "_pgBarBackgroundSize";
        final String pgbarSizeKey = title + "_pgbarSize";
        final String inputMaxKey = title + "_inputMax";
        final String inputTypeKey = title + "_inputType";
        final String bgKey = title + "_bg";
        final String accentKey = title + "_accent";
        final String pieColorKey1 = title + "_pieColor1";
        final String pieColorKey2 = title + "_pieColor2";
        final String pieColorKey3 = title + "_pieColor3";
        final String pieColorKey4 = title + "_pieColor4";
        final String pieColorKey5 = title + "_pieColor5";


        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrudOperations.deleteData(pgBarBackgroundSizeKey, sharedprefName, context);
                CrudOperations.deleteData(pgbarSizeKey, sharedprefName, context);
                CrudOperations.deleteData(inputMaxKey, sharedprefName, context);
                CrudOperations.deleteData(inputTypeKey, sharedprefName, context);
                CrudOperations.deleteData(bgKey, sharedprefName, context);
                CrudOperations.deleteData(accentKey, sharedprefName, context);
                CrudOperations.deleteData(pieColorKey1, sharedprefName, context);
                CrudOperations.deleteData(pieColorKey2, sharedprefName, context);
                CrudOperations.deleteData(pieColorKey3, sharedprefName, context);
                CrudOperations.deleteData(pieColorKey4, sharedprefName, context);
                CrudOperations.deleteData(pieColorKey5, sharedprefName, context);


            }
        });






    }


    public void generateLayout(){
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        ScrollView sc = new ScrollView(context);
        sc.addView(card);
        createCardCustomization();
        createWeekCustomization();
        createDayCustomization();
        createResetConfig();
        builder.setView(sc);
        builder.show();
    }


}
