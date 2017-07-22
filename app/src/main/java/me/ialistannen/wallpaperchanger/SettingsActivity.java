package me.ialistannen.wallpaperchanger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import me.ialistannen.wallpaperchanger.view.settings.SettingsFragment;

/**
 * An activity to display the settings.
 */
public class SettingsActivity extends AppCompatActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getFragmentManager().beginTransaction()
        .replace(android.R.id.content, new SettingsFragment())
        .commit();
  }

}
