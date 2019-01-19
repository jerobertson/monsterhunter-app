package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * Ailment object to deserialise ailments json to ailment objects.
 *
 * @author James Robertson
 */
public class Ailment {
    private final AilmentType TYPE;
    private final String DESCRIPTION;
    private final String ITEM_CURE;
    private final String CURE;

    public Ailment(AilmentType TYPE, String DESCRIPTION, String ITEM_CURE, String CURE) {
        this.TYPE = TYPE;
        this.DESCRIPTION = DESCRIPTION;
        this.ITEM_CURE = ITEM_CURE;
        this.CURE = CURE;
    }

    public AilmentType getTYPE() {
        return TYPE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public String getITEM_CURE() {
        return ITEM_CURE;
    }

    public String getCURE() {
        return CURE;
    }
}
