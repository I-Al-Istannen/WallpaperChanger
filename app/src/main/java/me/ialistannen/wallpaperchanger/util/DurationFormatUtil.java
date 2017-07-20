package me.ialistannen.wallpaperchanger.util;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Helps formatting durations.
 */
public class DurationFormatUtil {

  /**
   * Formats a String with the duration.
   *
   * @param durationMillis The duration in milliseconds
   * @param formatString The format string. Needs for long placeholders
   * @return The formatted String
   */
  public static String toSimpleString(long durationMillis, String formatString) {
    Map<TimeUnit, Long> parts = splitIntoParts(durationMillis);

    return String.format(
        Locale.ROOT,
        formatString,
        parts.get(TimeUnit.DAYS),
        parts.get(TimeUnit.HOURS),
        parts.get(TimeUnit.MINUTES),
        parts.get(TimeUnit.SECONDS)
    );
  }

  /**
   * @param durationMillis The duration in milliseconds
   * @return A Map with the components,{@link TimeUnit#DAYS} {@link TimeUnit#HOURS} {@link
   * TimeUnit#MINUTES} {@link TimeUnit#SECONDS}
   */
  public static Map<TimeUnit, Long> splitIntoParts(long durationMillis) {
    long days = TimeUnit.MILLISECONDS.toDays(durationMillis);
    durationMillis -= TimeUnit.DAYS.toMillis(days);

    long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
    durationMillis -= TimeUnit.HOURS.toMillis(hours);

    long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
    durationMillis -= TimeUnit.MINUTES.toMillis(minutes);

    long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMillis);

    Map<TimeUnit, Long> map = new EnumMap<>(TimeUnit.class);
    map.put(TimeUnit.DAYS, days);
    map.put(TimeUnit.HOURS, hours);
    map.put(TimeUnit.MINUTES, minutes);
    map.put(TimeUnit.SECONDS, seconds);

    return map;
  }
}
