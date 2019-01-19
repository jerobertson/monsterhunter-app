package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import uk.co.jerobertson.monsterhunterdatabase.MainActivity;
import uk.co.jerobertson.monsterhunterdatabase.data.Material;
import uk.co.jerobertson.monsterhunterdatabase.data.RankType;
import uk.co.jerobertson.monsterhunterdatabase.data.TagType;

/**
 * A class used to convert items from the old database to the new system.
 *
 * Like everything else in this package, it's horrible and a hack but only used once and only kept
 * for posterity.
 *
 * @author James Robertson
 */
@SuppressWarnings("ALL")
public class XmlMaterialConverter {

    private static final String ns = null;

    public static List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readMaterials(parser);
        } finally {
            in.close();
        }
    }

    private static List readMaterials(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "materials");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            entries.add(readEntry(parser, name));
        }
        return entries;
    }

    public static class Entry {
        public final List<Material> materials;

        public Entry(List<Material> materials) {
            this.materials = materials;
        }
    }

    private static Entry readEntry(XmlPullParser parser, String monsterName) throws XmlPullParserException, IOException {
        List<Material> materials = new ArrayList<>();
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("Low")) {
                materials.addAll(readMats(parser, "Low", monsterName, RankType.LOW));
            } else if (id.equals("High")) {
                materials.addAll(readMats(parser, "High", monsterName, RankType.HIGH));
            } else if (id.equals("G")) {
                materials.addAll(readMats(parser, "G", monsterName, RankType.G));
            } else {
                skip(parser);
            }
        }

        return new Entry(materials);
    }

    private static List<Material> readMats(XmlPullParser parser, String xmlName, String monsterName, RankType rankType) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, xmlName);

        List<Material> statuses = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("material")) {
                statuses.add(readMat(parser, "material", monsterName, rankType));
            } else {
                skip(parser);
            }
        }

        return statuses;
    }

    private static Material readMat(XmlPullParser parser, String xmlName, String monsterName, RankType rankType) throws  XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, xmlName);

        Material material = null;

        String tag = parser.getName();
        if (tag.equals(xmlName)) {
            String item = parser.getAttributeValue(null, "name");
            String obtain = parser.getAttributeValue(null, "obtainby");
            String part = parser.getAttributeValue(null, "part");
            String chance = parser.getAttributeValue(null, "chance");
            String count = parser.getAttributeValue(null, "count");

            TagType obt = null;
            for (TagType tagType : TagType.values()) {
                if (tagType.name().equals(obtain)) {
                    obt = tagType;
                }
            }

            Integer chanceInt = (chance == null) ? null : Integer.valueOf(chance);
            Integer countInt = (count == null) ? 1 : Integer.valueOf(count);

            material = new Material(monsterName, rankType, item, obt, part, chanceInt, countInt);

            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, xmlName);

        return material;
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
