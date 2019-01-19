package uk.co.jerobertson.monsterhunterdatabase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jerobertson.monsterhunterdatabase.data.Monster;

/**
 * Generates the items to put into a recyclerview for monsters.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class MonsterContent {

    public final List<MonsterItem> ITEMS = new ArrayList<>();
    public final Map<String, MonsterItem> ITEM_MAP = new HashMap<>();

    /**
     * Gets all items in the database and creates a recyclerview item if it's found in the search.
     * @param search Results are only included if their name contains this parameter.
     */
    public MonsterContent(String search) {
        Monster[] monsters = App.getMonsters();
        for (Monster m : monsters) {
            if (search == null ||
                    m.getNAME().toLowerCase().contains(search.toLowerCase())) {
                addItem(new MonsterItem(m.getICON(), m.getSORT_BY(), m.getNAME(), m.getAKA()));
            }
        }
    }

    /**
     * Adds the generated monster items to the items list and item map.
     * @param item The item to add to the list.
     */
    private void addItem(MonsterItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.sort_by, item);
    }

    /**
     * A list-item of monsters.
     */
    public static class MonsterItem implements Comparable<MonsterItem> {
        public final String icon;
        public final String sort_by;
        public final String name;
        public final String aka;

        /**
         * Creates the monster-item item.
         * @param icon The resource id to display in the recyclerview as the image.
         * @param sort_by The string to sort the monster by in the list (a monster can be a subspecies of another).
         * @param name The name to display in the recyclerview.
         * @param aka The short description/aka of the monster to display in the recyclerview.
         */
        public MonsterItem(String icon, String sort_by, String name, String aka) {
            this.icon = icon;
            this.sort_by = sort_by;
            this.name = name;
            this.aka = aka;
        }

        @Override
        public String toString() {
            return sort_by;
        }

        @Override
        public int compareTo(@NonNull MonsterItem monsterItem) {
            return sort_by.compareTo(monsterItem.sort_by);
        }
    }
}
