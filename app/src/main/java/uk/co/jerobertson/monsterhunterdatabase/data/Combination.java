package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * A combination created from the deserialisation of an item.
 * A combination requires two items, and produces at least min items and at most max.
 */
public class Combination {
    private final String ITEM_1;
    private final String ITEM_2;
    private final Integer MIN;
    private final Integer MAX;

    public Combination(String ITEM_1, String ITEM_2, Integer MIN, Integer MAX) {
        this.ITEM_1 = ITEM_1;
        this.ITEM_2 = ITEM_2;
        this.MIN = MIN;
        this.MAX = MAX;
    }

    public String getITEM_1() {
        return ITEM_1;
    }

    public String getITEM_2() {
        return ITEM_2;
    }

    @SuppressWarnings("unused") //Placeholder for future updates.
    public Integer getMIN() {
        return MIN;
    }

    @SuppressWarnings("unused") //Placeholder for future updates.
    public Integer getMAX() {
        return MAX;
    }

    @SuppressWarnings("unused") //Placeholder for future updates.
    public String getAmount() {
        if (MIN.equals(MAX)) {
            return MIN.toString();
        } else {
            return MIN.toString() + "-" + MAX.toString();
        }
    }
}
