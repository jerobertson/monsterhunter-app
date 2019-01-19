package uk.co.jerobertson.monsterhunterdatabase;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.TypedValue;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jerobertson.monsterhunterdatabase.data.Ailment;
import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;
import uk.co.jerobertson.monsterhunterdatabase.data.ElementalType;
import uk.co.jerobertson.monsterhunterdatabase.data.Item;
import uk.co.jerobertson.monsterhunterdatabase.data.Material;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;
import uk.co.jerobertson.monsterhunterdatabase.data.RankType;

/**
 * A helper class that provides context to the rest of the app as well as serialise the data found
 * in the assets (ie items, monsters and ailments).
 *
 * @author James Robertson
 */
public class App extends Application {

    @SuppressLint("StaticFieldLeak") //No memory leak as this class is a singleton containing the application context.
    private static Context context;

    private static Item[] items;
    private static Map<String, Item> itemMap;

    private static Monster[] monsters;
    private static Map<String, Monster> monsterMap;

    private static Ailment[] ailments;
    private static Map<AilmentType, Ailment> ailmentMap;

    private static Material[] materials;
    private static Map<String, List<Material>> materialMap;
    private static Map<String, List<Material>> materialLowMap;
    private static Map<String, List<Material>> materialHighMap;
    private static Map<String, List<Material>> materialGMap;

    /**
     * Sets the context variable to be retrieved by the rest of the app.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    /**
     * Gets the app context.
     * @return The app context.
     */
    public static Context getContext() {
        return context;
    }

    /**
     * Gets a list of all item objects serialised from the items json file. Generates this list if
     * it hasn't yet been serialised this instance.
     * @return An array of all items in the database.
     */
    public static Item[] getItems() {
        if (items == null) generateItemVariables();
        return items;
    }

    /**
     * Gets a specific item from the database, serialising the database first if it hasn't already
     * occurred.
     * @param name The name of the item to retrieve information on.
     * @return An item object for that name.
     */
    public static Item getItem(String name) {
        if (itemMap == null) generateItemVariables();
        return itemMap.get(name);
    }

    /**
     * A helper method that retrieves item images from the resources folder.
     * @param name The name of the item to get an image for.
     * @return A resource id for the item's image.
     */
    public static int getItemImage(String name) {
        if (itemMap == null) generateItemVariables();
        if (itemMap.containsKey(name)) {
            return context.getResources().getIdentifier(
                    "item_icon_" + getItem(name).getICON(),
                    "drawable",
                    App.getContext().getPackageName());
        } else {
            return getUnknownIcon();
        }
    }

    /**
     * Serialises the items.json database and generates an array of item objects.
     */
    private static void generateItemVariables() {
        Gson gson = new Gson();
        if (items == null) items = gson.fromJson(readJSONFromAsset("items.json", context), Item[].class);
        itemMap = new HashMap<>();
        for (Item i : items) {
            itemMap.put(i.getNAME(), i);
        }
    }

    /**
     * Gets a list of all monster objects serialised from the monsters json file. Generates this
     * list if it hasn't yet been serialised this instance.
     * @return An array of all monster items in the database.
     */
    public static Monster[] getMonsters() {
        if (monsters == null) generateMonsterVariables();
        return monsters;
    }

    /**
     * Gets a specific monster from the database, serialising the database first if it hasn't
     * already occurred.
     * @param name The name of the monster to retrieve information on.
     * @return A monster object for that name.
     */
    public static Monster getMonster(String name) {
        if (monsterMap == null) generateMonsterVariables();
        return monsterMap.get(name);
    }

    /**
     * A helper method that retrieves monster images from the resources folder.
     * @param name The name of the monster to get an image for.
     * @return A resource id for the monster's image.
     */
    public static int getMonsterImage(String name) {
        if (monsterMap == null) generateMonsterVariables();
        if (monsterMap.containsKey(name)) {
            return context.getResources().getIdentifier(
                    "monster_icon_" + getMonster(name).getICON(),
                    "drawable",
                    App.getContext().getPackageName());
        } else {
            return getUnknownIcon();
        }
    }

    /**
     * Serialises the monsters.json database and generates an array of item objects.
     */
    private static void generateMonsterVariables() {
        Gson gson = new Gson();
        if (monsters == null) monsters = gson.fromJson(readJSONFromAsset("monsters.json", context), Monster[].class);
        monsterMap = new HashMap<>();
        for (Monster m : monsters) {
            monsterMap.put(m.getNAME(), m);
        }
    }

    /**
     * A helper method that retrieves elemental images from the resources folder.
     * @param type The type of element to get an image for.
     * @return A resource id for the element's image.
     */
    public static int getElementalImage(ElementalType type) {
        if (type != null) {
            return context.getResources().getIdentifier(
                    "element_icon_" + type.name().toLowerCase(),
                    "drawable",
                    App.getContext().getPackageName());
        } else {
            return getUnknownIcon();
        }
    }

    /**
     * Gets a list of all ailment objects serialised from the ailments json file. Generates this
     * list if it hasn't yet been serialised this instance.
     * @return An array of all ailments in the database.
     */
    @SuppressWarnings("unused") //May be useful in future app updates.
    public static Ailment[] getAilments() {
        if (ailments == null) generateAilmentVariables();
        return ailments;
    }

