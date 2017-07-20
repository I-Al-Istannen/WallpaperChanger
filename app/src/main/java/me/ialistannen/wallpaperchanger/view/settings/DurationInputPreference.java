package me.ialistannen.wallpaperchanger.view.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;
import me.ialistannen.wallpaperchanger.R;

/**
 * A {@link DialogPreference} for inputting a Duration.
 */
public class DurationInputPreference extends DialogPreference {

  public DurationInputPreference(Context context, AttributeSet attrs) {
    super(context, attrs);

    init();
  }

  public DurationInputPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    init();
  }

  private void init() {
    setDialogLayoutResource(R.layout.preference_duration_input);
  }

  @Override
  protected void onBindDialogView(View view) {
    super.onBindDialogView(view);

    NumberPicker numberPicker = view.findViewById(R.id.TESTE_ME);
    numberPicker.setMinValue(0);
    numberPicker.setMaxValue(100);
  }
}
