package me.ialistannen.wallpaperchanger.automatedchanging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import me.ialistannen.wallpaperchanger.R;
import me.ialistannen.wallpaperchanger.images.provider.ProviderFactory;
import me.ialistannen.wallpaperchanger.images.util.RandomImageObtainTask;
import me.ialistannen.wallpaperchanger.wallpaper.WallpaperChanger;

/**
 * The receiver listening for {@link android.content.Intent#ACTION_SCREEN_ON} broadcasts.
 */
public class ScreenOnReceiver extends BroadcastReceiver {

  private static final String TAG = "ScreenOnReceiver";

  @Override
  public void onReceive(final Context context, Intent intent) {
    if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
      ForegroundBroadcastRegistrationService.startIfEnabled(context);
      return;
    }

    if (!ForegroundBroadcastRegistrationService.shouldRun(context)) {
      ForegroundBroadcastRegistrationService.cancel(context);
      return;
    }

    if (!isOnWifiOrAllowedToChange(context)) {
      return;
    }

    long lastChanged = getLastChanged(context);

    long changeInterval = getChangeInterval(context);

    Log.d(
        TAG,
        "onReceive: LastChanged " + lastChanged + " interval " + changeInterval
            + " for Intent " + intent.getAction()
    );

    if (System.currentTimeMillis() > lastChanged + changeInterval) {
      Log.d(TAG, "onReceive: Changing now. Set to " + System.currentTimeMillis());
      setLastChanged(context, System.currentTimeMillis());

      changeWallpaper(context);
    }
  }

  private boolean isOnWifiOrAllowedToChange(Context context) {
    String onlyChangeInWifiKey = context.getString(
        R.string.settings_automated_changing_only_in_wifi_key
    );
    boolean onlyChangeInWifi = PreferenceManager
        .getDefaultSharedPreferences(context).getBoolean(onlyChangeInWifiKey, true);

    if (!onlyChangeInWifi) {
      return true;
    }

    ConnectivityManager connectionService = (ConnectivityManager) context
        .getSystemService(Context.CONNECTIVITY_SERVICE);

    NetworkInfo activeNetworkInfo = connectionService.getActiveNetworkInfo();

    //noinspection SimplifiableIfStatement
    if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
      return false;
    }

    return activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI
        || activeNetworkInfo.getType() == ConnectivityManager.TYPE_ETHERNET;
  }

  private long getLastChanged(Context context) {
    File cacheFile = getCacheFile(context);

    List<String> contents = readContents(cacheFile);

    if (contents.isEmpty()) {
      return 0;
    }

    return parseLong(contents.get(0), 0);
  }

  private File getCacheFile(Context context) {
    return new File(context.getCacheDir(), "lastChanged.txt");
  }

  private List<String> readContents(File file) {
    if (!file.exists()) {
      return Collections.emptyList();
    }

    try (FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader)) {

      List<String> list = new ArrayList<>();

      String tmp;
      while ((tmp = reader.readLine()) != null) {
        list.add(tmp);
      }

      return list;

    } catch (IOException e) {
      Log.w(TAG, "readContents: Error reading last changed file", e);
    }

    return Collections.emptyList();
  }

  @SuppressWarnings("SameParameterValue")
  private long parseLong(String string, long defaultValue) {
    try {
      return Long.parseLong(string);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private long getChangeInterval(Context context) {
    String preferenceKey = context.getString(R.string.settings_automated_changing_duration_key);
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getLong(preferenceKey, TimeUnit.HOURS.toMillis(1));
  }

  private void setLastChanged(Context context, long lastChanged) {
    File cacheFile = getCacheFile(context);
    writeToFile(cacheFile, Collections.singletonList(String.valueOf(lastChanged)));
  }

  private void writeToFile(File file, Iterable<String> contents) {
    createFile(file);

    try (FileWriter fileWriter = new FileWriter(file);
        BufferedWriter writer = new BufferedWriter(fileWriter)) {

      for (String content : contents) {
        writer.write(content);
        writer.newLine();
      }
    } catch (IOException e) {
      Log.w(TAG, "writeToFile: Error saving last changed file", e);
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void createFile(File file) {
    try {
      if (!file.exists()) {
        file.createNewFile();
      }
    } catch (IOException e) {
      Log.i(TAG, "createFile: Error creating the last changed file", e);
    }
  }

  private void changeWallpaper(final Context context) {

    new RandomImageObtainTask() {
      @Override
      protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        if (bitmap == null) {
          return;
        }

        WallpaperChanger.changeTo(bitmap, context);
      }
    }.execute(ProviderFactory.getInstance(context));
  }
}
