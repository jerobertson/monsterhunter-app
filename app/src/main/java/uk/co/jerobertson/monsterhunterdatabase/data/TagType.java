package uk.co.jerobertson.monsterhunterdatabase.data;

/**
 * Created by James on 24/02/2018.
 */

public enum TagType {
    BOOK("Book"),
    BOOK_COMBINATION("Combination Book"),
    BOOK_MONSTER_GUIDE("Monster Guide"),
    BOOK_BOX_SPACE("Box Space Book"),
    CONSUMABLE("Consumable"),
    MEAT("Meat"),
    CHARM("Charm"),
    THROWABLE("Throwable"),
    KNIFE("Knife"),
    BOMB("Bomb"),
    DUNG("Dung"),
    EGG("Egg"),
    PLACEABLE("Placeable"),
    TRAP("Trap"),
    AMMO("Ammo"),
    BAIT("Bait"),
    GATHERING("Gathering"),
    PICKAXE("Pickaxe"),
    BUGNET("Bugnet"),
    ORE("Ore"),
    HORN("Horn"),
    WYSTONE("Wystone"),
    PLANT("Plant"),
    BERRY("Berry"),
    MUSHROOM("Mushroom"),
    ABRASIVE("Abrasive"),
    ARMOURSPHERE("Armoursphere"),
    FISH("Fish"),
    INSECT("Insect"),
    NECTAR("Nectar"),
    RELIC("Relic"),
    FIELD_ITEM("Field Item"),
    ACCOUNT_ITEM("Account Item"),
    SCALE("Scale"),
    DROPABLE("Dropable"),
    MATERIAL("Monster Material"),

    CARVE_BODY("Body Carve"),
    CARVE_TAIL("Tail Carve"),
    CARVE_BODY_FAKE("Body Carve (Playing Dead)"),
    CARVE_BODY_LOWER("Body Carve (Lower)"),
    CARVE_BODY_UPPER("Body Carve (Upper)"),
    CARVE_HEAD("Head Carve"),
    CARVE_MOUTH("Mouth Carve"),
    SHINY_DROP("Shiny Drop"),
    BREAK_PART("Break Part"),
    MINING_BACK("Back Mining"),
    MINING_ORE("Ore Mining"),
    MINING_SCALE("Scale Mining"),
    BUG_CATCH_BACK("Back Bug Catch"),
    CAPTURE("Capture"),
    REWARD_FRENZY("Frenzy Reward"),
    REWARD_APEX("Apex Reward")
    ;

    private final String text;

    TagType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
