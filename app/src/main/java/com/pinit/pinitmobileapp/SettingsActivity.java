package com.pinit.pinitmobileapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;
import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;


public class SettingsActivity extends Activity {

    public static Map<Preference, Integer> preferenceMap = new HashMap<>();
    public static Map<Integer, Integer> drawableMap = new HashMap<>();

    public static final int SWITCH_MODE_COLOUR = 0;
    public static final int SWITCH_MODE_ICON = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceMap.put(findPreference("pick_pocket"), 0);
            preferenceMap.put(findPreference("drunk"), 1);
            preferenceMap.put(findPreference("robbery"), 2);
            preferenceMap.put(findPreference("scam"), 3);
            preferenceMap.put(findPreference("harrassment"), 4);
            preferenceMap.put(findPreference("others"), 5);

            drawableMap.put(getActivity().getColor(R.color.colorAccent), R.drawable.pick_pocket);
            drawableMap.put(getActivity().getColor(R.color.redLight), R.drawable.red_light);
            drawableMap.put(getActivity().getColor(R.color.redMiddle), R.drawable.red_middle);
            drawableMap.put(getActivity().getColor(R.color.redDarkest), R.drawable.red_darkest);

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


            for (final Map.Entry<Preference, Integer> entry : preferenceMap.entrySet()) {
                    entry.getKey().setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {
                            showColourPicker(preference, getActivity());
                            return true;
                        }
                    });
            }


            final SwitchPreference switchForIcons = (SwitchPreference) findPreference("switchForIcons");
            if (MapsActivity.currentMode == PinMode.COLOUR) {
                switchForIcons.setIcon(getActivity().getDrawable(R.drawable.pick_pocket));
            } else {
                switchForIcons.setIcon(getActivity().getDrawable(R.drawable.wallpin));
            }

            if (switchForIcons != null) {
                switchForIcons.setOnPreferenceChangeListener(new SwitchPreference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object o) {
                        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("settingsSwitch", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        if (switchForIcons.isChecked()) {
                            editor.putInt("Switch", SWITCH_MODE_ICON);
                            preference.setIcon(getActivity().getDrawable(R.drawable.wallpin));
                        } else {
                            editor.putInt("Switch", SWITCH_MODE_COLOUR);
                            preference.setIcon(getActivity().getDrawable(R.drawable.pick_pocket));
                        }
                        editor.commit();
                        return true;
                    }
                });
            }
        }


        }

        public static void showColourPicker(final Preference prefsBttn, final Activity activity) {
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        colorPickerDialog.initialize(R.string.color_picker_default_title,
                new int[] {
                        activity.getColor(R.color.colorAccent),
                        activity.getColor(R.color.redDarkest),
                        activity.getColor(R.color.redMiddle),
                        activity.getColor(R.color.redLight)

                }, activity.getResources().getColor(R.color.colorPrimaryDark), 4, 4);

        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
            @Override
            public void onColorSelected(int colour) {
                prefsBttn.getIcon().setTint(colour);
                int drawableID = findDrawable(colour);

                int position = preferenceMap.get(prefsBttn);
                MapsActivity.pinsList.get(position).setCompoundDrawablesWithIntrinsicBounds(null, null, activity.getDrawable(drawableID),null);
                MapsActivity.colours.set(position, drawableID);

            }
        });

        android.app.FragmentManager fm = activity.getFragmentManager();
        colorPickerDialog.setArguments(R.string.colour_picker_title, 4,4);
        colorPickerDialog.setStyle(R.style.ColourPickerDialogueStyle, R.style.ColourPickerDialogueStyle);
        colorPickerDialog.show(fm, "colorpicker");
    }

    public static int findDrawable(int colour) {
        int drawableID = 0;
        for (Map.Entry<Integer, Integer> entry : drawableMap.entrySet()) {
            if (entry.getKey() == colour) {
                drawableID = entry.getValue();
            }
        }
        return drawableID;
    }

//    public void showColourPicker(final View view) {
//        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
//        colorPickerDialog.initialize(R.string.color_picker_default_title,
//                new int[] {
//                        getResources().getColor(R.color.colorPrimary),
//
//                }, getResources().getColor(R.color.colorPrimaryDark), 3, 2);
//
//        colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
//            @Override
//            public void onColorSelected(int colour) {
//                AppCompatButton bttn = (AppCompatButton) view;
//                bttn.setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.haha), null);
//                SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putInt("haha", R.drawable.haha);
//                editor.commit();
////                MapsActivity.pinsList.get(0).setCompoundDrawablesWithIntrinsicBounds(null, null, getDrawable(R.drawable.haha), null);
//
//            }
//        });
//
//        android.app.FragmentManager fm = getFragmentManager();
//        colorPickerDialog.show(fm, "colorpicker");
//    }
//public static void showColourPickerDialogue(final Preference bttn, Activity activity) {
////        SharedPreferences sharedPrefs = getApplicationContext().getSharedPreferences("settingsInfo", MODE_PRIVATE);
//
//    SharedPreferences sharedPrefs = activity.getSharedPreferences("settings", Context.MODE_PRIVATE);
//    final SharedPreferences.Editor editor = sharedPrefs.edit();
//
//    ColorPicker picker = new ColorPicker(activity);
//    picker.setOnColorPickListener(new ColorPicker.OnColorPickListener() {
//        @Override
//        public void onColorPicked(int pickedColor) {
//            bttn.getIcon().setTint(pickedColor);
//            editor.putInt("pick_pocket", pickedColor);
//            Log.w("SettingsActivity", Integer.toHexString(pickedColor));
//            editor.commit();
//        }
//    });
//    picker.show();
//
//
//}
}

