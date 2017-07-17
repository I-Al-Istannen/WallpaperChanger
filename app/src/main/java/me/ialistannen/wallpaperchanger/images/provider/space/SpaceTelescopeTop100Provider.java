package me.ialistannen.wallpaperchanger.images.provider.space;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import me.ialistannen.wallpaperchanger.images.provider.ImageProvider;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * Queries <a href="https://www.spacetelescope.org/images/archive/top100/">Space Telescope top
 * 100</a> for images.
 */
public class SpaceTelescopeTop100Provider implements ImageProvider {

  private static final String URL = "https://www.spacetelescope.org/images/feed/top100/";
  private static final Random RANDOM = new Random();

  private List<String> cachedLinks;

  @Override
  public String getRandomImageUrl() {
    List<String> multipleImageUrls = getMultipleImageUrls(Integer.MAX_VALUE);

    if (multipleImageUrls.isEmpty()) {
      return null;
    }

    return multipleImageUrls.get(RANDOM.nextInt(multipleImageUrls.size()));
  }

  @Override
  public List<String> getMultipleImageUrls(int maxLength) {
    if (cachedLinks != null) {
      return cachedLinks;
    }
    try {
      cachedLinks = new Parser().parse();

      if (cachedLinks.size() > maxLength) {
        cachedLinks = new ArrayList<>(cachedLinks.subList(0, maxLength));
      }

      return cachedLinks;
    } catch (XmlPullParserException | IOException e) {
      return Collections.emptyList();
    }
  }

  private static class Parser {

    List<String> parse() throws XmlPullParserException, IOException {
      List<String> urls = new ArrayList<>(100);

      XmlPullParserFactory parserFactory = XmlPullParserFactory.newInstance();

      XmlPullParser pullParser = parserFactory.newPullParser();
      pullParser.setInput(new URL(URL).openConnection().getInputStream(), "UTF-8");

      while (pullParser.next() != XmlPullParser.END_DOCUMENT) {

        switch (pullParser.getEventType()) {
          case XmlPullParser.START_TAG:
            if (pullParser.getName().equals("enclosure")) {
              urls.add(pullParser.getAttributeValue(null, "url"));
            }
            break;
        }
      }

      return urls;
    }
  }
}
