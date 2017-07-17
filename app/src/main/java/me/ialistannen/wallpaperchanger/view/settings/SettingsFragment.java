package me.ialistannen.wallpaperchanger.view.settings;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import me.ialistannen.wallpaperchanger.R;

/**
 * The fragment responsible for displaying the settings.
 */
public class SettingsFragment extends PreferenceFragment {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    addPreferencesFromResource(R.xml.preferences);
  }
}
