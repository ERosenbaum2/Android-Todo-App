package com.example.todoapp;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        SwitchPreferenceCompat nightPref = findPreference("night_mode");
        if (nightPref != null) {
            nightPref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean on = (Boolean) newValue;
                AppCompatDelegate.setDefaultNightMode(
                        on ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                );
                getActivity().recreate();
                return true;
            });
        }
    }
}
