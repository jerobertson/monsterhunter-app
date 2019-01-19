package uk.co.jerobertson.monsterhunterdatabase;

public enum ListType {
    MONSTER("Monster"),
    AILMENT("Ailment"),
    ITEM("Item")
    ;

    private final String text;

    ListType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
