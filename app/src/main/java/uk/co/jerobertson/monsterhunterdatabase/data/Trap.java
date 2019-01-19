package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * A trap object created from the deserialisation of the monsters file. A trap has a type, and an
 * amount of time that it affects a monster depending on whether the monster is normal, enraged or
 * fatigued.
 *
 * @author James Robertson
 */
public class Trap {
    private final TrapType TYPE;
    private final Integer NORMAL;
    private final Integer ENRAGED;
    private final Integer FATIGUED;

    public Trap(TrapType TYPE, Integer NORMAL, Integer ENRAGED, Integer FATIGUED) {
        this.TYPE = TYPE;
        this.NORMAL = NORMAL;
        this.ENRAGED = ENRAGED;
        this.FATIGUED = FATIGUED;
    }

    public TrapType getTYPE() {
        return TYPE;
    }

    public Integer getNORMAL() {
        return NORMAL;
    }

    public Integer getENRAGED() {
        return ENRAGED;
    }

    public Integer getFATIGUED() {
        return FATIGUED;
    }
}
