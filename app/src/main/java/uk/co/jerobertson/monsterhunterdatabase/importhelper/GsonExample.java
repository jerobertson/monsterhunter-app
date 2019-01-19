package uk.co.jerobertson.monsterhunterdatabase.importhelper;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import uk.co.jerobertson.monsterhunterdatabase.data.Ailment;
import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;
import uk.co.jerobertson.monsterhunterdatabase.data.BodyPart;
import uk.co.jerobertson.monsterhunterdatabase.data.ElementalDamage;
import uk.co.jerobertson.monsterhunterdatabase.data.ElementalType;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;
import uk.co.jerobertson.monsterhunterdatabase.data.MonsterStatus;
import uk.co.jerobertson.monsterhunterdatabase.data.PhysicalDamage;
import uk.co.jerobertson.monsterhunterdatabase.data.PhysicalType;
import uk.co.jerobertson.monsterhunterdatabase.data.Trap;
import uk.co.jerobertson.monsterhunterdatabase.data.TrapType;

/**
 * An example class built to check that the json files were being created correctly. It's otherwise
 * unused but kept for reference in later updates.
 *
 * @author James Robertson
 */
@SuppressWarnings("unused")
public class GsonExample {

    private Monster monster;
    private Ailment ailment;

    public GsonExample() {
        Map<PhysicalType, Integer> headPhysicalNormalDamageCount = new HashMap<>();
        headPhysicalNormalDamageCount.put(PhysicalType.CUT, 50);
        headPhysicalNormalDamageCount.put(PhysicalType.HIT, 50);
        headPhysicalNormalDamageCount.put(PhysicalType.SHOT, 60);
        PhysicalDamage headPhysicalNormalDamage = new PhysicalDamage(headPhysicalNormalDamageCount);
        Map<PhysicalType, Integer> headPhysicalBrokenDamageCount = new HashMap<>();
        headPhysicalBrokenDamageCount.put(PhysicalType.CUT, 50);
        headPhysicalBrokenDamageCount.put(PhysicalType.HIT, 50);
        headPhysicalBrokenDamageCount.put(PhysicalType.SHOT, 60);
        PhysicalDamage headPhysicalBrokenDamage = new PhysicalDamage(headPhysicalBrokenDamageCount);
        Map<String, PhysicalDamage> headPhysical = new HashMap<>();
        headPhysical.put("Normal", headPhysicalNormalDamage);
        headPhysical.put("Broken", headPhysicalBrokenDamage);
        Map<ElementalType, Integer> headElementalNormalDamageCount = new HashMap<>();
        headElementalNormalDamageCount.put(ElementalType.FIRE, 0);
        headElementalNormalDamageCount.put(ElementalType.WATER, 5);
        headElementalNormalDamageCount.put(ElementalType.ICE, 0);
        headElementalNormalDamageCount.put(ElementalType.THUNDER, 20);
        headElementalNormalDamageCount.put(ElementalType.DRAGON, 20);
        ElementalDamage headElementalNormalDamage = new ElementalDamage(headElementalNormalDamageCount);
        Map<ElementalType, Integer> headElementalBrokenDamageCount = new HashMap<>();
        headElementalBrokenDamageCount.put(ElementalType.FIRE, 0);
        headElementalBrokenDamageCount.put(ElementalType.WATER, 15);
        headElementalBrokenDamageCount.put(ElementalType.ICE, 5);
        headElementalBrokenDamageCount.put(ElementalType.THUNDER, 20);
        headElementalBrokenDamageCount.put(ElementalType.DRAGON, 25);
        ElementalDamage headElementalBrokenDamage = new ElementalDamage(headElementalBrokenDamageCount);
        Map<String, ElementalDamage> headElemental = new HashMap<>();
        headElemental.put("Normal", headElementalNormalDamage);
        headElemental.put("Broken", headElementalBrokenDamage);
        BodyPart head = new BodyPart("Head", headPhysical, headElemental);

        Map<PhysicalType, Integer> bodyPhysicalNormalDamageCount = new HashMap<>();
        bodyPhysicalNormalDamageCount.put(PhysicalType.CUT, 50);
        bodyPhysicalNormalDamageCount.put(PhysicalType.HIT, 50);
        bodyPhysicalNormalDamageCount.put(PhysicalType.SHOT, 60);
        PhysicalDamage bodyPhysicalNormalDamage = new PhysicalDamage(bodyPhysicalNormalDamageCount);
        Map<PhysicalType, Integer> bodyPhysicalBrokenDamageCount = new HashMap<>();
        bodyPhysicalBrokenDamageCount.put(PhysicalType.CUT, 50);
        bodyPhysicalBrokenDamageCount.put(PhysicalType.HIT, 50);
        bodyPhysicalBrokenDamageCount.put(PhysicalType.SHOT, 60);
        PhysicalDamage bodyPhysicalBrokenDamage = new PhysicalDamage(bodyPhysicalBrokenDamageCount);
        Map<String, PhysicalDamage> bodyPhysical = new HashMap<>();
        bodyPhysical.put("Normal", bodyPhysicalNormalDamage);
        bodyPhysical.put("Broken", bodyPhysicalBrokenDamage);
        Map<ElementalType, Integer> bodyElementalNormalDamageCount = new HashMap<>();
        bodyElementalNormalDamageCount.put(ElementalType.FIRE, 0);
        bodyElementalNormalDamageCount.put(ElementalType.WATER, 5);
        bodyElementalNormalDamageCount.put(ElementalType.ICE, 0);
        bodyElementalNormalDamageCount.put(ElementalType.THUNDER, 20);
        bodyElementalNormalDamageCount.put(ElementalType.DRAGON, 20);
        ElementalDamage bodyElementalNormalDamage = new ElementalDamage(bodyElementalNormalDamageCount);
        Map<ElementalType, Integer> bodyElementalBrokenDamageCount = new HashMap<>();
        bodyElementalBrokenDamageCount.put(ElementalType.FIRE, 0);
        bodyElementalBrokenDamageCount.put(ElementalType.WATER, 15);
        bodyElementalBrokenDamageCount.put(ElementalType.ICE, 5);
        bodyElementalBrokenDamageCount.put(ElementalType.THUNDER, 20);
        bodyElementalBrokenDamageCount.put(ElementalType.DRAGON, 25);
        ElementalDamage bodyElementalBrokenDamage = new ElementalDamage(bodyElementalBrokenDamageCount);
        Map<String, ElementalDamage> bodyElemental = new HashMap<>();
        bodyElemental.put("Normal", bodyElementalNormalDamage);
        bodyElemental.put("Broken", bodyElementalBrokenDamage);
        BodyPart body = new BodyPart("Body", bodyPhysical, bodyElemental);

        Set<BodyPart> bodyParts = new HashSet<>();
        bodyParts.add(head);
        bodyParts.add(body);

        Set<AilmentType> ailments = new HashSet<>();
        ailments.add(AilmentType.FIREBLIGHT_SEVERE);
        ailments.add(AilmentType.DRAGONBLIGHT_SEVERE);
        ailments.add(AilmentType.DEFENCE_DOWN);

        MonsterStatus statusPoison = new MonsterStatus(AilmentType.POISON, 400, 150, 1000, 60, 240);
        MonsterStatus statusSleep = new MonsterStatus(AilmentType.SLEEP, 350, 150, 950, 30, null);
        MonsterStatus statusParalysis = new MonsterStatus(AilmentType.PARALYSIS, 350, 150, 950, 10, null);
        MonsterStatus statusStun = new MonsterStatus(AilmentType.STUN, 200, 150, 800, 10, null);
        MonsterStatus statusBlast = new MonsterStatus(AilmentType.BLASTBLIGHT, 140, 75, 2015, null, 300);
        MonsterStatus statusJump = new MonsterStatus(AilmentType.JUMP, 50, 120, 530, null, null);
        Set<MonsterStatus> statuses = new HashSet<>();
        statuses.add(statusPoison);
        statuses.add(statusSleep);
        statuses.add(statusParalysis);
        statuses.add(statusStun);
        statuses.add(statusBlast);
        statuses.add(statusJump);

        Trap trapSonic = new Trap(TrapType.SONIC, 5, 0, 5);
        Trap trapFlash = new Trap(TrapType.FLASH, 5, 5, 5);
        Trap trapShock = new Trap(TrapType.SHOCK, 0, 0 ,0);
        Trap trapPitfall = new Trap(TrapType.PITFALL, 0, 0 ,0);
        Set<Trap> traps = new HashSet<>();
        traps.add(trapSonic);
        traps.add(trapFlash);
        traps.add(trapShock);
        traps.add(trapPitfall);

        monster = new Monster("Akantor",
                "akantor",
                "akantor",
                "The Black God, Tyrant of Fire",
                "A wyvern truly wrapped in mystery. Known to some as the black god and to others as the tyrant of fire, this large and brutal creature is known to the Guild simply as Akantor...",
                "It has strong forelimbs, thick spikes, a clawed tail and large tusks. The Akantor bears a strong resemblance to Tigrex, the differences being that Akantor has only the barest nubs of forewings left, making it incapable of flight, and its immense size, which dramatically slows down its movements.",
                ElementalType.FIRE,
                ElementalType.DRAGON,
                bodyParts,
                ailments,
                statuses,
                traps);

        ailment = new Ailment(AilmentType.FIREBLIGHT, "Quickly decreases health over time.", "Nulberry", "Roll three times, or roll into water once.");
    }

    public String getMonster() {
        Gson gson = new Gson();
        return gson.toJson(monster);
    }

    public String getAilment() {
        Gson gson = new Gson();
        return gson.toJson(ailment);
    }
}
