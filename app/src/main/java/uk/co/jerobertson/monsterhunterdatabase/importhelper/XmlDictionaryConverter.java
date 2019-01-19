package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class used to convert the dictionary of shortnames to icons.
 *
 * Like everything else in this package, it's horrible and a hack but only used once and only kept
 * for posterity.
 *
 * @author James Robertson
 */
@SuppressWarnings("ALL")
public class XmlDictionaryConverter {

    private static final String ns = null;

    public static Map<String, String> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private static Map<String, String> readItems(XmlPullParser parser) throws XmlPullParserException, IOException {
        Map<String, String> entries = new HashMap<>();

        parser.require(XmlPullParser.START_TAG, ns, "dictionary");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("keyvalue")) {
                Entry e = readEntry(parser);
                entries.put(e.key, e.value);
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Entry {
        public final String key;
        public final String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "keyvalue");
        String key = parser.getAttributeValue(null, "key");
        String value = parser.getAttributeValue(null, "value");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
        }
        return new Entry(key, value);
    }

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
