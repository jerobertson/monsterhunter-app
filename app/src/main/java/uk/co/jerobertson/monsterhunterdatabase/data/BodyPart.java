package uk.co.jerobertson.monsterhunterdatabase.data;

import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Bodypart object created from the deserialisation of a monster.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class BodyPart {
    private final String NAME;
    private final Map<String, PhysicalDamage> PHYSICAL_DAMAGE;
    private final Map<String, ElementalDamage> ELEMENTAL_DAMAGE;

    public BodyPart(String NAME, Map<String, PhysicalDamage> PHYSICAL_DAMAGE, Map<String, ElementalDamage> ELEMENTAL_DAMAGE) {
        this.NAME = NAME;
        this.PHYSICAL_DAMAGE = PHYSICAL_DAMAGE;
        this.ELEMENTAL_DAMAGE = ELEMENTAL_DAMAGE;
    }

    public String getNAME() {
        return NAME;
    }

    public Map<String, PhysicalDamage> getPHYSICAL_DAMAGE() {
        return PHYSICAL_DAMAGE;
    }

    public Map<String, ElementalDamage> getELEMENTAL_DAMAGE() {
        return ELEMENTAL_DAMAGE;
    }

    /**
     * Each body part has a state that it can be in (e.g. normal, broken). This goes through all of
     * the bodyparts types and produces a list.
     * @return A set of all the part types. If 'Normal' exists, it appears first.
     */
    public LinkedHashSet<String> getPartTypes() {
        LinkedHashSet<String> partTypes = new LinkedHashSet<>();
        if (getPHYSICAL_DAMAGE() != null) {
            if (getPHYSICAL_DAMAGE().containsKey("Normal")) partTypes.add("Normal");
            for (String p : getPHYSICAL_DAMAGE().keySet()) {
                if (!p.equals("Normal")) partTypes.add(p);
            }
        }
        if (getELEMENTAL_DAMAGE() != null) {
            if (getELEMENTAL_DAMAGE().containsKey("Normal")) partTypes.add("Normal");
            for (String e : getELEMENTAL_DAMAGE().keySet()) {
                if (!e.equals("Normal")) partTypes.add(e);
            }
        }
        return partTypes;
    }

    /**
     * Get the /cut/ damage for this body part when it is /broken/
     * @param partType The part status, eg normal, broken, guarded, etc.
     * @param type The physical damage type, eg cut, hit, shot.
     * @return The result, or 0 if it doesn't exist.
     */
    public int getPhysicalPartTypeDamageTypeDamage(String partType, PhysicalType type) {
        if (!getPHYSICAL_DAMAGE().containsKey(partType)) return 0;
        if (!getPHYSICAL_DAMAGE().get(partType).getDAMAGE().containsKey(type)) return 0;
        return getPHYSICAL_DAMAGE().get(partType).getDAMAGE().get(type);
    }

    /**
     * Get the /dragon/ damage for this body part when it is /broken/
     * @param partType The part status, eg normal, broken, guarded, etc.
     * @param type The elemental damage type, eg fire, water, ice, etc.
     * @return The result, or 0 if it doesn't exist.
     */
    public int getElementalPartTypeDamageTypeDamage(String partType, ElementalType type) {
        if (!getELEMENTAL_DAMAGE().containsKey(partType)) return 0;
        if (!getELEMENTAL_DAMAGE().get(partType).getDAMAGE().containsKey(type)) return 0;
        return getELEMENTAL_DAMAGE().get(partType).getDAMAGE().get(type);
    }

    /**
     * Get the average physical damage done to this bodypart when it is /cut/.
     * @param type The physical damage type, eg cut, hit, shot.
     * @return The result, or 0 if the type does no damage.
     */
    public int getAllPhysicalPartTypesDamageTypeAverage(PhysicalType type) {
        int damageAmount = 0;
        int count = 0;
        if (getPHYSICAL_DAMAGE() == null) return 0;
        for (String partType : getPHYSICAL_DAMAGE().keySet()) {
            count++;
            damageAmount += getPhysicalPartTypeDamageTypeDamage(partType, type);
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * Get the average elemental damage done to this bodypart when it is /cut/.
     * @param type The elemental damage type, eg fire, water, ice, etc.
     * @return The result, or 0 if the type does no damage.
     */
    public int getAllElementalPartTypesDamageTypeAverage(ElementalType type) {
        int damageAmount = 0;
        int count = 0;
        if (getELEMENTAL_DAMAGE() == null) return 0;
        for (String partType : getELEMENTAL_DAMAGE().keySet()) {
            count++;
            damageAmount += getElementalPartTypeDamageTypeDamage(partType, type);
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * Get the average physical damage done to this bodypart.
     * @return The result, or 0 if this bodypart cannot be physically damaged.
     */
    public int getAllPhysicalPartTypesAverage() {
        int damageAmount = 0;
        int count = 0;
        for (PhysicalType type : PhysicalType.values()) {
            count++;
            damageAmount += getAllPhysicalPartTypesDamageTypeAverage(type);
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * Get the average elemental damage done to this bodypart.
     * Note: I see no reason why this would ever be useful, but it's included in-case it ever is.
     * @return The result, or 0 if this bodypart cannot be elementally damaged.
     */
    public int getAllElementalPartTypesAverage() {
        int damageAmount = 0;
        int count = 0;
        for (ElementalType type : ElementalType.values()) {
            count++;
            damageAmount += getAllElementalPartTypesDamageTypeAverage(type);
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

}
