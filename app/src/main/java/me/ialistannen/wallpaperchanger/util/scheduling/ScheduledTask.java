package me.ialistannen.wallpaperchanger.util.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Changes a wallpaper after a given time.
 */
public abstract class ScheduledTask extends BroadcastReceiver {

  private static final String PING_KEY = "me.ialistannen.wallpaperchanger.ScheduledTask.PING";

  private final Intent pingIntent;
  private PendingIntent pendingIntent;

  private int periodMillis;
  private int delayMillis;
  private boolean registered;

  /**
   * The task will not be scheduled until you invoke {@link #register(int, int, Context)}.
   */
  public ScheduledTask() {
    pingIntent = new Intent(PING_KEY);
  }

  /**
   * Registers this {@link ScheduledTask} to run.
   *
   * <br>Multiple calls to this method will not start
   * a new task, unless {@link #cancel(Context)} was called.
   *
   * @param periodMillis The delay to wait between executions
   * @param delayMillis The delay before the first call
   * @param context The {@link Context} to get the ALARM_SERVICE for.
   */
  public void register(int periodMillis, int delayMillis, Context context) {
    if (registered) {
      return;
    }
    this.periodMillis = periodMillis;
    this.delayMillis = delayMillis;

    registerTask(context);
  }

  /**
   * Cancels this task if it is scheduled. Otherwise does nothing.
   *
   * @param context The {@link Context} to get the ALARM_SERVICE for
   */
  public void cancel(Context context) {
    getAlarmManager(context).cancel(createOrGetPendingIntent(context));
    pendingIntent = null;
    registered = false;
  }

  /**
   * Checks if this task is registered, i.e. the OS will call it after the specified intervals.
   *
   * <p>Use {@link #register(int, int, Context)} and {@link #cancel(Context)} to control this
   * behaviour.
   *
   * @return True if this task is registered.
   */
  public boolean isRegistered() {
    return registered;
  }

  private void registerTask(Context context) {
    getAlarmManager(context).setInexactRepeating(
        AlarmManager.ELAPSED_REALTIME,
        delayMillis,
        periodMillis,
        createOrGetPendingIntent(context)
    );

    context.registerReceiver(this, new IntentFilter(PING_KEY));

    registered = true;
  }

  private AlarmManager getAlarmManager(Context context) {
    return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
  }

  private PendingIntent createOrGetPendingIntent(Context context) {
    if (pendingIntent == null) {
      pendingIntent = PendingIntent.getBroadcast(
          context.getApplicationContext(),
          0,
          pingIntent,
          PendingIntent.FLAG_UPDATE_CURRENT
      );
    }

    return pendingIntent;
  }
}
