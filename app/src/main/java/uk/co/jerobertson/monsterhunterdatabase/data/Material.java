package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * A material deserialised from the materials json file.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class Material {
    private final String MONSTER_NAME;
    private final RankType RANK;
    private final String ITEM_NAME;
    private final TagType OBTAIN_BY;
    private final String BODY_PART;
    private final Integer OBTAIN_CHANCE;
    private final Integer COUNT;

    public Material(String MONSTER_NAME, RankType RANK, String ITEM_NAME, TagType OBTAIN_BY, String BODY_PART, Integer OBTAIN_CHANCE, Integer COUNT) {
        this.MONSTER_NAME = MONSTER_NAME;
        this.RANK = RANK;
        this.ITEM_NAME = ITEM_NAME;
        this.OBTAIN_BY = OBTAIN_BY;
        this.BODY_PART = BODY_PART;
        this.OBTAIN_CHANCE = OBTAIN_CHANCE;
        this.COUNT = COUNT;
    }

    public String getMONSTER_NAME() {
        return MONSTER_NAME;
    }

    public RankType getRANK() {
        return RANK;
    }

    public String getITEM_NAME() {
        return ITEM_NAME;
    }

    public TagType getOBTAIN_BY() {
        return OBTAIN_BY;
    }

    public String getBODY_PART() {
        return BODY_PART;
    }

    public Integer getOBTAIN_CHANCE() {
        return OBTAIN_CHANCE;
    }

    public Integer getCOUNT() {
        return COUNT;
    }
}
