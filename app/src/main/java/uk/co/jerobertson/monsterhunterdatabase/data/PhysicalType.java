package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * A list of physical damage types.
 *
 * @author James Robertson
 */
public enum PhysicalType {
    CUT("Cut"),
    HIT("Hit"),
    SHOT("Shot")
    ;

    private final String text;

    PhysicalType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
