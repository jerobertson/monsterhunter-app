package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * Created by James on 23/02/2018.
 */

public enum TrapType {
    SONIC("Sonic Bomb"),
    FLASH("Flash Bomb"),
    SHOCK("Shock Trap"),
    PITFALL("Pitfall Trap")
    ;

    private final String text;

    TrapType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
