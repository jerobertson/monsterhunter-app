package uk.co.jerobertson.monsterhunterdatabase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jerobertson.monsterhunterdatabase.data.Ailment;
import uk.co.jerobertson.monsterhunterdatabase.data.Item;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;

/**
 * Generates the items to put into a recyclerview for items.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class QuickSearchContent {

    public final List<QuickSearchItem> ITEMS = new ArrayList<>();
    public final Map<String, QuickSearchItem> ITEM_MAP = new HashMap<>();

    /**
     * Gets all items, monsters, and ailments in the database and creates a recyclerview item if
     * it's found in the search.
     * @param search Results are only included if their name contains this parameter.
     */
    public QuickSearchContent(String search) {
        Monster[] monsters = App.getMonsters();
        for (Monster m : monsters) {
            if (search != null && !search.isEmpty() && (
                    m.getNAME().toLowerCase().contains(search.toLowerCase()))) {
                addItem(new QuickSearchItem(m.getICON(),
                        m.getNAME(),
                        ListType.MONSTER));
            }
        }
        Ailment[] ailments = App.getAilments();
        for (Ailment a : ailments) {
            if (search != null && !search.isEmpty() && (
                    a.getTYPE().toString().toLowerCase().contains(search.toLowerCase()))) {
                addItem(new QuickSearchItem(a.getTYPE().name(),
                        a.getTYPE().toString(),
                        ListType.AILMENT));
            }
        }
        Item[] items = App.getItems();
        for (Item i : items) {
            if (search != null && !search.isEmpty() && (
                    i.getNAME().toLowerCase().contains(search.toLowerCase()) ||
                    i.getTagString().toLowerCase().contains(search.toLowerCase()))) {
                addItem(new QuickSearchItem(i.getICON(),
                        i.getNAME(),
                        ListType.ITEM));
            }
        }
    }

    /**
     * Adds the generated items to the item list and items map.
     * @param item The item to add to the list.
     */
    private void addItem(QuickSearchItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    /**
     * A list-item of quick-search-items.
     */
    public static class QuickSearchItem implements Comparable<QuickSearchItem> {
        public final String icon;
        public final String name;
        public final ListType type;

        /**
         * Creates the quick-search-list-item. Note that icon is only really useful for the ailment,
         * as monsters and items have a helper method in the App.class to retrieve their icon using
         * the object's name.
         * @param icon The resource id to display in the recyclerview as the image.
         * @param name The name to display in the recyclerview.
         * @param type The type of item (ie monster/item/ailment/etc).
         */
        public QuickSearchItem(String icon, String name, ListType type) {
            this.icon = icon;
            this.name = name;
            this.type = type;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(@NonNull QuickSearchItem QuickSearchItem) {
            return name.compareTo(QuickSearchItem.name);
        }
    }
}
