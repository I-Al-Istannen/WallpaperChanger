package me.ialistannen.wallpaperchanger;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import me.ialistannen.wallpaperchanger.automatedchanging.ForegroundBroadcastRegistrationService;
import me.ialistannen.wallpaperchanger.images.provider.ProviderFactory;
import me.ialistannen.wallpaperchanger.images.util.RandomImageObtainTask;
import me.ialistannen.wallpaperchanger.wallpaper.WallpaperChanger;

public class MainActivity extends AppCompatActivity {

  private RetainFragment retainFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    PreferenceManager.setDefaultValues(this, R.xml.preferences, true);

    if (savedInstanceState == null) {
      setSupportActionBar((Toolbar) findViewById(R.id.activity_main_actionbar));

      ForegroundBroadcastRegistrationService.startIfEnabled(this);
    }

    if (retainFragment == null) {
      retainFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
    }

    findViewById(R.id.activity_main_accept_button)
        .setEnabled(retainFragment.getCachedImage() != null);
  }

  /**
   * Called when the user clicks the preview button.
   *
   * @param view The clicked button
   */
  public void onPreviewNextWallpaper(View view) {
    final Button nextButton = (Button) findViewById(R.id.activity_main_next_button);

    nextButton.setText(R.string.activity_main_loading_image);
    nextButton.setEnabled(false);

    new RandomImageObtainTask() {
      @Override
      protected void onPostExecute(Bitmap bitmap) {
        nextButton.setText(R.string.activity_main_next_button);
        nextButton.setEnabled(true);

        if (bitmap == null) {
          return;
        }

        ImageView imageView = (ImageView) findViewById(R.id.activity_main_background_image_preview);
        imageView.setImageBitmap(bitmap);

        retainFragment.setCachedImage(bitmap);

        findViewById(R.id.activity_main_accept_button).setEnabled(true);
      }
    }.execute(ProviderFactory.getInstance(this));
  }

  /**
   * Called when the user clicks the accept button.
   *
   * @param view The clicked button
   */
  public void onAcceptWallpaper(View view) {
    WallpaperChanger.changeTo(retainFragment.getCachedImage(), this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main_action_bar, menu);

    return true;
  }

  public void onShowSettings(MenuItem item) {
    startActivity(new Intent(this, SettingsActivity.class));
  }

  public static class RetainFragment extends Fragment {

    private static final String TAG = "me.ialistannen.wallpaperchanger.RetainFragment";

    private Bitmap cachedImage;

    /**
     * @param fragmentManager The {@link FragmentManager} to use
     * @return The created (or cached) {@link RetainFragment}
     */
    static RetainFragment findOrCreateRetainFragment(FragmentManager fragmentManager) {
      RetainFragment fragment = (RetainFragment) fragmentManager.findFragmentByTag(TAG);

      if (fragment == null) {
        fragment = new RetainFragment();
        fragmentManager.beginTransaction().add(fragment, TAG).commit();
      }

      return fragment;
    }

    void setCachedImage(Bitmap cachedImage) {
      this.cachedImage = cachedImage;
    }

    Bitmap getCachedImage() {
      return cachedImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setRetainInstance(true);
    }
  }
}
