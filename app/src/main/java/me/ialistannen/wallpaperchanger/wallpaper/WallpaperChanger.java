package me.ialistannen.wallpaperchanger.wallpaper;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.IOException;
import java.net.URL;

/**
 * Changes the wallpaper.
 */
public class WallpaperChanger extends AsyncTask<String, Void, Void> {

  private Context context;

  private WallpaperChanger(Context context) {
    this.context = context;
  }

  @Override
  protected Void doInBackground(String... strings) {
    if (strings.length != 1) {
      throw new IllegalArgumentException("You need to pass ONE url.");
    }

    try {
      URL url = new URL(strings[0]);
      WallpaperManager.getInstance(context.getApplicationContext()).setStream(url.openStream());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Changes the wallpaper to a given URL.
   *
   * @param image The {@link Bitmap} to change it to
   * @param context The {@link Context}
   */
  public static void changeTo(final Bitmap image, final Context context) {
    new AsyncTask<Void, Void, Boolean>() {

      @Override
      protected Boolean doInBackground(Void... voids) {
        try {
          WallpaperManager.getInstance(context).setBitmap(image);

          return true;
        } catch (IOException e) {
          e.printStackTrace();
        }

        return false;
      }

      @Override
      protected void onPostExecute(Boolean successful) {
        super.onPostExecute(successful);

        if (!successful) {
          Toast.makeText(
              context, "An error occurred setting the wallpaper", Toast.LENGTH_SHORT
          ).show();
        }
      }
    }.execute();
  }
}
