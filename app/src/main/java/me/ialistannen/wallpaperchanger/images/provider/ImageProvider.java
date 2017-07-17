package me.ialistannen.wallpaperchanger.images.provider;

import java.util.List;

/**
 * Provides images.
 *
 * <em>With a <strong>huge</strong> certainty these methods will take long and block.</em>
 */
public interface ImageProvider {

  /**
   * Fetches the URL for a random image.
   *
   * @return The URL for a random image
   */
  String getRandomImageUrl();

  /**
   * Returns multiple image urls.
   *
   * @param maxLength The maximum amount of image urls
   * @return The given amount of image urls
   */
  List<String> getMultipleImageUrls(int maxLength);
}
