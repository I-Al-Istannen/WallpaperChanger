package me.ialistannen.wallpaperchanger.images.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import me.ialistannen.wallpaperchanger.images.provider.ImageProvider;

/**
 * Obtains a random image from an {@link ImageProvider}.
 */
public class RandomImageObtainTask extends AsyncTask<ImageProvider, Void, Bitmap> {

  @Override
  protected Bitmap doInBackground(ImageProvider... imageProviders) {
    if (imageProviders.length != 1) {
      throw new IllegalArgumentException("You need to pass ONE ImageProvider");
    }

    ImageProvider imageProvider = imageProviders[0];

    String imageUrlString = imageProvider.getRandomImageUrl();

    try {
      return doExceptional(imageUrlString);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return null;
  }

  private Bitmap doExceptional(String imageUrlString) throws IOException {
    HttpURLConnection urlConnection = (HttpURLConnection) new URL(imageUrlString).openConnection();
    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0");

    Options options = new Options();
    options.inDensity = DisplayMetrics.DENSITY_DEFAULT;
    options.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
    options.inScaled = false;

    return BitmapFactory.decodeStream(urlConnection.getInputStream(), null, options);
  }
}
