package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import android.util.Log;

import com.google.gson.Gson;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.co.jerobertson.monsterhunterdatabase.App;
import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;
import uk.co.jerobertson.monsterhunterdatabase.data.BodyPart;
import uk.co.jerobertson.monsterhunterdatabase.data.Combination;
import uk.co.jerobertson.monsterhunterdatabase.data.ElementalDamage;
import uk.co.jerobertson.monsterhunterdatabase.data.ElementalType;
import uk.co.jerobertson.monsterhunterdatabase.data.Item;
import uk.co.jerobertson.monsterhunterdatabase.data.Material;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;
import uk.co.jerobertson.monsterhunterdatabase.data.PhysicalDamage;
import uk.co.jerobertson.monsterhunterdatabase.data.PhysicalType;
import uk.co.jerobertson.monsterhunterdatabase.data.TagType;
import uk.co.jerobertson.monsterhunterdatabase.data.Trap;
import uk.co.jerobertson.monsterhunterdatabase.data.TrapType;

/**
 * A class used to convert data from an old project of mine (that used terribly formatted xml files)
 * to this project (which uses nicely formatted json).
 * Yes, it's monolithic; yes it hurts to look at; yes there are magic strings absolutely
 * everywhere... But it works! And it only has to work once! This class is never called upon
 * during the use of the app, and is only kept for posterity (or if I totally break the json files
 * somehow.
 *
 * @author James Robertson
 */
@SuppressWarnings("ALL") //This whole class is a hack...
public class XmlConverter {

