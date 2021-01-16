package com.example.test_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_manager {
    public static MaterialAlertDialogBuilder main_layout(final Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        ConstraintLayout main = (ConstraintLayout) inflater.inflate(R.layout.activity_manager, null, false);
        HashMap<String, ArrayList> data = get_activity_list(context);
        final MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);


        final HashMap<Integer,String> radio_name_id_map = new HashMap<>();

        final RadioGroup radiogroup = main.findViewById(R.id.radiogroup);
        ArrayList<String> keys = data.get("Keys");
        for(int i =0; i < keys.size(); i++ ){
            RadioButton button = new RadioButton(context);
            button.setText(keys.get(i));
            radiogroup.addView(button);
            radio_name_id_map.put( button.getId(), String.valueOf(button.getText()));
        }

        Button delete = main.findViewById(R.id.button4);
        Button add = main.findViewById(R.id.button9);
        Button edit = main.findViewById(R.id.button6);
        Button customize = main.findViewById(R.id.button5);

        dialog.setView(main);
        final AlertDialog dialogg = dialog.create();


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radiogroup.getCheckedRadioButtonId() == -1){
                    Toast.makeText(context, "SELECT ITEM", Toast.LENGTH_LONG ).show();
                }
                else {
                    final String title = radio_name_id_map.get(radiogroup.getCheckedRadioButtonId());
                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
                    final RadioGroup rg = new RadioGroup(context);
                    final RadioButton rb1 = new RadioButton(context);
                    rb1.setText("Edit Object Name");
                    final RadioButton rb2 = new RadioButton(context);
                    rb2.setText("Edit Amount");
                    rg.addView(rb1);
                    rg.addView(rb2);
                    builder.setView(rg);
                    builder.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(rb1.isChecked()){
                                createEditAlertDialog(context, false, title);
                            }
                            else if (rb2.isChecked()){
                                createEditAlertDialog(context, true, title);
                            }else {
                                Toast.makeText(context, "Select object", Toast.LENGTH_LONG).show();
                            }

                        }
                    });

//                    CrudOperations.save_data(title);
                    final AlertDialog alert = builder.create();
                    alert.setView(rg);
                    alert.show();
                    dialogg.dismiss();
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (radiogroup.getCheckedRadioButtonId() == -1){
                        Toast.makeText(context, "SELECT ITEM", Toast.LENGTH_LONG ).show();
                    }
                    else {
                        String title = radio_name_id_map.get(radiogroup.getCheckedRadioButtonId());
                        createDeleteDataAlertDialog(context, title);
                        CrudOperations.delete_data(title, "Main_data", context);
                        dialogg.dismiss();
                    }
            }
        });
        dialogg.show();


        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
                final EditText text = new EditText(context);
                dialog.setTitle("Add Activity");

                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        switch(keyCode){
                            case KeyEvent.KEYCODE_ENTER:
                                if(text.getText().toString().isEmpty() || (text.getText().toString().trim().length() == 0 )){
                                    Toast.makeText(context, "CANNOT BE EMPTY", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    dialog.dismiss();
                                    add_confirm(text, context, "Main_data");
                                    dialogg.dismiss();
                                }
                        }
                        return true;
                    }
                });
                dialog.setView(text);
                dialog.setPositiveButton("Next", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(text.getText().toString().isEmpty() || (text.getText().toString().trim().length() == 0 )){
                            Toast.makeText(context, "CANNOT BE EMPTY", Toast.LENGTH_LONG).show();
                        }
                        else {
                            dialog.dismiss();
                            add_confirm(text, context, "Main_data");
                            dialogg.dismiss();
                        }

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();
            }
        });


        return dialog;
    }


    public static String add_confirm(EditText text, final Context context, final String pref_name){
        final String activity = text.getText().toString().trim();
        final MaterialAlertDialogBuilder Second_dialog = new MaterialAlertDialogBuilder(context);
        Second_dialog.setTitle("Add Limit/Target");
        final EditText numberPicker = new EditText(context);
        numberPicker.setInputType(InputType.TYPE_CLASS_NUMBER);
        Second_dialog.setView(numberPicker);
        Second_dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int hours =Integer.parseInt(numberPicker.getText().toString());
                CrudOperations.save_data(activity, hours, pref_name, context);

            }
        });
        Second_dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Second_dialog.show();
        return activity;


    }



    private static HashMap<String, ArrayList> get_activity_list(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("Main_data", Context.MODE_PRIVATE);
        Map<String, ?> entries = prefs.getAll();
        ArrayList keys = new ArrayList();
        ArrayList values = new ArrayList();
        for(Map.Entry<String, ?> entry : entries.entrySet()) {
            keys.add(entry.getKey());
            values.add(entry.getValue());
        }

        HashMap<String, ArrayList> data = new HashMap<>();
        data.put("Keys", keys);
        data.put("Values", values);
        return data;
    }


    private static void createEditAlertDialog(final Context context, final Boolean isNumber, final String title){
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        final EditText text = new EditText(context);
        if(isNumber){
            text.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        builder.setView(text);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Editable t = text.getText();
                if(isNumber){ //if edit amount
                    try{
                    CrudOperations.save_data(title, Integer.valueOf(t.toString()),  "Main_data", context);
                    Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                    }catch (Exception e){
                        Toast.makeText(context, "Cannot be empty", Toast.LENGTH_LONG).show();
                    }
                }else { //if edit name
                    if(text.getText().toString().isEmpty() || (text.getText().toString().trim().length() == 0 )){
                        Toast.makeText(context, "CANNOT BE EMPTY", Toast.LENGTH_LONG).show();
                    } else{
                    SQLFunctions sqlFunctions = new SQLFunctions(context);
                    sqlFunctions.renameSQL(t.toString(), title);
                    Integer i = CrudOperations.read_data(title, "Main_data", context);
                    CrudOperations.delete_data(title, "Main_data", context);
                    CrudOperations.save_data(t.toString(), i, "Main_data", context);
                    Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                }

                }

            }
        });

        builder.show();
    }


    private static void createDeleteDataAlertDialog(final Context context, final String title){
        final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle("Delete Data?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SQLFunctions sqlFunctions = new SQLFunctions(context);
                sqlFunctions.DeleteData(title);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }


}
