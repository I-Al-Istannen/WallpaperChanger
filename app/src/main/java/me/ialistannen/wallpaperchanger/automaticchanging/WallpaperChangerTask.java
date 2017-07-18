package me.ialistannen.wallpaperchanger.automaticchanging;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.widget.Toast;
import me.ialistannen.wallpaperchanger.R;
import me.ialistannen.wallpaperchanger.images.provider.ProviderFactory;
import me.ialistannen.wallpaperchanger.images.util.RandomImageObtainTask;
import me.ialistannen.wallpaperchanger.util.scheduling.ScheduledTask;
import me.ialistannen.wallpaperchanger.wallpaper.WallpaperChanger;

/**
 * A {@link ScheduledTask} that changes the wallpaper.
 */
public class WallpaperChangerTask extends ScheduledTask {

  public void registerIfEnabled(Context context) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    String isEnabledKey = context.getString(R.string.settings_automated_changing_enabled_key);

    if (!preferences.getBoolean(isEnabledKey, false)) {
      return;
    }

    // TODO: 18.07.17 Continue here 
  }

  @Override
  public void onReceive(final Context context, Intent intent) {
    Toast.makeText(context, R.string.wallpaper_changer_task_changing_now, Toast.LENGTH_SHORT)
        .show();

    new RandomImageObtainTask() {
      @Override
      protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        WallpaperChanger.changeTo(bitmap, context);
      }
    }.execute(ProviderFactory.getInstance(context));
  }
}
