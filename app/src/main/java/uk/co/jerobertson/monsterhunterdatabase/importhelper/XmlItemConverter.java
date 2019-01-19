package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A class used to convert items from the old database to the new system.
 *
 * Like everything else in this package, it's horrible and a hack but only used once and only kept
 * for posterity.
 *
 * @author James Robertson
 */
@SuppressWarnings("ALL")
public class XmlItemConverter {

    private static final String ns = null;

    public static List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readItems(parser);
        } finally {
            in.close();
        }
    }

    private static List readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "items");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final String name;
        public final String shortName;
        public final String tags;
        public final String description;
        public final String combine1;
        public final String combine2;
        public final String combine3;
        public final String combine4;
        public final String combineCount1;
        public final String combineCount2;
        public final String price;

        public Entry(String name, String shortName, String tags, String description, String combine1, String combine2, String combine3, String combine4, String combineCount1, String combineCount2, String price) {
            this.name = name;
            this.shortName = shortName;
            this.tags = tags;
            this.description = description;
            this.combine1 = combine1;
            this.combine2 = combine2;
            this.combine3 = combine3;
            this.combine4 = combine4;
            this.combineCount1 = combineCount1;
            this.combineCount2 = combineCount2;
            this.price = price;
        }
    }

    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String name = parser.getAttributeValue(null, "name");
        String shortName = parser.getAttributeValue(null, "short");
        String tags = parser.getAttributeValue(null, "tags");
        String description = null;
        List<String> combines = null;
        List<String> combineAmounts = null;
        String price = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("description")) {
                description = readDescription(parser);
            } else if (id.equals("combine")) {
                combines = readCombines(parser);
            } else if (id.equals("combineamount")) {
                combineAmounts = readCombineAmount(parser);
            } else if (id.equals("price")) {
                price = readPrice(parser);
            } else {
                skip(parser);
            }
        }

        if (description == null) description = "";
        if (combines == null) {
            combines = new ArrayList<>();
            combines.add("");
            combines.add("");
            combines.add("");
            combines.add("");
        }
        if (combineAmounts == null) {
            combineAmounts = new ArrayList<>();
            combineAmounts.add("");
            combineAmounts.add("");
        }
        if (price == null) price = "";
        if (tags == null) tags = "";

        return new Entry(name, shortName, tags, description, combines.get(0), combines.get(1), combines.get(2), combines.get(3), combineAmounts.get(0), combineAmounts.get(1), price);
    }

    // Processes title tags in the feed.
    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        String description = "";
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String tag = parser.getName();
        if (tag.equals("description")) {
            description = parser.getAttributeValue(null, "text");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    // Processes link tags in the feed.
    private static String readPrice(XmlPullParser parser) throws IOException, XmlPullParserException {
        String price = "";
        parser.require(XmlPullParser.START_TAG, ns, "price");
        String tag = parser.getName();
        if (tag.equals("price")) {
            price = parser.getAttributeValue(null, "sell");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "price");
        return price;
    }

    // Processes summary tags in the feed.
    private static List<String> readCombines(XmlPullParser parser) throws IOException, XmlPullParserException {
        String combo1 = "";
        String combo2 = "";
        String combo3 = "";
        String combo4 = "";
        parser.require(XmlPullParser.START_TAG, ns, "combine");
        String tag = parser.getName();
        if (tag.equals("combine")) {
            combo1 = parser.getAttributeValue(null, "first");
            combo2 = parser.getAttributeValue(null, "second");
            combo3 = parser.getAttributeValue(null, "altfirst");
            combo4 = parser.getAttributeValue(null, "altsecond");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "combine");
        List<String> combos = new ArrayList<>();
        combos.add(combo1 + "");
        combos.add(combo2 + "");
        combos.add(combo3 + "");
        combos.add(combo4 + "");
        return combos;
    }

    private static List<String> readCombineAmount(XmlPullParser parser) throws IOException, XmlPullParserException {
        String primary = null;
        String secondary = null;
        parser.require(XmlPullParser.START_TAG, ns, "combineamount");
        String tag = parser.getName();
        if (tag.equals("combineamount")) {
            primary = parser.getAttributeValue(null, "primary");
            secondary = parser.getAttributeValue(null, "alt");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "combineamount");
        List<String> amounts = new ArrayList<>();
        amounts.add((primary == null) ? "" : primary);
        amounts.add((secondary == null) ? "" : secondary);
        return amounts;
    }

    // For the tags title and summary, extracts their text values.
    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