    /**
     * Gets a specific ailment from the database, serialising the database first if it hasn't
     * already occurred.
     * @param type The type of ailment to retrieve
     * @return An ailment object for the type.
     */
    public static Ailment getAilment(AilmentType type) {
        if (ailments == null) generateAilmentVariables();
        return ailmentMap.get(type);
    }

    /**
     * A helper method that retrieves ailment images from the resources folder.
     * @param type The type of ailment to get an image for.
     * @return The resource id for the ailment's image.
     */
    public static int getAilmentImage(AilmentType type) {
        if (type != null) {
            return context.getResources().getIdentifier(
                    "ailment_icon_" + type.name().toLowerCase(),
                    "drawable",
                    App.getContext().getPackageName());
        } else {
            return getUnknownIcon();
        }
    }

    /**
     * Serialises the ailments.json database and generates an array of ailment objects.
     */
    private static void generateAilmentVariables() {
        Gson gson = new Gson();
        if (ailments == null) ailments = gson.fromJson(readJSONFromAsset("ailments.json", context), Ailment[].class);
        ailmentMap = new HashMap<>();
        for (Ailment a : ailments) {
            ailmentMap.put(a.getTYPE(), a);
        }
    }

    /**
     * Gets a list of all material objects serialised from the material json file. Generates this
     * list if it hasn't yet been serialised this instance.
     * @return An array of all materials in the database.
     */
    public static Material[] getMonsterMaterials() {
        if (materials == null) generateMaterialVariables();
        return materials;
    }

    /**
     * Gets all material for a specific monster from the database, serialising the database first if
     * it hasn't already occurred.
     * @param monster The monster to retrieve all the materials for.
     * @return A list of all the monster's materials.
     */
    @SuppressWarnings("unused") //Here for completeness sake, and may be useful in the future.
    public static List<Material> getMonsterMaterials(String monster) {
        if (materials == null) generateMaterialVariables();
        return materialMap.get(monster);
    }

    /**
     * Gets all material for a specific monster at a specific rank from the database, serialising
     * the database first if it hasn't already occurred.
     * @param monster The monster to retrieve all the materials for.
     * @param rank The rank the materials should be.
     * @return A list of all the monster's materials at the given rank.
     */
    public static List<Material> getMonsterMaterials(String monster, RankType rank) {
        if (materials == null) generateMaterialVariables();
        if (rank.equals(RankType.LOW)) {
            return materialLowMap.get(monster);
        } else if (rank.equals(RankType.HIGH)) {
            return materialHighMap.get(monster);
        } else {
            return materialGMap.get(monster);
        }
    }

    /**
     * Serialises the materials.json database and generates an array of material objects.
     */
    private static void generateMaterialVariables() {
        Gson gson = new Gson();
        if (materials == null) materials = gson.fromJson(readJSONFromAsset("materials.json", context), Material[].class);
        materialMap = new HashMap<>();
        materialLowMap = new HashMap<>();
        materialHighMap = new HashMap<>();
        materialGMap = new HashMap<>();
        for (Material m : materials) {
            if (!materialMap.containsKey(m.getMONSTER_NAME())) {
                materialMap.put(m.getMONSTER_NAME(), new ArrayList<Material>());
                materialLowMap.put(m.getMONSTER_NAME(), new ArrayList<Material>());
                materialHighMap.put(m.getMONSTER_NAME(), new ArrayList<Material>());
                materialGMap.put(m.getMONSTER_NAME(), new ArrayList<Material>());
            }
            materialMap.get(m.getMONSTER_NAME()).add(m);
            if (m.getRANK().equals(RankType.LOW)) {
                materialLowMap.get(m.getMONSTER_NAME()).add(m);
            } else if (m.getRANK().equals(RankType.HIGH)) {
                materialHighMap.get(m.getMONSTER_NAME()).add(m);
            } else if (m.getRANK().equals(RankType.G)) {
                materialGMap.get(m.getMONSTER_NAME()).add(m);
            }
        }
    }

    /**
     * If an image can't be found, call this method to retrieve a resource id for an 'unknown' icon.
     * @return A resource id for the 'unknown' icon.
     */
    private static int getUnknownIcon() {
        return context.getResources().getIdentifier(
                "ic_menu_unknown",
                "drawable",
                App.getContext().getPackageName());
    }

    /**
     * Helper method to read in a file from the assets folder.
     * @param filename The filename to read in.
     * @param context The application context.
     * @return A string containing the contents of the file.
     */
    private static String readJSONFromAsset(String filename, Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            //noinspection ResultOfMethodCallIgnored - don't need to know the result, it goes in the buffer.
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            return null;
        }
        return json;
    }

    /**
     * Helper method to calculate pixels from dp. This ensures all screen sizes have the same
     * layout.
     * @param dp The dp to convert to pixels.
     * @return The number of pixels that correlates to the dp.
     */
    @SuppressWarnings("SameParameterValue") //This may be different in the future, and is a really stupid warning.
    public static int getPxFromDp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /**
     * An interface for the main activity and any fragments to implement to control the app's
     * navmenu.
     */
    public interface DrawerLocker {
        void setDrawerEnabled(boolean enabled);
    }
}
