package uk.co.jerobertson.monsterhunterdatabase.data;

import java.util.Map;

/**
 * Physical damage deserialised from a monster.
 * Physical damages have a damage amount for each type of physical damage.
 *
 * @author James Robertson
 */
public class PhysicalDamage {
    private final Map<PhysicalType, Integer> DAMAGE;

    public PhysicalDamage(Map<PhysicalType, Integer> DAMAGE) {
        this.DAMAGE = DAMAGE;
    }

    public Map<PhysicalType, Integer> getDAMAGE() {
        return DAMAGE;
    }
}
