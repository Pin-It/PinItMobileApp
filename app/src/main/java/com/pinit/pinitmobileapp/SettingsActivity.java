package com.pinit.pinitmobileapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


import cn.qqtheme.framework.picker.ColorPicker;


public class SettingsActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final int COLOURS_MODE = 0;
    public static final int ICONS_MODE = 1;

    public static Map<Preference, String> preferenceMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pick_pocket")) {


        }

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceMap.put((getPreferenceManager().findPreference("pick_pocket")), "pick_pocket");
            preferenceMap.put((getPreferenceManager().findPreference("drunk")), "drunk");
            preferenceMap.put(getPreferenceManager().findPreference("robbery"), "robbery");
            preferenceMap.put(getPreferenceManager().findPreference("scam"), "scam");
            preferenceMap.put(getPreferenceManager().findPreference("harrassment"), "harrassment");
            preferenceMap.put(getPreferenceManager().findPreference("others"), "others");

            Preference button = (Preference) getPreferenceManager().findPreference("exitBttn");
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
                            showColourPickerDialogue(preference, MapsActivity.sharedPrefs, getActivity());
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

                        return true;
                    }
                });
            }
        }

        }

    public static void showColourPickerDialogue(final Preference bttn, SharedPreferences sharedPrefs, Activity activity) {
//        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("settingsInfo", MODE_PRIVATE);

        final SharedPreferences.Editor editor = MapsActivity.sharedPrefs.edit();

        ColorPicker picker = new ColorPicker(activity);
        picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
            @Override
            public void onColorPicked(int pickedColor) {
                bttn.getIcon().setTint(pickedColor);
                editor.putInt("pick_pocket", pickedColor);
                Log.w("SettingsActivity", Integer.toHexString(pickedColor));
                editor.commit();
            }
        });
        picker.show();


    }

}


