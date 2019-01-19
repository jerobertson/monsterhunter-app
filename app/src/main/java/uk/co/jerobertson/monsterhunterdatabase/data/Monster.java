package uk.co.jerobertson.monsterhunterdatabase.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The deserialisation of a monster from the json file.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class Monster {
    private final String NAME;
    private final String SORT_BY;
    private final String ICON;
    private final String AKA;
    private final String DESCRIPTION;
    private final String PHYSIOLOGY;
    private final ElementalType PRIMARY_ATTACK;
    private final ElementalType SECONDARY_ATTACK;
    private final Set<BodyPart> BODY_PARTS; //A monster has multiple body parts, each with different state, each state with a different damage value.
    private final Set<AilmentType> AILMENTS; //A monster can cause a set of ailments to a player.
    private final Set<MonsterStatus> STATUSES; //A monster can be given a set of status effects if a player meets certain requirements defined in this set.
    private final Set<Trap> TRAPS; //A monster can be vulnerable to different types of traps.

    public Monster(String NAME, String SORT_BY, String ICON, String AKA, String DESCRIPTION, String PHYSIOLOGY, ElementalType PRIMARY_ATTACK, ElementalType SECONDARY_ATTACK, Set<BodyPart> BODY_PARTS, Set<AilmentType> AILMENTS, Set<MonsterStatus> STATUSES, Set<Trap> TRAPS) {
        this.NAME = NAME;
        this.SORT_BY = SORT_BY;
        this.ICON = ICON;
        this.AKA = AKA;
        this.DESCRIPTION = DESCRIPTION;
        this.PHYSIOLOGY = PHYSIOLOGY;
        this.PRIMARY_ATTACK = PRIMARY_ATTACK;
        this.SECONDARY_ATTACK = SECONDARY_ATTACK;
        this.BODY_PARTS = BODY_PARTS;
        this.AILMENTS = AILMENTS;
        this.STATUSES = STATUSES;
        this.TRAPS = TRAPS;
    }

    public String getNAME() {
        return NAME;
    }

    public String getSORT_BY() {
        return SORT_BY;
    }

    public String getICON() {
        return ICON;
    }

    public String getAKA() {
        return AKA;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public String getPHYSIOLOGY() {
        return PHYSIOLOGY;
    }

    public ElementalType getPRIMARY_ATTACK() {
        return PRIMARY_ATTACK;
    }

    public ElementalType getSECONDARY_ATTACK() {
        return SECONDARY_ATTACK;
    }

    public Set<BodyPart> getBODY_PARTS() {
        return BODY_PARTS;
    }

    /**
     * Each body part has a state (such as normal, broken, etc). This is used to retrieve a set of
     * all states to be display in the monster details to highlight the monster's weakness.
     * Because of the implementation of BodyPart.getBODY_PARTS if 'Normal' exists, this is returned
     * first, hence the need for a linked hash set.
     * @return A list of all bodypart states.
     */
    public LinkedHashSet<String> getBodyPartTypes() {
        LinkedHashSet<String> partTypes = new LinkedHashSet<>();
        for (BodyPart bodyPart : getBODY_PARTS()) {
            partTypes.addAll(bodyPart.getPartTypes());
        }
        return partTypes;
    }

    /**
     * Each body part has a name (such as head, body, tail, etc). This is used to retrieve the names
     * of all body parts this monster has.
     * @return
     */
    @SuppressWarnings("unused") //May be useful in future app updates.
    public Set<String> getBodyPartNames() {
        Set<String> partNames = new HashSet<>();
        for (BodyPart bodyPart : getBODY_PARTS()) {
            partNames.add(bodyPart.getNAME());
        }
        return partNames;
    }

    /**
     * Each body part has different toughness. This method works out the average toughness of each
     * part so that a player knows how much average damage they can do to the monster. This is
     * useful for determining how important elemental damage is and whether to take a weapon that
     * inflicts elemental damage or status effects.
     * @param type The physical type of damage being done (ie cut, hit, shot).
     * @return The average damage done for that physical type.
     */
    public int getAveragePhysicalDamage(PhysicalType type) {
        int damageAmount = 0;
        int count = 0;
        for (BodyPart bodyPart : getBODY_PARTS()) {
            count++;
            damageAmount += bodyPart.getAllPhysicalPartTypesDamageTypeAverage(type);
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * This is an average for all physical damage types.
     * @return The average physical damage done to the monster.
     */
    public int getAveragePhysicalDamage() {
        int damageAmount = 0;
        int count = 0;
        for (BodyPart bodyPart : getBODY_PARTS()) {
            count++;
            damageAmount += bodyPart.getAllPhysicalPartTypesAverage();
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * Each body part has different elemental weakness. This method works out the average weakness
     * for a specific element type so that a player knows how much average elemental damage they can
     * do to the monster. This is useful when compared to the physical damage to see how important
     * elemental damage is, but also to see which element is the best to use against the monster.
     * @param type The type of element to calculate the average for.
     * @return The average damage done to all body parts and their states for this element.
     */
    public int getAverageElementalDamage(ElementalType type) {
        int damageAmount = 0;
        int count = 0;
        for (BodyPart bodyPart : getBODY_PARTS()) {
            count++;
            damageAmount += bodyPart.getAllElementalPartTypesDamageTypeAverage(type);
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * This is an average for all elemental damage.
     * @return The average elemental damage done to the monster.
     */
    @SuppressWarnings("unused") //I have no idea why this method would ever be useful, but it's here for completion sake just in case it is in the future.
    public int getAverageElementalDamage() {
        int damageAmount = 0;
        int count = 0;
        for (BodyPart bodyPart : getBODY_PARTS()) {
            count++;
            damageAmount += bodyPart.getAllElementalPartTypesAverage();
        }
        return (count == 0) ? 0 : (int)Math.round(((double)damageAmount) / count);
    }

    /**
     * Gets the average damage done to the monster's body parts in order of most damage done to
     * least damage done. As multiple parts can have the same damage value, this returns a map that
     * contains the damage values done to a list of bodypart names.
     * @param partType The state of the part (eg normal/broken/etc) to search for.
     * @param type The physical damage type (ie cut/hit/shot).
     * @return A map of damage values to a list of body parts with that value.
     */
    public SortedMap<Integer, List<String>> getWeakestBodyParts(String partType, PhysicalType type) {
        SortedMap<Integer, List<String>> bodyPartMap = new TreeMap<>(Collections.reverseOrder());
        for (BodyPart bodyPart : getBODY_PARTS()) {
            int average = 0;
            if (bodyPart.getPartTypes().contains(partType)) average = bodyPart.getPhysicalPartTypeDamageTypeDamage(partType, type);
            if (!bodyPartMap.containsKey(average)) bodyPartMap.put(average, new ArrayList<String>());
            bodyPartMap.get(average).add(bodyPart.getNAME());
        }
        return bodyPartMap;
    }

    /**
     * This is a generalisation of the overloaded method. It does it by body part rather than body
     * part states. This may be useful in the future, but for now it's just here for completion
     * sake.
     * @return A map of damage values to a list of body parts with that value.
     */
    public SortedMap<Integer, List<String>> getWeakestBodyParts() {
        SortedMap<Integer, List<String>> bodyPartMap = new TreeMap<>(Collections.reverseOrder());
        for (BodyPart bodyPart : getBODY_PARTS()) {
            int average = bodyPart.getAllPhysicalPartTypesAverage();
            if (!bodyPartMap.containsKey(average)) bodyPartMap.put(average, new ArrayList<String>());
            bodyPartMap.get(average).add(bodyPart.getNAME());
        }
        return bodyPartMap;
    }

    public Set<AilmentType> getAILMENTS() {
        return AILMENTS;
    }

    public Set<MonsterStatus> getSTATUSES() {
        return STATUSES;
    }

    public Set<Trap> getTRAPS() {
        return TRAPS;
    }
}
