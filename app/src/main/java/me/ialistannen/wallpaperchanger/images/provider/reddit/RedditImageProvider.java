package me.ialistannen.wallpaperchanger.images.provider.reddit;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import me.ialistannen.wallpaperchanger.images.provider.ImageProvider;

/**
 * An {@link ImageProvider} that scrapes pictures from reddit.
 */
public class RedditImageProvider implements ImageProvider {

  private static final String TAG = "RedditImageProvider";

  private final Random RANDOM = new Random();
  private Gson gson;

  private String subredditName;

  private List<String> imageUrls;

  /**
   * @param subredditName The name of the subreddit.
   */
  public RedditImageProvider(String subredditName) {
    this.subredditName = subredditName;
  }

  @Override
  public String getRandomImageUrl() {
    List<String> urls = getMultipleImageUrls(Integer.MAX_VALUE);

    if (urls.isEmpty()) {
      return null;
    }

    return urls.get(RANDOM.nextInt(urls.size()));
  }

  @Override
  public List<String> getMultipleImageUrls(int maxLength) {
    if (imageUrls == null) {
      imageUrls = getImageUrls();
    }
    if (maxLength >= imageUrls.size()) {
      return imageUrls;
    }

    return new ArrayList<>(imageUrls.subList(0, maxLength));
  }

  private List<String> getImageUrls() {
    JsonObject page = readPageAsJson(getSubredditUrl());

    if (page == null || !page.has("data")) {
      return Collections.emptyList();
    }

    JsonObject data = page.getAsJsonObject("data");

    if (!data.has("children")) {
      return Collections.emptyList();
    }

    JsonArray children = data.getAsJsonArray("children");

    List<String> urlList = new ArrayList<>(children.size());

    for (JsonElement child : children) {
      if (!child.isJsonObject()) {
        continue;
      }
      JsonObject childObject = child.getAsJsonObject();
      JsonObject childData = childObject.getAsJsonObject("data");

      if (!childData.has("url")) {
        continue;
      }

      String url = childData.getAsJsonPrimitive("url").getAsString();
      if (isImageUrl(url)) {
        urlList.add(url);
      }
    }

    return urlList;
  }

  private JsonObject readPageAsJson(String url) {
    if (gson == null) {
      gson = new Gson();
    }

    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.addRequestProperty("User-Agent", "Mozilla/5.0");

      try (InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
          BufferedReader reader = new BufferedReader(inputStreamReader)) {

        return gson.fromJson(reader, JsonObject.class);
      }

    } catch (IOException e) {
      Log.w(TAG, "readPageAsJson: Error reading from webpage", e);
    }
    return null;
  }


  private String getSubredditUrl() {
    return String.format("https://www.reddit.com/r/%s.json", subredditName);
  }

  private boolean isImageUrl(String url) {
    return url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".jpeg");
  }
}
