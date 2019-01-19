package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * List of ailment types.
 *
 * @author James Robertson
 */
@SuppressWarnings("unused")
public enum AilmentType {
    FIREBLIGHT("Fireblight"),
    FIREBLIGHT_P("Fireblight +"),
    FIREBLIGHT_PP("Fireblight ++"),
    FIREBLIGHT_SEVERE("Severe Fireblight"),
    WATERBLIGHT("Waterblight"),
    WATERBLIGHT_P("Waterblight +"),
    WATERBLIGHT_PP("Waterblight ++"),
    WATERBLIGHT_SEVERE("Severe Waterblight"),
    ICEBLIGHT("Iceblight"),
    ICEBLIGHT_P("Iceblight +"),
    ICEBLIGHT_PP("Iceblight ++"),
    ICEBLIGHT_SEVERE("Severe Iceblight"),
    THUNDERBLIGHT("Thunderblight"),
    THUNDERBLIGHT_P("Thunderblight +"),
    THUNDERBLIGHT_PP("Thunderblight ++"),
    THUNDERBLIGHT_SEVERE("Severe Thunderblight"),
    DRAGONBLIGHT("Dragonblight"),
    DRAGONBLIGHT_P("Dragonblight +"),
    DRAGONBLIGHT_PP("Dragonblight ++"),
    DRAGONBLIGHT_SEVERE("Severe Dragonblight"),
    POISON("Poison"),
    POISON_DEADLY("Deadly Poison"),
    STUN("Stun"),
    PARALYSIS("Paralysis"),
    SLEEP("Sleep"),
    FATIGUE("Fatigue"),
    SOILED("Soiled"),
    BLASTBLIGHT("Blastblight"),
    SNOWMAN("Snowman"),
    MUDDY("Muddy"),
    WEBBED("Webbed"),
    TARRED("Tarred"),
    DEFENCE_DOWN("Defence Down"),
    BLEEDING("Bleeding"),
    JUMP("Jump"),
    MOUNT("Mount")
    ;

    private final String text;

    AilmentType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
