package com.pinit.pinitmobileapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

//import com.flask.colorpicker.ColorPickerView;
//import com.flask.colorpicker.OnColorSelectedListener;
//import com.flask.colorpicker.builder.ColorPickerClickListener;
//import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.HashMap;
import java.util.Map;

import cn.qqtheme.framework.picker.ColorPicker;


public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final int COLOURS_MODE = 0;
    public static final int ICONS_MODE = 1;

    public static Map<Preference, String> preferenceMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceMap.put(getPreferenceManager().findPreference("pick_pocket"), "pick_pocket");
        preferenceMap.put(getPreferenceManager().findPreference("drunk"), "drunk");
        preferenceMap.put(getPreferenceManager().findPreference("robbery"), "robbery");
        preferenceMap.put(getPreferenceManager().findPreference("scam"), "scam");
        preferenceMap.put(getPreferenceManager().findPreference("harrassment"), "harrassment");
        preferenceMap.put(getPreferenceManager().findPreference("others"), "others");

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pick_pocket")) {
            Preference preference = findPreference(key);
            preference.getIcon().setTint(sharedPreferences.getInt(key, 000000));

        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference button = (Preference) getPreferenceManager().findPreference("exitBttn");
            final SharedPreferences.Editor editor = MapsActivity.sharedPreferences.edit();
            if (button != null) {
                button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference arg0) {
                        getActivity().finish();
                        return true;
                    }
                });
            }

//            final ListPreference pickPocket = (ListPreference) getPreferenceManager().findPreference("pick_pocket");



            for (final Map.Entry<Preference, String> entry : preferenceMap.entrySet()) {
                final Preference preference = getPreferenceManager().findPreference(entry.getValue());
                if (preference != null) {
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            showColourPickerDialogue(entry.getKey());
                            return true;
                        }
                    });
                }
            }


            final SwitchPreference switchForIcons = (SwitchPreference) getPreferenceManager().findPreference("switchForIcons");
            switchForIcons.setChecked(true);
            switchForIcons.setIcon(R.drawable.pinuno);

            if (switchForIcons != null) {
                switchForIcons.setOnPreferenceChangeListener(new SwitchPreference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        if (switchForIcons.isChecked()) {
                            editor.putInt("ChangeIcons", ICONS_MODE);
                            editor.commit();
                            switchForIcons.setIcon(R.drawable.wallpin);

                        } else {
                            editor.putInt("ChangeIcons", COLOURS_MODE);
                            editor.commit();
                            switchForIcons.setIcon(R.drawable.pinuno);
                        }
                        return true;
                    }
                });
            }
        }


        public void showColourPickerDialogue(final Preference bttn) {
            final SharedPreferences.Editor editor = MapsActivity.sharedPreferences.edit();
//            ColorPickerDialogBuilder
//                    .with(getActivity().getApplicationContext())
//                    .setTitle("Choose color")
//                    .initialColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.colorAccent))
//                    .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
//                    .density(12)
//                    .setOnColorSelectedListener(new OnColorSelectedListener() {
//                        @Override
//                        public void onColorSelected(int selectedColor) {
//
//                        }
//                    })
//                    .setPositiveButton("ok", new ColorPickerClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
//
//                        }
//                    })
//                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                        }
//                    })
//                    .build()
//                    .show();
//            AmbilWarnaDialog colourPicker = new AmbilWarnaDialog(getActivity().getApplicationContext(), 0x7f060026, new AmbilWarnaDialog.OnAmbilWarnaListener() {
//                @Override
//                public void onCancel(AmbilWarnaDialog dialog) {
//                    dialog.getDialog().dismiss();
//                }
//
//                @Override
//                public void onOk(AmbilWarnaDialog dialog, int color) {
//                    bttn.getIcon().setTint(color);
//                    editor.putInt(bttn.getKey(), color);
//                    editor.commit();
//
//                }
//            });
//            colourPicker.show();
            ColorPicker picker = new ColorPicker(getActivity());
            picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
                @Override
                public void onColorPicked(int pickedColor) {
                    bttn.getIcon().setTint(pickedColor);
                    editor.putInt(bttn.getKey(), pickedColor);
                    editor.commit();
                }
            });
            picker.show();
//


        }
    }
}

