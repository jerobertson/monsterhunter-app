package uk.co.jerobertson.monsterhunterdatabase;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jerobertson.monsterhunterdatabase.data.Item;

/**
 * Generates the items to put into a recyclerview for items.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class ItemContent {

    public final List<ItemItem> ITEMS = new ArrayList<>();
    public final Map<String, ItemItem> ITEM_MAP = new HashMap<>();

    /**
     * Gets all items in the database and creates a recyclerview item if it's found in the search.
     * @param search Results are only included if their name contains this parameter.
     */
    public ItemContent(String search) {
        Item[] items = App.getItems();
        for (Item i : items) {
            String tags = i.getTagString();

            String price = (i.getPRICE() == null) ? "?z" : i.getPRICE() + "z";

            if (search == null ||
                    i.getNAME().toLowerCase().contains(search.toLowerCase()) ||
                    i.getTagString().toLowerCase().contains(search.toLowerCase())) {
                addItem(new ItemItem(i.getICON(), i.getNAME(), price, tags, i.getDESCRIPTION()));
            }
        }
    }

    /**
     * Adds the generated items to the item list and items map.
     * @param item The item to add to the list.
     */
    private void addItem(ItemItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    /**
     * A list-item of items. I swear the name makes sense.
     */
    @SuppressWarnings("WeakerAccess")
    public static class ItemItem implements Comparable<ItemItem> {
        public final String icon;
        public final String name;
        public final String price;
        public final String tagList;
        public final String description;

        /**
         * Create the list-item item item item item item item thing item.
         * @param icon The resource id to display in the recyclerview as the image.
         * @param name The name to display in the recyclerview.
         * @param price The price to display in the recyclerview.
         * @param tagList A simple string to show as the tags for the item in the recyclerview.
         * @param description The description to display in the recyclerview.
         */
        public ItemItem(String icon, String name, String price, String tagList, String description) {
            this.icon = icon;
            this.name = name;
            this.price = price;
            this.tagList = tagList;
            this.description = description;
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public int compareTo(@NonNull ItemItem itemItem) {
            return name.compareTo(itemItem.name);
        }
    }
}
