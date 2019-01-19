package uk.co.jerobertson.monsterhunterdatabase.data;

import java.util.List;

/**
 * The deserialisation of an item from the json file.
 *
 * @author James Robertson
 */
public class Item {
    private final String NAME;
    private final String ICON;
    private final String DESCRIPTION;
    private final Integer PRICE;
    private final List<Combination> COMBINATIONS; //A list of combinations to make this item.
    private final List<String> CREATES; //A list of items this item can be used to create.
    private final List<TagType> TAGS; //A list of item tags that can be used for searching.

    public Item(String NAME, String ICON, String DESCRIPTION, Integer PRICE, List<Combination> COMBINATIONS, List<String> CREATES, List<TagType> TAGS) {
        this.NAME = NAME;
        this.ICON = ICON;
        this.DESCRIPTION = DESCRIPTION;
        this.PRICE = PRICE;
        this.COMBINATIONS = COMBINATIONS;
        this.CREATES = CREATES;
        this.TAGS = TAGS;
    }

    public String getNAME() {
        return NAME;
    }

    public String getICON() {
        return ICON;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public Integer getPRICE() {
        return PRICE;
    }

    public List<Combination> getCOMBINATIONS() {
        return COMBINATIONS;
    }

    public List<String> getCREATES() {
        return CREATES;
    }

    public List<TagType> getTAGS() {
        return TAGS;
    }

    /**
     * The tag string is a list of all the tags pre-pended by a # and followed by a space.
     * @return A string containing all of the tags.
     */
    public String getTagString() {
        StringBuilder sb = new StringBuilder();
        for (TagType tag : TAGS) {
            sb.append(String.format("#%s ", tag.toString()));
        }
        return sb.toString();
    }
}