    //Monster Converter
    static {
        if (false) {
            try {
                Gson gson = new Gson();
                List<XmlMonsterConverter.Entry> monsterEntries = XmlMonsterConverter.parse(App.getContext().getAssets().open("old_data/monsters.xml"));
                List<Monster> monsters = new ArrayList<>();

                for (XmlMonsterConverter.Entry e : monsterEntries) {
                    String[] elements = e.element.split(",");
                    ElementalType elementPrimary = null;
                    ElementalType elementSecondary = null;
                    if (elements.length > 0 && !elements[0].equals("null") && !elements[0].equals("")) {
                        elementPrimary = ElementalType.valueOf(elements[0].toUpperCase());
                    }
                    if (elements.length == 2 && !elements[1].equals("null") && !elements[1].equals("")) {
                        elementSecondary = ElementalType.valueOf(elements[1].toUpperCase());
                    }

                    Set<Trap> traps = new HashSet<>();
                    for (String b : e.bombs) {
                        String[] split = b.split(",");
                        if (split[0].equals("sonic") && split.length == 4) {
                            traps.add(new Trap(TrapType.SONIC, Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3])));
                        } else if (split[0].equals("flash") && split.length == 4) {
                            traps.add(new Trap(TrapType.FLASH, Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3])));
                        }
                    }
                    for (String t : e.traps) {
                        String[] split = t.split(",");
                        if (split[0].equals("shock") && split.length == 4) {
                            traps.add(new Trap(TrapType.SHOCK, Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3])));
                        } else if (split[0].equals("pitfall") && split.length == 4) {
                            traps.add(new Trap(TrapType.PITFALL, Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer.valueOf(split[3])));
                        }
                    }

                    List<Map<String, Map<PhysicalType, Integer>>> physicalParts = new ArrayList<>(); //{partstate, {cut/hit/shot, damagevalue}}
                    for (int i = 0; i < e.bodyparts.split(",").length; i++) {
                        physicalParts.add(new HashMap<String, Map<PhysicalType, Integer>>());
                        if (e.partTypeCutValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeCutValues.entrySet()) { //map of partstates to damage values
                                if (!physicalParts.get(i).containsKey(damageSet.getKey()))
                                    physicalParts.get(i).put(damageSet.getKey(), new HashMap<PhysicalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    physicalParts.get(i).get(damageSet.getKey()).put(PhysicalType.CUT, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                        if (e.partTypeHitValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeHitValues.entrySet()) { //map of partstates to damage values
                                if (!physicalParts.get(i).containsKey(damageSet.getKey()))
                                    physicalParts.get(i).put(damageSet.getKey(), new HashMap<PhysicalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    physicalParts.get(i).get(damageSet.getKey()).put(PhysicalType.HIT, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                        if (e.partTypeShotValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeShotValues.entrySet()) { //map of partstates to damage values
                                if (!physicalParts.get(i).containsKey(damageSet.getKey()))
                                    physicalParts.get(i).put(damageSet.getKey(), new HashMap<PhysicalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    physicalParts.get(i).get(damageSet.getKey()).put(PhysicalType.SHOT, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                    }
                    List<Map<String, PhysicalDamage>> physicalDamageParts = new ArrayList<>();
                    for (Map<String, Map<PhysicalType, Integer>> physicalPart : physicalParts) {
                        physicalDamageParts.add(new HashMap<String, PhysicalDamage>());
                        for (Map.Entry<String, Map<PhysicalType, Integer>> part : physicalPart.entrySet()) {
                            physicalDamageParts.get(physicalDamageParts.size() - 1).put(part.getKey(), new PhysicalDamage(part.getValue()));
                        }
                    }
                    List<Map<String, Map<ElementalType, Integer>>> elementalParts = new ArrayList<>(); //{partstate, {cut/hit/shot, damagevalue}}
                    for (int i = 0; i < e.bodyparts.split(",").length; i++) {
                        elementalParts.add(new HashMap<String, Map<ElementalType, Integer>>());
                        if (e.partTypeFireValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeFireValues.entrySet()) { //map of partstates to damage values
                                if (!elementalParts.get(i).containsKey(damageSet.getKey()))
                                    elementalParts.get(i).put(damageSet.getKey(), new HashMap<ElementalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    elementalParts.get(i).get(damageSet.getKey()).put(ElementalType.FIRE, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                        if (e.partTypeWaterValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeWaterValues.entrySet()) { //map of partstates to damage values
                                if (!elementalParts.get(i).containsKey(damageSet.getKey()))
                                    elementalParts.get(i).put(damageSet.getKey(), new HashMap<ElementalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    elementalParts.get(i).get(damageSet.getKey()).put(ElementalType.WATER, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                        if (e.partTypeIceValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeIceValues.entrySet()) { //map of partstates to damage values
                                if (!elementalParts.get(i).containsKey(damageSet.getKey()))
                                    elementalParts.get(i).put(damageSet.getKey(), new HashMap<ElementalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    elementalParts.get(i).get(damageSet.getKey()).put(ElementalType.ICE, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                        if (e.partTypeThunderValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeThunderValues.entrySet()) { //map of partstates to damage values
                                if (!elementalParts.get(i).containsKey(damageSet.getKey()))
                                    elementalParts.get(i).put(damageSet.getKey(), new HashMap<ElementalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    elementalParts.get(i).get(damageSet.getKey()).put(ElementalType.THUNDER, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                        if (e.partTypeDragonValues != null) {
                            for (Map.Entry<String, String> damageSet : e.partTypeDragonValues.entrySet()) { //map of partstates to damage values
                                if (!elementalParts.get(i).containsKey(damageSet.getKey()))
                                    elementalParts.get(i).put(damageSet.getKey(), new HashMap<ElementalType, Integer>());
                                if (damageSet.getValue().split(",").length > i) {
                                    elementalParts.get(i).get(damageSet.getKey()).put(ElementalType.DRAGON, Integer.valueOf(damageSet.getValue().split(",")[i]));
                                }
                            }
                        }
                    }
                    List<Map<String, ElementalDamage>> elementalDamageParts = new ArrayList<>();
                    for (Map<String, Map<ElementalType, Integer>> elementalPart : elementalParts) {
                        elementalDamageParts.add(new HashMap<String, ElementalDamage>());
                        for (Map.Entry<String, Map<ElementalType, Integer>> part : elementalPart.entrySet()) {
                            elementalDamageParts.get(elementalDamageParts.size() - 1).put(part.getKey(), new ElementalDamage(part.getValue()));
                        }
                    }

                    Set<BodyPart> bodyParts = new HashSet<>();
                    for (int i = 0; i < e.bodyparts.split(",").length; i++) {
                        String name = e.bodyparts.split(",")[i];
                        Map<String, PhysicalDamage> physicalDamage = (physicalDamageParts.isEmpty()) ? null : physicalDamageParts.get(i);
                        Map<String, ElementalDamage> elementalDamage = (elementalDamageParts.isEmpty()) ? null : elementalDamageParts.get(i);
                        BodyPart bodyPart = new BodyPart(name, physicalDamage, elementalDamage);
                        bodyParts.add(bodyPart);
                    }

                    Set<AilmentType> ailments = null;
                    if (!e.ailments.isEmpty()) {
                        ailments = new HashSet<>();
                        for (AilmentType type : e.ailments) {
                            ailments.add(type);
                        }
                    }

                    Monster monster = new Monster(e.name,
                            e.shortName,
                            e.name.toLowerCase().replace(" ", "_").replace("'", ""),
                            e.aka,
                            e.description,
                            e.physiology,
                            elementPrimary,
                            elementSecondary,
                            bodyParts, //bodyPart damages need adding.
                            ailments,
                            e.statuses,
                            traps);
                    monsters.add(monster);

                    String out = gson.toJson(monster).replace("Cut", "CUT").replace("Hit", "HIT").replace("Shot", "SHOT");
                    for (String part : splitEqually(out, 500)) {
                        Log.d("!!!!!", part);
                    }
                }

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.d("!!!!!!!!!!!!!!!!!!!!", sw.toString());
            }
        }
    }

    //Item Converter
    static {
        if (false) {
            try {
                Gson gson = new Gson();
                List<XmlItemConverter.Entry> itemEntries = XmlItemConverter.parse(App.getContext().getAssets().open("old_data/books.xml"));
                Map<String, String> dictionaryEntries = XmlDictionaryConverter.parse(App.getContext().getAssets().open("old_data/dictionary.xml"));

                Map<String, String> shortNames = new HashMap<>();
                for (XmlItemConverter.Entry e : itemEntries) {
                    shortNames.put(e.shortName, e.name);
                }

                for (XmlItemConverter.Entry e : itemEntries) {
                    List<Combination> combos = new ArrayList<>();
                    if (!e.combine1.equals("")) {
                        if (e.combineCount1.equals("")) {
                            Combination combo = new Combination(shortNames.get(e.combine1), shortNames.get(e.combine2), 1, 1);
                            combos.add(combo);
                        } else {
                            String[] counts = e.combineCount1.split("-");
                            if (counts.length == 1) {
                                Combination combo = new Combination(shortNames.get(e.combine1), shortNames.get(e.combine2), Integer.valueOf(counts[0]), Integer.valueOf(counts[0]));
                                combos.add(combo);
                            } else {
                                Combination combo = new Combination(shortNames.get(e.combine1), shortNames.get(e.combine2), Integer.valueOf(counts[0]), Integer.valueOf(counts[1]));
                                combos.add(combo);
                            }
                        }
                    }
                    if (!e.combine3.equals("")) {
                        if (e.combineCount2.equals("")) {
                            Combination combo = new Combination(shortNames.get(e.combine3), shortNames.get(e.combine4), 1, 1);
                            combos.add(combo);
                        } else {
                            String[] counts = e.combineCount2.split("-");
                            if (counts.length == 1) {
                                Combination combo = new Combination(shortNames.get(e.combine3), shortNames.get(e.combine4), Integer.valueOf(counts[0]), Integer.valueOf(counts[0]));
                                combos.add(combo);
                            } else {
                                Combination combo = new Combination(shortNames.get(e.combine3), shortNames.get(e.combine4), Integer.valueOf(counts[0]), Integer.valueOf(counts[1]));
                                combos.add(combo);
                            }
                        }
                    }

                    List<TagType> tags = new ArrayList<>();
                    for (String tag : e.tags.split(",")) {
                        try {
                            tags.add(TagType.valueOf(tag.toUpperCase()));
                        } catch (Exception ex) {
                        }
                    }

                    Integer price = null;
                    if (!e.price.equals("")) {
                        price = Integer.valueOf(e.price);
                    }

                    Item i = new Item(e.name, dictionaryEntries.get(e.shortName), e.description, price, combos, new ArrayList<String>(), tags);
                    String out = gson.toJson(i);
                    for (String part : splitEqually(out, 500)) {
                        Log.d("!!!!!", part);
                    }
                }
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.d("!!!!!!!!!!!!!!!!!!!!", sw.toString());
            }
        }
    }

    //Material Converter
    static {
        if (true) {
            try {
                Gson gson = new Gson();
                List<XmlItemConverter.Entry> itemEntries = XmlItemConverter.parse(App.getContext().getAssets().open("old_data/items.xml"));
                List<XmlMonsterConverter.Entry> monsterEntries = XmlMonsterConverter.parse(App.getContext().getAssets().open("old_data/monsters.xml"));
                List<XmlMaterialConverter.Entry> materialEntries = XmlMaterialConverter.parse(App.getContext().getAssets().open("old_data/materials.xml"));
                List<Material> newMats = new ArrayList<>();

                Map<String, String> shortNames = new HashMap<>();
                for (XmlItemConverter.Entry e : itemEntries) {
                    shortNames.put(e.shortName, e.name);
                }
                for (XmlMonsterConverter.Entry e : monsterEntries) {
                    shortNames.put(e.shortName, e.name);
                }

                for (XmlMaterialConverter.Entry e : materialEntries) {
                    for (Material m : e.materials) {
                        newMats.add(new Material(shortNames.get(m.getMONSTER_NAME()), m.getRANK(), shortNames.get(m.getITEM_NAME()), m.getOBTAIN_BY(), m.getBODY_PART(), m.getOBTAIN_CHANCE(), m.getCOUNT()));
                    }
                }

                for (Material m : newMats) {
                    Log.d("!!!!!", gson.toJson(m));
                }

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Log.d("!!!!!!!!!!!!!!!!!!!!", sw.toString());
            }
        }
    }

    private static List<String> splitEqually(String text, int size) {
        List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
        ret.add("+++");
        for (int start = 0; start < text.length(); start += size) {
            ret.add(text.substring(start, Math.min(text.length(), start + size)));
        }
        return ret;
    }
}
