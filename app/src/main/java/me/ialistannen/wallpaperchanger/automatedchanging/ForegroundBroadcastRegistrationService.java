package me.ialistannen.wallpaperchanger.automatedchanging;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import me.ialistannen.wallpaperchanger.R;

/**
 * A {@link android.app.Service} to register SCREEN_ON
 */
public class ForegroundBroadcastRegistrationService extends Service {

  private static final String TAG = "BroadcastRegister";

  private BroadcastReceiver onReceiver;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onDestroy() {
    if (onReceiver != null) {
      unregisterReceiver(onReceiver);
      onReceiver = null;
    }
    Log.i(getClass().getSimpleName(), "I was killed.");
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    if (onReceiver != null) {
      unregisterReceiver(onReceiver);
    }

    onReceiver = new ScreenOnReceiver();

    IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
    registerReceiver(onReceiver, intentFilter);

    Log.i(TAG, "onCreate: Created the receiver");

    Intent startupIntent = createAlarmManagerIntent(this);
    new AlarmManagerPersister().persist(this, startupIntent, getChangeInterval(this));

    return START_STICKY;
  }

  private static Intent createAlarmManagerIntent(Context context) {
    Intent startupIntent = new Intent(context.getApplicationContext(), ScreenOnReceiver.class);
    startupIntent.setAction("Called by my alarm manager!");
    return startupIntent;
  }

  private long getChangeInterval(Context context) {
    String preferenceKey = context.getString(R.string.settings_automated_changing_duration_key);
    return PreferenceManager.getDefaultSharedPreferences(context)
        .getLong(preferenceKey, TimeUnit.HOURS.toMillis(1));
  }

  /**
   * Starts the service if it is enabled.
   *
   * @param context The {@link Context} to use to fetch settings
   */
  public static void startIfEnabled(Context context) {
    if (!shouldRun(context)) {
      return;
    }

    // reset it
    cancel(context);

    Intent serviceIntent = getStartIntent(context);
    context.startService(serviceIntent);

    Log.i(TAG, "startIfEnabled: I was called and I've done it!");
  }

  private static Intent getStartIntent(Context context) {
    return new Intent(
        context, ForegroundBroadcastRegistrationService.class
    );
  }

  /**
   * Stops the service.
   *
   * @param context The {@link Context} used to start it
   */
  public static void cancel(Context context) {
    new AlarmManagerPersister().cancel(context, createAlarmManagerIntent(context));
    context.stopService(getStartIntent(context));
  }

  /**
   * @param context The {@link Context} to use to fetch settings
   * @return True if the service should run
   */
  static boolean shouldRun(Context context) {
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    String isEnabledKey = context.getString(R.string.settings_automated_changing_enabled_key);

    return preferences.getBoolean(isEnabledKey, false);
  }
}
