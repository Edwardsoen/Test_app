package com.example.test_app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

import static android.widget.LinearLayout.VERTICAL;

public class FirstFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{


    private SharedPreferences prefs;
    private ViewGroup.LayoutParams lp;
    private LinearLayout lin;
    private ScrollView scrollView;
    private  SharedPreferences configPrefs;


    public FirstFragment(){
    }




    @Override
    public View onCreateView(

            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        prefs = getContext().getSharedPreferences("Main_data", Context.MODE_PRIVATE);
        prefs.registerOnSharedPreferenceChangeListener(this);
        configPrefs = getContext().getSharedPreferences("cardConfig", Context.MODE_PRIVATE);
        configPrefs.registerOnSharedPreferenceChangeListener(this);
        SharedPreferences graphConfigPrefs = getContext().getSharedPreferences("graphConfig", Context.MODE_PRIVATE);
        graphConfigPrefs.registerOnSharedPreferenceChangeListener(this);
//        configPrefs.registerOnSharedPreferenceChangeListener( );

        return inflater.inflate(R.layout.fragment_first, container, false);

    }






    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MaterialCardView create_card(final Context context, final String title){
        final GraphLayout GraphLayout = new GraphLayout(context, title);
        LayoutInflater inflater = getLayoutInflater();
        ConstraintLayout card_layout = (ConstraintLayout) inflater.inflate(R.layout.card, null, false);
        final LinearLayout data_layout = card_layout.findViewById(R.id.data_layout);
        data_layout.addView(GraphLayout.createMonthlyLayout(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        TabLayout tablayout = card_layout.findViewById(R.id.tabLayout);

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){ //TODO: CHANGE LAYOUT TO NON-STATIC
                    case 0:
                        data_layout.addView(GraphLayout.createMonthlyLayout(), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    case 1:
                        data_layout.addView(GraphLayout.createDailyLayout(null, null, null ), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    case 2:
                        data_layout.addView(GraphLayout.createWeeklyLayout2(null,  null), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        //data_layout.addView(GraphLayout.createWeeklyLayout(new HashMap<String, Integer>()), new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                data_layout.removeAllViews();

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        TextView title_tv = card_layout.findViewById(R.id.textView17);
        title_tv.setText(title);
        final MaterialCardView card = new MaterialCardView(context);
        card.addView(card_layout);
        card.setClickable(true);
        card.setCheckable(true);
        card.setFocusable(true);
        HashMap<String, Integer> colorMap = readCardConfig(context, title);
        tablayout.setBackgroundColor(colorMap.get("bg"));
        card.setBackgroundColor(colorMap.get("bg"));
        tablayout.setSelectedTabIndicatorColor(colorMap.get("accent"));
        tablayout.setTabTextColors(colorMap.get("text"), colorMap.get("accent"));
        title_tv.setTextColor(colorMap.get("text"));


        return card;
    }

    public LinearLayout create_linear_layout(){
        LinearLayout lin = new LinearLayout(getContext());
        lin.setOrientation(VERTICAL);
        return lin;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public int add_card(String title, LinearLayout lin){
        MaterialCardView card = create_card(getContext(), title);
//        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CardView.LayoutParams lp = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT, CardView.LayoutParams.WRAP_CONTENT);
        lp.setMargins(50, 0, 50, 0);
        LinearLayout b = new LinearLayout(getContext());
        ViewGroup.LayoutParams lp2 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100); //100pixel gap
        card.setElevation(8f);


        lin.addView(card, lp);
        lin.addView(b, lp2);
        int ids = card.getId();
        return ids;
    }


    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Context context = getContext();
        final String Shared_pref_name = "Main_data";
        lin = create_linear_layout();
        lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        scrollView = view.findViewById(R.id.ScrollView);
        final ConstraintLayout mainlayout = view.findViewById(R.id.main_cons);
        final HashMap<String, Integer> map = new HashMap<>();
        Map<String, Integer> data = (Map<String, Integer>) prefs.getAll();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            int id = add_card(entry.getKey(), lin);
            map.put(entry.getKey(), id);
        }
        scrollView.addView(lin, lp);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        sharedPreferences = getContext().getSharedPreferences("Main_data", Context.MODE_PRIVATE);
        scrollView.removeAllViews();
        lin.removeAllViews();
        Map<String, Integer> data = (Map<String, Integer>) sharedPreferences.getAll();
        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            add_card(entry.getKey(), lin);
        }
//        Toast.makeText(getContext(), , Toast.LENGTH_LONG).show();
        scrollView.addView(lin, lp);
    }




    private static void generateDefaultCardConfig(Context context, String title){
        String sharedPrefName = "cardConfig";
        String bgKey = title + "_bg";
        String textKey = title + "_text";
        String accentKey = title + "_accent";
        CrudOperations.SaveStringData(bgKey,"#ffffff",sharedPrefName,context);
        CrudOperations.SaveStringData(textKey,"#000000",sharedPrefName,context);
        CrudOperations.SaveStringData(accentKey,"#000099",sharedPrefName,context);
    }


    private static HashMap<String, Integer> readCardConfig(Context context, String title){
        HashMap<String, Integer> data = new HashMap<>();
        String sharedPrefName = "cardConfig";
        String bgKey = title + "_bg";
        String textKey = title + "_text";
        String accentKey = title + "_accent";
        String bgHex = CrudOperations.readStringData(bgKey, sharedPrefName, context);
        if(bgHex == null){
            generateDefaultCardConfig(context, title);
            bgHex = CrudOperations.readStringData(bgKey, sharedPrefName, context);
        }
        String accentHex = CrudOperations.readStringData(accentKey, sharedPrefName, context);
        String textHex = CrudOperations.readStringData(textKey, sharedPrefName, context);
        data.put("bg", Color.parseColor(bgHex));
        data.put("text", Color.parseColor(textHex));
        data.put("accent",Color.parseColor( accentHex));
        return data;

    }
}