package me.ialistannen.wallpaperchanger;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import me.ialistannen.wallpaperchanger.images.provider.ImageProvider;
import me.ialistannen.wallpaperchanger.images.provider.space.SpaceTelescopeTop100Provider;
import me.ialistannen.wallpaperchanger.images.util.RandomImageObtainTask;
import me.ialistannen.wallpaperchanger.wallpaper.WallpaperChanger;

public class MainActivity extends AppCompatActivity {

  private final ImageProvider imageProvider = new SpaceTelescopeTop100Provider();
  private RetainFragment retainFragment;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

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
    }.execute(imageProvider);
  }

  /**
   * Called when the user clicks the accept button.
   *
   * @param view The clicked button
   */
  public void onAcceptWallpaper(View view) {
    WallpaperChanger.changeTo(retainFragment.getCachedImage(), this);
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
