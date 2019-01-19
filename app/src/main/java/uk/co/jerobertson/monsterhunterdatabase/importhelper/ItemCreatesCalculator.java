package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uk.co.jerobertson.monsterhunterdatabase.App;
import uk.co.jerobertson.monsterhunterdatabase.data.Combination;
import uk.co.jerobertson.monsterhunterdatabase.data.Item;

/**
 * The previous project didn't store the creates field in the item, so this was a helper class to
 * generate these fields for the app. Now that they're generated, it's useless, but kept for
 * posterity or in case it may become useful again in the future.
 *
 * @author James Robertson
 */
@SuppressWarnings("unused")
public class ItemCreatesCalculator {

    static {
        Gson gson = new Gson();
        Item[] items = App.getItems();
        List<Item> newItems = new ArrayList<>();
        Map<String, List<String>> itemMap = new HashMap<>(); //This item is used to created a list of these items.

        for (Item i : items) {
            for (Combination c : i.getCOMBINATIONS()) {
                if (c.getITEM_1() != null) {
                    if (!itemMap.containsKey(c.getITEM_1())) itemMap.put(c.getITEM_1(), new ArrayList<String>());
                    itemMap.get(c.getITEM_1()).add(i.getNAME());
                }
                if (c.getITEM_2() != null) {
                    if (!itemMap.containsKey(c.getITEM_2())) itemMap.put(c.getITEM_2(), new ArrayList<String>());
                    itemMap.get(c.getITEM_2()).add(i.getNAME());
                }
            }
        }

        for (Item i : items) {
            newItems.add(new Item(i.getNAME(),
                    i.getICON(),
                    i.getDESCRIPTION(),
                    i.getPRICE(),
                    i.getCOMBINATIONS(),
                    (itemMap.get(i.getNAME()) != null) ? itemMap.get(i.getNAME()) : i.getCREATES(),
                    i.getTAGS()));
        }

        for (Item i : newItems) {
            Log.d("!!!!!!!!!!!!!!!!!!!!", gson.toJson(i));
        }
    }
}
