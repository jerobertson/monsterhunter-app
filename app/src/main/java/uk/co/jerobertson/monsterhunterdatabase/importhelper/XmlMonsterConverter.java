package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;
import uk.co.jerobertson.monsterhunterdatabase.data.MonsterStatus;

/**
 * A class used to convert monsters from the old database to the new system.
 *
 * Like everything else in this package, it's horrible and a hack but only used once and only kept
 * for posterity.
 *
 * @author James Robertson
 */
@SuppressWarnings("ALL")
public class XmlMonsterConverter {

    private static final String ns = null;

    public static List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readMonsters(parser);
        } finally {
            in.close();
        }
    }

    private static List readMonsters(XmlPullParser parser) throws XmlPullParserException, IOException {
        List entries = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "monsters");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("monster")) {
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
        public final String aka;
        public final String description;
        public final String physiology;
        public final List<String> bombs;
        public final List<String> traps;
        public final String element;
        public final String bodyparts;
        public final List<AilmentType> ailments;
        public final Map<String, String> partTypeCutValues;
        public final Map<String, String> partTypeHitValues;
        public final Map<String, String> partTypeShotValues;
        public final Map<String, String> partTypeFireValues;
        public final Map<String, String> partTypeWaterValues;
        public final Map<String, String> partTypeIceValues;
        public final Map<String, String> partTypeThunderValues;
        public final Map<String, String> partTypeDragonValues;
        public final Set<MonsterStatus> statuses;

        public Entry(String name, String shortName, String aka, String description, String physiology, List<String> bombs, List<String> traps, String element, String bodyparts, List<AilmentType> ailments, Map<String, String> partTypeCutValues, Map<String, String> partTypeHitValues, Map<String, String> partTypeShotValues, Map<String, String> partTypeFireValues, Map<String, String> partTypeWaterValues, Map<String, String> partTypeIceValues, Map<String, String> partTypeThunderValues, Map<String, String> partTypeDragonValues, Set<MonsterStatus> statuses) {
            this.name = name;
            this.shortName = shortName;
            this.aka = aka;
            this.description = description;
            this.physiology = physiology;
            this.bombs = bombs;
            this.traps = traps;
            this.element = element;
            this.bodyparts = bodyparts;
            this.ailments = ailments;
            this.partTypeCutValues = partTypeCutValues;
            this.partTypeHitValues = partTypeHitValues;
            this.partTypeShotValues = partTypeShotValues;
            this.partTypeFireValues = partTypeFireValues;
            this.partTypeWaterValues = partTypeWaterValues;
            this.partTypeIceValues = partTypeIceValues;
            this.partTypeThunderValues = partTypeThunderValues;
            this.partTypeDragonValues = partTypeDragonValues;
            this.statuses = statuses;
        }
    }

    private static Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "monster");

        String name = parser.getAttributeValue(null, "name");
        String shortName = parser.getAttributeValue(null, "short");
        String aka = parser.getAttributeValue(null, "aka");
        String description = null;
        String physiology = null;
        List<String> bombs = null;
        List<String> traps = null;
        String element = null;
        String bodyparts = null;
        List<AilmentType> ailments = null;
        Map<String, String> partTypeCutValues = null;
        Map<String, String> partTypeHitValues = null;
        Map<String, String> partTypeShotValues = null;
        Map<String, String> partTypeFireValues = null;
        Map<String, String> partTypeWaterValues = null;
        Map<String, String> partTypeIceValues = null;
        Map<String, String> partTypeThunderValues = null;
        Map<String, String> partTypeDragonValues = null;
        Map<String, Map<String, Map<String, String>>> idonteven = null; //eg physical(cut:(default:50,35,35,20,30,25,30),(etc)),(etc())
        Set<MonsterStatus> statuses = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("description")) {
                description = readDescription(parser);
            } else if (id.equals("physiology")) {
                physiology = readPhysiology(parser);
            } else if (id.equals("bombs")) {
                bombs = readBombs(parser);
            } else if (id.equals("traps")) {
                traps = readTraps(parser);
            } else if (id.equals("element")) {
                element = readElement(parser);
            } else if (id.equals("bodyparts")) {
                bodyparts = readBodyparts(parser);
            } else if (id.equals("ailments")) {
                ailments = readAilments(parser);
            } else if (id.equals("weakness")) {
                idonteven = readWeakness(parser);
            } else if (id.equals("status")) {
                statuses = readStatuses(parser);
            } else {
                skip(parser);
            }
        }

        if (idonteven != null && !idonteven.isEmpty()) {
            if (idonteven.containsKey("physical")) partTypeCutValues = idonteven.get("physical").get("cut");
            if (idonteven.containsKey("physical")) partTypeHitValues = idonteven.get("physical").get("hit");
            if (idonteven.containsKey("physical")) partTypeShotValues = idonteven.get("physical").get("shot");
            if (idonteven.containsKey("elemental")) partTypeFireValues = idonteven.get("elemental").get("fire");
            if (idonteven.containsKey("elemental")) partTypeWaterValues = idonteven.get("elemental").get("water");
            if (idonteven.containsKey("elemental")) partTypeIceValues = idonteven.get("elemental").get("ice");
            if (idonteven.containsKey("elemental")) partTypeThunderValues = idonteven.get("elemental").get("thunder");
            if (idonteven.containsKey("elemental")) partTypeDragonValues = idonteven.get("elemental").get("dragon");
        }

        if (description == null) description = "";
        if (physiology == null) physiology = "";
        if (element == null) element = "";
        if (bodyparts == null) bodyparts = "";
        if (bombs == null) bombs = new ArrayList<>();
        if (traps == null) traps = new ArrayList<>();
        if (ailments == null) ailments = new ArrayList<>();

        return new Entry(name, shortName, aka, description, physiology, bombs, traps, element, bodyparts, ailments, partTypeCutValues, partTypeHitValues, partTypeShotValues, partTypeFireValues, partTypeWaterValues, partTypeIceValues, partTypeThunderValues, partTypeDragonValues, statuses);
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

    private static String readPhysiology(XmlPullParser parser) throws IOException, XmlPullParserException {
        String description = "";
        parser.require(XmlPullParser.START_TAG, ns, "physiology");
        String tag = parser.getName();
        if (tag.equals("physiology")) {
            description = parser.getAttributeValue(null, "text");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "physiology");
        return description;
    }

    // Processes link tags in the feed.
    private static List<String> readBombs(XmlPullParser parser) throws IOException, XmlPullParserException {
        String sonic = "";
        String flash = "";
        parser.require(XmlPullParser.START_TAG, ns, "bombs");
        String tag = parser.getName();
        if (tag.equals("bombs")) {
            sonic = "sonic," + parser.getAttributeValue(null, "sonic");
            flash = "flash," + parser.getAttributeValue(null, "flash");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "bombs");
        List<String> bombs = new ArrayList<>();
        bombs.add(sonic);
        bombs.add(flash);
        return bombs;
    }

    // Processes summary tags in the feed.
    private static List<String> readTraps(XmlPullParser parser) throws IOException, XmlPullParserException {
        String shock = "";
        String pitfall = "";
        parser.require(XmlPullParser.START_TAG, ns, "traps");
        String tag = parser.getName();
        if (tag.equals("traps")) {
            shock = "shock," + parser.getAttributeValue(null, "shock");
            pitfall = "pitfall," + parser.getAttributeValue(null, "pitfall");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "traps");
        List<String> bombs = new ArrayList<>();
        bombs.add(shock);
        bombs.add(pitfall);
        return bombs;
    }

    private static String readElement(XmlPullParser parser) throws IOException, XmlPullParserException {
        String element = "";
        parser.require(XmlPullParser.START_TAG, ns, "element");
        String tag = parser.getName();
        if (tag.equals("element")) {
            element = parser.getAttributeValue(null, "primary") + "," +
                    parser.getAttributeValue(null, "secondary");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "element");
        return element;
    }

    private static String readBodyparts(XmlPullParser parser) throws IOException, XmlPullParserException {
        String bodyparts = "";
        parser.require(XmlPullParser.START_TAG, ns, "bodyparts");
        String tag = parser.getName();
        if (tag.equals("bodyparts")) {
            bodyparts = parser.getAttributeValue(null, "names");
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, "bodyparts");
        return bodyparts;
    }

    private static List<AilmentType> readAilments(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ailments");

        List<AilmentType> ailments = new ArrayList<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("fire")) {
                ailments.add(readAilmentSeverity(parser, "fire", AilmentType.FIREBLIGHT, AilmentType.FIREBLIGHT_SEVERE));
            } else if (id.equals("water")) {
                ailments.add(readAilmentSeverity(parser, "water", AilmentType.WATERBLIGHT, AilmentType.WATERBLIGHT_SEVERE));
            } else if (id.equals("ice")) {
                ailments.add(readAilmentSeverity(parser, "ice", AilmentType.ICEBLIGHT, AilmentType.ICEBLIGHT_SEVERE));
            } else if (id.equals("thunder")) {
                ailments.add(readAilmentSeverity(parser, "thunder", AilmentType.THUNDERBLIGHT, AilmentType.THUNDERBLIGHT_SEVERE));
            } else if (id.equals("dragon")) {
                ailments.add(readAilmentSeverity(parser, "dragon", AilmentType.DRAGONBLIGHT, AilmentType.DRAGONBLIGHT_SEVERE));
            } else if (id.equals("poison")) {
                ailments.add(readAilmentSeverity(parser, "poison", AilmentType.POISON, AilmentType.POISON_DEADLY));
            } else if (id.equals("stun")) {
                ailments.add(readAilmentSeverity(parser, "stun", AilmentType.STUN, null));
            } else if (id.equals("paralysis")) {
                ailments.add(readAilmentSeverity(parser, "paralysis", AilmentType.PARALYSIS, null));
            } else if (id.equals("sleep")) {
                ailments.add(readAilmentSeverity(parser, "sleep", AilmentType.SLEEP, null));
            } else if (id.equals("fatigue")) {
                ailments.add(readAilmentSeverity(parser, "fatigue", AilmentType.FATIGUE, null));
            } else if (id.equals("soiled")) {
                ailments.add(readAilmentSeverity(parser, "soiled", AilmentType.SOILED, null));
            } else if (id.equals("blast")) {
                ailments.add(readAilmentSeverity(parser, "blast", AilmentType.BLASTBLIGHT, null));
            } else if (id.equals("snowman")) {
                ailments.add(readAilmentSeverity(parser, "snowman", AilmentType.SNOWMAN, null));
            } else if (id.equals("muddy")) {
                ailments.add(readAilmentSeverity(parser, "muddy", AilmentType.MUDDY, null));
            } else if (id.equals("webbed")) {
                ailments.add(readAilmentSeverity(parser, "webbed", AilmentType.WEBBED, null));
            } else if (id.equals("tarred")) {
                ailments.add(readAilmentSeverity(parser, "tarred", AilmentType.TARRED, null));
            } else if (id.equals("defence")) {
                ailments.add(readAilmentSeverity(parser, "defence", AilmentType.DEFENCE_DOWN, null));
            } else if (id.equals("bleeding")) {
                ailments.add(readAilmentSeverity(parser, "bleeding", AilmentType.BLEEDING, null));
            } else {
                skip(parser);
            }
        }

        return ailments;
    }

    private static AilmentType readAilmentSeverity(XmlPullParser parser, String search, AilmentType nonSevereType, AilmentType severeType) throws  XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, search);

        AilmentType type = null;
        String tag = parser.getName();
        if (tag.equals(search)) {
            String severe = parser.getAttributeValue(null, "severe");
            if (severe == null) {
                type = nonSevereType;
            } else {
                type = severeType;
            }
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, search);

        return type;
    }

    private static Map<String, Map<String, Map<String, String>>> readWeakness(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "weakness");

        Map<String, Map<String, Map<String, String>>> values = new HashMap<>(); //eg physical(cut:(default:50,35,35,20,30,25,30),(etc)),(etc())

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("physical")) {
                values.put("physical", readWeaknessType(parser, "physical"));
            } else if (id.equals("elemental")) {
                values.put("elemental", readWeaknessType(parser, "elemental"));
            } else {
                skip(parser);
            }
        }

        return values;
    }

    private static Map<String, Map<String, String>> readWeaknessType(XmlPullParser parser, String pOrE) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, pOrE);

        Map<String, Map<String, String>> values = new HashMap<>();// eg cut:(default:50,35,35,20,30,25,30),(etc)

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("cut")) {
                values.put("cut", readWeaknessTypeValues(parser, "cut"));
            } else if (id.equals("hit")) {
                values.put("hit", readWeaknessTypeValues(parser, "hit"));
            } else if (id.equals("shot")) {
                values.put("shot", readWeaknessTypeValues(parser, "shot"));
            } else if (id.equals("fire")) {
                values.put("fire", readWeaknessTypeValues(parser, "fire"));
            } else if (id.equals("water")) {
                values.put("water", readWeaknessTypeValues(parser, "water"));
            } else if (id.equals("ice")) {
                values.put("ice", readWeaknessTypeValues(parser, "ice"));
            } else if (id.equals("thunder")) {
                values.put("thunder", readWeaknessTypeValues(parser, "thunder"));
            } else if (id.equals("dragon")) {
                values.put("dragon", readWeaknessTypeValues(parser, "dragon"));
            } else {
                skip(parser);
            }
        }
        return values;
    }

    private static Map<String, String> readWeaknessTypeValues(XmlPullParser parser, String type) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, type);

        Map<String, String> values = new HashMap<>(); // eg default:50,35,35,20,30,25,30
        String tag = parser.getName();
        if (tag.equals(type)) {
            int attributeCount = parser.getAttributeCount();
            for (int i = 0; i < attributeCount; i++) {
                String name = parser.getAttributeName(i).replace("default", "normal");
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                values.put(name, parser.getAttributeValue(i));
            }
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, type);

        return values;
    }

    private static Set<MonsterStatus> readStatuses(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "status");

        Set<MonsterStatus> statuses = new HashSet<>();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String id = parser.getName();
            if (id.equals("poison")) {
                statuses.add(readStatus(parser, "poison", AilmentType.POISON));
            } else if (id.equals("sleep")) {
                statuses.add(readStatus(parser, "sleep", AilmentType.SLEEP));
            } else if (id.equals("paralysis")) {
                statuses.add(readStatus(parser, "paralysis", AilmentType.PARALYSIS));
            } else if (id.equals("stun")) {
                statuses.add(readStatus(parser, "stun", AilmentType.STUN));
            } else if (id.equals("fatigue")) {
                statuses.add(readStatus(parser, "fatigue", AilmentType.FATIGUE));
            } else if (id.equals("blast")) {
                statuses.add(readStatus(parser, "blast", AilmentType.BLASTBLIGHT));
            } else if (id.equals("jump")) {
                statuses.add(readStatus(parser, "jump", AilmentType.JUMP));
            } else if (id.equals("mount")) {
                statuses.add(readStatus(parser, "mount", AilmentType.MOUNT));
            } else {
                skip(parser);
            }
        }

        return statuses;
    }

    private static MonsterStatus readStatus(XmlPullParser parser, String search, AilmentType type) throws  XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, search);

        MonsterStatus status = null;

        String tag = parser.getName();
        if (tag.equals(search)) {
            String init = parser.getAttributeValue(null, "init");
            String inc = parser.getAttributeValue(null, "inc");
            String max = parser.getAttributeValue(null, "max");
            String dur = parser.getAttributeValue(null, "dur");
            String dmg = parser.getAttributeValue(null, "dmg");
            Integer initInt = (init == null) ? null : Integer.valueOf(init);
            Integer incInt = (inc == null) ? null : Integer.valueOf(inc);
            Integer maxInt = (max == null) ? null : Integer.valueOf(max);
            Integer durInt = (dur == null) ? null : Integer.valueOf(dur);
            Integer dmgInt = (dmg == null) ? null : Integer.valueOf(dmg);

            status = new MonsterStatus(type, initInt, incInt, maxInt, durInt, dmgInt);

            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, search);

        return status;
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
