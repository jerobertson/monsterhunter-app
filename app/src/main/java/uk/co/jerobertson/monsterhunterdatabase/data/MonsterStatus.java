package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * A status object created from the deserialisation of the monsters json file.
 *
 * @author James Robertson
 */
public class MonsterStatus {
    private final AilmentType TYPE;
    private final Integer INITIAL;
    private final Integer INCREMENT;
    private final Integer MAX;
    private final Integer DURATION;
    private final Integer DAMAGE;

    public MonsterStatus(AilmentType TYPE, Integer INITIAL, Integer INCREMENT, Integer MAX, Integer DURATION, Integer DAMAGE) {
        this.TYPE = TYPE;
        this.INITIAL = INITIAL;
        this.INCREMENT = INCREMENT;
        this.MAX = MAX;
        this.DURATION = DURATION;
        this.DAMAGE = DAMAGE;
    }

    public AilmentType getTYPE() {
        return TYPE;
    }

    public Integer getINITIAL() {
        return INITIAL;
    }

    public Integer getINCREMENT() {
        return INCREMENT;
    }

    public Integer getMAX() {
        return MAX;
    }

    public Integer getDURATION() {
        return DURATION;
    }

    public Integer getDAMAGE() {
        return DAMAGE;
    }
}
