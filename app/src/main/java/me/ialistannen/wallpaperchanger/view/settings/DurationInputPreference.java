package me.ialistannen.wallpaperchanger.view.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import me.ialistannen.wallpaperchanger.R;
import me.ialistannen.wallpaperchanger.util.DurationFormatUtil;

/**
 * A {@link DialogPreference} for inputting a Duration.
 */
public class DurationInputPreference extends DialogPreference {

  @IdRes
  private static int SPINNER_DAYS = R.id.preference_duration_input_spinner_days;
  @IdRes
  private static int SPINNER_HOURS = R.id.preference_duration_input_spinner_hours;
  @IdRes
  private static int SPINNER_MINUTES = R.id.preference_duration_input_spinner_minutes;
  @IdRes
  private static int SPINNER_SECONDS = R.id.preference_duration_input_spinner_seconds;


  private Set<Integer> hiddenSpinners = new HashSet<>();
  private long durationMillis = -1;
  private View rootView;

  @SuppressWarnings("unused")
  public DurationInputPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    init(attrs, context);
  }

  @SuppressWarnings("unused")
  public DurationInputPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init(attrs, context);
  }

  private void init(AttributeSet attributeSet, Context context) {
    hideIrrelevantSpinners(attributeSet, context);
    setDialogLayoutResource(R.layout.preference_duration_input);
  }

  private void hideIrrelevantSpinners(AttributeSet attributeSet, Context context) {
    TypedArray attributes = context
        .obtainStyledAttributes(attributeSet, R.styleable.DurationInputPreference);

    if (!attributes.getBoolean(R.styleable.DurationInputPreference_enable_days, true)) {
      hiddenSpinners.add(SPINNER_DAYS);
    }
    if (!attributes.getBoolean(R.styleable.DurationInputPreference_enable_hours, true)) {
      hiddenSpinners.add(SPINNER_HOURS);
    }
    if (!attributes.getBoolean(R.styleable.DurationInputPreference_enable_minutes, true)) {
      hiddenSpinners.add(SPINNER_MINUTES);
    }
    if (!attributes.getBoolean(R.styleable.DurationInputPreference_enable_seconds, true)) {
      hiddenSpinners.add(SPINNER_SECONDS);
    }

    attributes.recycle();
  }

  @Override
  protected void onBindDialogView(View view) {
    super.onBindDialogView(view);

    rootView = view;

    Map<TimeUnit, Long> parts = DurationFormatUtil.splitIntoParts(durationMillis);

    setSpinnerDefaults(SPINNER_DAYS, view, 365, parts.get(TimeUnit.DAYS));
    setSpinnerDefaults(SPINNER_HOURS, view, 23, parts.get(TimeUnit.HOURS));
    setSpinnerDefaults(SPINNER_MINUTES, view, 59, parts.get(TimeUnit.MINUTES));
    setSpinnerDefaults(SPINNER_SECONDS, view, 59, parts.get(TimeUnit.SECONDS));

    for (Integer hiddenSpinner : hiddenSpinners) {
      hideSpinner(hiddenSpinner, view);
    }
  }

  private void setSpinnerDefaults(@IdRes int id, View view, int max, long currentValue) {
    NumberPicker picker = view.findViewById(id);
    picker.setMinValue(0);
    picker.setMaxValue(max);
    picker.setValue((int) currentValue);
  }

  private void hideSpinner(@IdRes int id, View view) {
    view.findViewById(id).setEnabled(false);
  }

  @Override
  protected void onDialogClosed(boolean positiveResult) {
    super.onDialogClosed(positiveResult);
    if (positiveResult) {
      durationMillis = computeDurationMillis();
      getSharedPreferences().edit().putLong(getKey(), durationMillis).apply();

      notifyChanged();
    }
    rootView = null;
  }

  @Override
  protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
    super.onSetInitialValue(restorePersistedValue, defaultValue);

    long defaultLongValue;
    if (defaultValue != null && defaultValue instanceof CharSequence) {
      defaultLongValue = Long.parseLong(defaultValue.toString());
    } else {
      defaultLongValue = 0;
    }

    if (restorePersistedValue) {
      durationMillis = getSharedPreferences().getLong(getKey(), defaultLongValue);
    } else if (shouldPersist()) {
      durationMillis = getPersistedLong(defaultLongValue);
    } else {
      durationMillis = 0;
    }
  }

  @Override
  protected Long onGetDefaultValue(TypedArray a, int index) {
    return Long.parseLong(a.getString(index));
  }

  @Override
  public CharSequence getSummary() {
    String format = getContext()
        .getString(R.string.preference_duration_input_summary_duration_format);

    String durationAsString = DurationFormatUtil.toSimpleString(durationMillis, format);

    return String.format(Locale.ROOT, super.getSummary().toString(), durationAsString);
  }

  private long computeDurationMillis() {
    long result = 0;
    result += TimeUnit.DAYS.toMillis(getSpinnerValue(SPINNER_DAYS));
    result += TimeUnit.HOURS.toMillis(getSpinnerValue(SPINNER_HOURS));
    result += TimeUnit.MINUTES.toMillis(getSpinnerValue(SPINNER_MINUTES));
    result += TimeUnit.SECONDS.toMillis(getSpinnerValue(SPINNER_SECONDS));

    return result;
  }

  private int getSpinnerValue(@IdRes int id) {
    return ((NumberPicker) rootView.findViewById(id)).getValue();
  }

}
