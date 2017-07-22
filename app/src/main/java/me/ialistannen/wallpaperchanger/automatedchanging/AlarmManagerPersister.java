package me.ialistannen.wallpaperchanger.automatedchanging;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * A class that aims to use the {@link AlarmManager} to persists a service.
 */
class AlarmManagerPersister {

  /**
   * Tries to persist by restarting it with the {@link AlarmManager}.
   *
   * @param context The {@link Context} to get the {@link AlarmManager} for
   * @param intent The {@link Intent} to start
   * @param intervalMillis The interval in milliseconds between restarts of the context
   */
  void persist(Context context, Intent intent, long intervalMillis) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    PendingIntent pendingIntent = createPendingIntent(context, intent);

    Log.i("AlarmManagerPersister", "persist: Interval " + intervalMillis);

    alarmManager.setInexactRepeating(
        AlarmManager.ELAPSED_REALTIME, 0, intervalMillis, pendingIntent
    );
  }

  /**
   * Cancels the persisted task.
   *
   * @param context The {@link Context} to get the {@link AlarmManager} for
   * @param intent The {@link Intent} to start
   */
  void cancel(Context context, Intent intent) {
    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

    alarmManager.cancel(createPendingIntent(context, intent));
  }

  private PendingIntent createPendingIntent(Context context, Intent intent) {
    return PendingIntent.getBroadcast(
        context.getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
    );
  }
}
