package me.ialistannen.wallpaperchanger.view.settings;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;
import me.ialistannen.wallpaperchanger.R;
import me.ialistannen.wallpaperchanger.automatedchanging.ForegroundBroadcastRegistrationService;

/**
 * The fragment responsible for displaying the settings.
 */
public class SettingsFragment extends PreferenceFragment implements
    OnSharedPreferenceChangeListener {

  private static final String TAG = "SettingsFragment";

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preferences);
  }

  @Override
  public void onResume() {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onPause() {
    super.onPause();
    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    String changingEnabledKey = getActivity()
        .getString(R.string.settings_automated_changing_enabled_key);
    String durationChangeKey = getActivity()
        .getString(R.string.settings_automated_changing_duration_key);

    if (!changingEnabledKey.equals(key) && !durationChangeKey.equals(key)) {
      return;
    }

    if (sharedPreferences.getBoolean(changingEnabledKey, false)) {
      ForegroundBroadcastRegistrationService.startIfEnabled(getActivity());
      Log.d(TAG, "onSharedPreferenceChanged: Enabled automatic changing, started service");
    } else {
      ForegroundBroadcastRegistrationService.cancel(getActivity());
      Log.d(TAG, "onSharedPreferenceChanged: Disabled automatic changing, stopped service");
    }
  }
}
