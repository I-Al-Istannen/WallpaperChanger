package me.ialistannen.wallpaperchanger.images.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import java.util.HashMap;
import java.util.Map;
import me.ialistannen.wallpaperchanger.R;
import me.ialistannen.wallpaperchanger.images.provider.reddit.RedditImageProvider;
import me.ialistannen.wallpaperchanger.images.provider.space.SpaceTelescopeTop100Provider;

/**
 * Creates {@link ImageProvider}s based on the settings.
 */
public final class ProviderFactory {

  private static final ProviderFactory instance = new ProviderFactory();

  private Map<String, ImageProvider> providerMap = new HashMap<>();

  private ProviderFactory() {
    providerMap.put("spaceTelescopes_Top_100", new SpaceTelescopeTop100Provider());
    providerMap.put("reddit_earthporn", new RedditImageProvider("earthporn"));
    providerMap.put("reddit_naturepics", new RedditImageProvider("naturepics"));
  }

  /**
   * Creates the {@link ImageProvider} selected in the {@link SharedPreferences} of the {@link
   * Context}.
   *
   * @param context The Context to read the settings from
   * @return The created {@link ImageProvider}
   */
  public static ImageProvider getInstance(Context context) {
    Context applicationContext = context.getApplicationContext();

    SharedPreferences sharedPreferences = PreferenceManager
        .getDefaultSharedPreferences(applicationContext);

    String providerKey = context.getString(R.string.settings_source_chooser_key);

    String provider = sharedPreferences.getString(providerKey, null);

    ImageProvider imageProvider = instance.providerMap.get(provider);

    return imageProvider == null ? new SpaceTelescopeTop100Provider() : imageProvider;
  }
}
