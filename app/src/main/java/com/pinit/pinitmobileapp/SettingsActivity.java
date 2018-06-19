package com.pinit.pinitmobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;

import cn.qqtheme.framework.picker.ColorPicker;


public class SettingsActivity extends Activity {


    public static final int SWITCH_MODE_COLOUR = 0;
    public static final int SWITCH_MODE_ICON = 1;

    private ArrayList<SettingsItem> settingsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsList.add(new SettingsItem("Pick Pocket", R.drawable.pick_pocket));
        settingsList.add(new SettingsItem("Drunk", R.drawable.drunk));
        settingsList.add(new SettingsItem("Robbery", R.drawable.robbery));
        settingsList.add(new SettingsItem("Scam", R.drawable.scam));
        settingsList.add(new SettingsItem("Harrassment", R.drawable.harrassment));
        settingsList.add(new SettingsItem("Others", R.drawable.others));

        final ArrayAdapter<SettingsItem> adapter = new settingsAdapter(this, 0, settingsList);

        final ListView listView = (ListView) findViewById(R.id.settings_pin_list);
        listView.setAdapter(adapter);

        AdapterView.OnItemClickListener adapterViewListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                final SettingsItem item = settingsList.get(position);

                AppCompatButton customizeBttn = view.findViewById(R.id.customizeBttn);
                final ImageView img = view.findViewById(R.id.settings_pin_image);
                customizeBttn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showColourPickerDialogue(MapsActivity.currentMode, getDrawable(item.getPinImgId()), SettingsActivity.this, position, img);
                    }
                });
            }
        };
        listView.setOnItemClickListener(adapterViewListener);

        final ImageView pinModeImg = findViewById(R.id.pinModeImg);
        final SwitchCompat pinSwitch = findViewById(R.id.setting_pinMode_switch);
        pinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences sharedPrefs = SettingsActivity.this.getSharedPreferences("settingsSwitch", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                if (pinSwitch.isChecked()) {
                    editor.putInt("Switch", SWITCH_MODE_COLOUR);
                    pinModeImg.setImageDrawable(getDrawable(R.drawable.pinuno));
                    switchSettingsItemImg(SWITCH_MODE_COLOUR, adapter, listView);
                } else {
                    editor.putInt("Switch", SWITCH_MODE_ICON);
                    pinModeImg.setImageDrawable(getDrawable(R.drawable.wallpin));
                    switchSettingsItemImg(SWITCH_MODE_ICON, adapter, listView);
                }
                editor.commit();
            }
        });
    }

    private void switchSettingsItemImg(int mode, ArrayAdapter<SettingsItem> adapter, ListView listView) {
        List<Integer> list = mode == 0 ? MapsActivity.colours : MapsActivity.icons;
        for(int i = 0; i < 6; i++) {
            SettingsItem item = adapter.getItem(i);
            item.setPinImgId(list.get(i));
        }
        adapter.notifyDataSetChanged();
    }

    class settingsAdapter extends ArrayAdapter<SettingsItem> {

        Context context;
        ArrayList<SettingsItem> list;

        public settingsAdapter(Context context, int resource, ArrayList<SettingsItem> objects){
            super(context, resource, objects);
            this.context = context;
            this.list = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            SettingsItem item = list.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            View view  = inflater.inflate(R.layout.settings_adaptor, null);

            TextView pinType = (TextView) view.findViewById(R.id.settings_pin_text);
            pinType.setText(item.getPinType());

            final ImageView pinImg = view.findViewById(R.id.settings_pin_image);
            pinImg.setImageDrawable(getDrawable(item.getPinImgId()));

            AppCompatButton customizeBttn = view.findViewById(R.id.customizeBttn);

            final int index = position;
            final SettingsItem view_item = item;
            customizeBttn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showColourPickerDialogue(MapsActivity.currentMode, getDrawable(view_item.getPinImgId()), SettingsActivity.this, index, pinImg);
                }
            });


            return view;
        }
        }

    public static void showColourPickerDialogue(PinMode mode, final Drawable img, final Activity activity, final int position, final View view) {

        final List<Integer> list = mode == PinMode.COLOUR ? MapsActivity.colours : MapsActivity.icons;

        ColorPicker picker = new ColorPicker(activity);
        picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
            @Override
            public void onColorPicked(int pickedColor) {
                img.setTint(pickedColor);
                Drawable img = activity.getDrawable(list.get(position));
                DrawableCompat.setTint(img, pickedColor);
                MapsActivity.pinsList.get(position).setCompoundDrawablesWithIntrinsicBounds(null, null, img,null);
                list.set(position, list.get(position));

                ImageView imgView = (ImageView) view;
                imgView.setImageDrawable(img);

            }
        });
        picker.show();


    }
}

