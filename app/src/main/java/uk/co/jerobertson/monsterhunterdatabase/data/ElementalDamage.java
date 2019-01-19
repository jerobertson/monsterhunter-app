package uk.co.jerobertson.monsterhunterdatabase.data;

import java.util.Map;

/**
 * Elemental damage deserialised from a monster.
 * Elemental damages have a damage amount for each type of element.
 *
 * @author James Robertson
 */
public class ElementalDamage {
    private final Map<ElementalType, Integer> DAMAGE;

    public ElementalDamage(Map<ElementalType, Integer> DAMAGE) {
        this.DAMAGE = DAMAGE;
    }

    @SuppressWarnings("WeakerAccess")
    public Map<ElementalType, Integer> getDAMAGE() {
        return DAMAGE;
    }
}
