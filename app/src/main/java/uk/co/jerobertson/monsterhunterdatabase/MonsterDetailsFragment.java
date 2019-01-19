package uk.co.jerobertson.monsterhunterdatabase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;
import uk.co.jerobertson.monsterhunterdatabase.data.ElementalType;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;
import uk.co.jerobertson.monsterhunterdatabase.data.PhysicalType;
import uk.co.jerobertson.monsterhunterdatabase.data.Trap;
import uk.co.jerobertson.monsterhunterdatabase.data.TrapType;

public class MonsterDetailsFragment extends Fragment {
    public static String ARG_MOSTER_NAME = "monster";

    private String monsterName;
    private Monster monster;

    private OnFragmentInteractionListener mListener;

    /**
     * Fragments require empty public constructors.
     */
    public MonsterDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Sets up the variables for the instance and informs the activity this fragment has a custom
     * options menu.
     * @param savedInstanceState A saved instance of this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            monsterName = getArguments().getString(ARG_MOSTER_NAME);
            monster = App.getMonster(monsterName);
        }
    }

    /**
     * Sets the title of the actionbar and informs the activity the navdrawer should not be
     * available.
     */
    @Override
    @SuppressWarnings("ConstantConditions") //We know that the action bar won't be null.
    public void onResume() {
        super.onResume();
        SpannableString s = new SpannableString("Monster Details");
        s.setSpan(new TypefaceSpan(App.getContext(), "imperator_small_caps.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((MainActivity)getActivity()).inMenu = false;
    }

    /**
     * Inflates the layout and sets the text and image resources.
     * @param inflater The layout inflater.
     * @param container The viewgroup container.
     * @param savedInstanceState A saved instance state.
     * @return The inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monster_details, container, false);
        int physicalDamageAverage = monster.getAveragePhysicalDamage();

        //Sets the monster icon, title and descriptor/aka tag.
        setHeader(view);

        //Gets the monster's elemental attacks and displays them.
        setAttack(view);

        //Get the monster's ailments and list them in the flexbox, adding listeners to navigate to ailment description views.
        setAilments((FlexboxLayout)view.findViewById(R.id.ailment_layout));

        //Set icons and descriptions for traps.
        setTraps(view);

        //Create each of the 5 elemental bars. This is a surprisingly complex ordeal.
        createElementBar(view.findViewById(R.id.fire_damage), ElementalType.FIRE, AilmentType.FIREBLIGHT, physicalDamageAverage, R.color.colorFire);
        createElementBar(view.findViewById(R.id.water_damage), ElementalType.WATER, AilmentType.WATERBLIGHT, physicalDamageAverage, R.color.colorWater);
        createElementBar(view.findViewById(R.id.ice_damage), ElementalType.ICE, AilmentType.ICEBLIGHT, physicalDamageAverage, R.color.colorIce);
        createElementBar(view.findViewById(R.id.thunder_damage), ElementalType.THUNDER, AilmentType.THUNDERBLIGHT, physicalDamageAverage, R.color.colorThunder);
        createElementBar(view.findViewById(R.id.dragon_damage), ElementalType.DRAGON, AilmentType.DRAGONBLIGHT, physicalDamageAverage, R.color.colorDragon);

        //Calculate and displays the monster's weakest body parts.
        setWeakText(view.findViewById(R.id.parts_layout));

        return view;
    }

    /**
     * Sets the title, icon and description/aka of the view.
     * @param view The view to update, ie this fragment.
     */
    private void setHeader(View view) {
        ((TextView)view.findViewById(R.id.name)).setText(monsterName);
        ((ImageView)view.findViewById(R.id.icon)).setImageResource(App.getMonsterImage(monsterName));
        ((TextView)view.findViewById(R.id.aka)).setText(monster.getAKA());
    }

    /**
     * If a monster has an elemental attack, change the icon to the element. Otherwise, grey out
     * the image (which is by default, a question mark).
     * @param view The view to update, ie this fragment.
     */
    private void setAttack(View view) {
        ImageView primaryAttack = view.findViewById(R.id.primary_attack);
        primaryAttack.setImageResource(App.getElementalImage(monster.getPRIMARY_ATTACK()));
        primaryAttack.setImageAlpha((monster.getPRIMARY_ATTACK() == null) ? 51 : 255);

        ImageView secondaryAttack = view.findViewById(R.id.secondary_attack);
        secondaryAttack.setImageResource(App.getElementalImage(monster.getSECONDARY_ATTACK()));
        secondaryAttack.setImageAlpha((monster.getSECONDARY_ATTACK() == null) ? 51 : 255);
    }

    /**
     * If a monster can cause ailments, create an imageview with the relevant ailment, give it a
     * listener and add it to the view.
     * @param layout The layout to put the ailment in.
     */
    private void setAilments(FlexboxLayout layout) {
        if (monster.getAILMENTS() == null) return;
        for (AilmentType ailment : monster.getAILMENTS()) {
            ImageView ailmentImage = (ImageView) getLayoutInflater().inflate(R.layout.generic_image, layout, false);
            ailmentImage.setImageResource(App.getAilmentImage(ailment));
            ailmentImage.setOnClickListener(switchAilments(ailment));
            layout.addView(ailmentImage);
        }
    }

    /**
     * Each monster is vulnerable (or not) to 4 traps (2 bombs and 2 actual traps). Here we define
     * (by setting the opacity of the image) whether the monster is effected, and then set text to
     * explain how long each monster is affected for when it is normal/enraged/fatigued.
     * @param view The layout to update the traps from.
     */
    private void setTraps(View view) {
        //Grey out all the images by default, and make them opaque again if the monster is affected.
        ((ImageView)view.findViewById(R.id.trap_sonic_image)).setImageAlpha(51);
        ((ImageView)view.findViewById(R.id.trap_flash_image)).setImageAlpha(51);
        ((ImageView)view.findViewById(R.id.trap_shock_image)).setImageAlpha(51);
        ((ImageView)view.findViewById(R.id.trap_pitfall_image)).setImageAlpha(51);

        //When a user clicks on one of the items, let them get the item details for it.
        view.findViewById(R.id.trap_sonic_image).setOnClickListener(switchItems("Sonic Bomb"));
        view.findViewById(R.id.trap_flash_image).setOnClickListener(switchItems("Flash Bomb"));
        view.findViewById(R.id.trap_shock_image).setOnClickListener(switchItems("Shock Trap"));
        view.findViewById(R.id.trap_pitfall_image).setOnClickListener(switchItems("Pitfall Trap"));

        for (Trap trap : monster.getTRAPS()) {
            if (trap.getTYPE().equals(TrapType.SONIC)) {
                ((ImageView)view.findViewById(R.id.trap_sonic_image)).setImageAlpha(255);
                ((TextView)view.findViewById(R.id.trap_sonic_text)).setText(String.format("%ss/%ss/%ss", trap.getNORMAL(), trap.getENRAGED(), trap.getFATIGUED()));
            } else if (trap.getTYPE().equals(TrapType.FLASH)) {
                ((ImageView)view.findViewById(R.id.trap_flash_image)).setImageAlpha(255);
                ((TextView)view.findViewById(R.id.trap_flash_text)).setText(String.format("%ss/%ss/%ss", trap.getNORMAL(), trap.getENRAGED(), trap.getFATIGUED()));

            } else if (trap.getTYPE().equals(TrapType.SHOCK)) {
                ((ImageView)view.findViewById(R.id.trap_shock_image)).setImageAlpha(255);
                ((TextView)view.findViewById(R.id.trap_shock_text)).setText(String.format("%ss/%ss/%ss", trap.getNORMAL(), trap.getENRAGED(), trap.getFATIGUED()));

            } else if (trap.getTYPE().equals(TrapType.PITFALL)) {
                ((ImageView)view.findViewById(R.id.trap_pitfall_image)).setImageAlpha(255);
                ((TextView)view.findViewById(R.id.trap_pitfall_text)).setText(String.format("%ss/%ss/%ss", trap.getNORMAL(), trap.getENRAGED(), trap.getFATIGUED()));
            }
        }
    }

    /**
     * Element bars have multiple parts that need dealing with. This method handles all of them; the
     * icon, the percentage bar and the average element damage.
     * @param view The view to update.
     * @param element The name of the element being updated.
     * @param ailment The ailment equivalent of the element.
     * @param physicalDamageAverage The average physical damage done to the monster (used for
     *                             calculations)
     * @param colour The colour to use for the elemental bar.
     */
    private void createElementBar(View view, ElementalType element, AilmentType ailment, int physicalDamageAverage, int colour) {
        int elementAverage = monster.getAverageElementalDamage(element);
        double elementPercent = ((double)elementAverage) / physicalDamageAverage;

        setElementBarIcon(view, ailment, elementPercent, elementAverage);
        setElementBar(view, (float) elementPercent, colour);
        setElementCount(view, Integer.toString(monster.getAverageElementalDamage(element)));
    }

    /**
     * The element icon depends upon how much damage the element does relative to the average
     * physical damage of the monster.
     * @param element The view to update.
     * @param type The ailment type (to retrieve the images).
     * @param average The elemental percentage for the monster.
     * @param amount The actual amount of damage. In the case that a monster is not probably defined
     *               in the database, the element average would be 1 making it "super effective".
     *               Instead, we can use this to check if it's just 0 and set it to "minimal".
     */
    private void setElementBarIcon(View element, AilmentType type, double average, double amount) {
        ImageView icon = element.findViewById(R.id.icon);

        if (amount == 0 || average < 0.25) { //Minimal damage, grey out the icon.
            icon.setImageResource(App.getAilmentImage(type));
            icon.setImageAlpha(51);
        } else if (average < 0.4) { //Average damage, show the icon.
            icon.setImageResource(App.getAilmentImage(type));
        } else if (average < 0.6) { //Lots of damage, show the + variant of the icon.
            icon.setImageResource(App.getAilmentImage(AilmentType.valueOf(type.name() + "_P")));
        } else { //Huge amounts of damage, show the "++" variant of the icon.
            icon.setImageResource(App.getAilmentImage(AilmentType.valueOf(type.name() + "_PP")));
        }
    }

    /**
     * Android's progress bar is horrible. You actually cannot get rid of the margin and padding
     * surrounding it. As such, I've cheated somewhat by simply defining two frame layouts with
     * different coloured backgrounds and setting weights on each one to simulate progress.
     * @param element The view to update.
     * @param percent The percentage of the bar to fill.
     * @param colour The colour of the bar.
     */
    private void setElementBar(View element, float percent, int colour) {
        FrameLayout amount = element.findViewById(R.id.amount);
        FrameLayout remaining = element.findViewById(R.id.remaining);

        if (Float.isNaN(percent)) percent = 0f; //Monster's not set up properly in the database, so grey out the bar.

        //Set the first frame layout to weigh as much as the element's percent.
        ViewGroup.LayoutParams elementAmountLayout = amount.getLayoutParams();
        LinearLayout.LayoutParams elementAmountNewLayout = new LinearLayout.LayoutParams(elementAmountLayout.width, elementAmountLayout.height, percent);
        elementAmountNewLayout.setMargins(0, App.getPxFromDp(5), 0, App.getPxFromDp(5));
        amount.setLayoutParams(elementAmountNewLayout);
        amount.setBackgroundResource(colour);

        //Set the second frame layout to weigh 1 - the element's progress. It's opacity is decreased in the layout to simulate being greyed out.
        ViewGroup.LayoutParams elementRemainingLayout = remaining.getLayoutParams();
        LinearLayout.LayoutParams elementRemainingNewLayout = new LinearLayout.LayoutParams(elementRemainingLayout.width, elementRemainingLayout.height, 1 - percent);
        elementRemainingNewLayout.setMargins(0, App.getPxFromDp(5), 0, App.getPxFromDp(5));
        remaining.setLayoutParams(elementRemainingNewLayout);
        remaining.setBackgroundResource(colour);
    }

    /**
     * Sets the element bar's number to the average for that element.
     * @param element The view to update.
     * @param count The amount to set as the text.
     */
    private void setElementCount(View element, String count) {
        TextView text = element.findViewById(R.id.avg_text);
        text.setText(count);
    }

    /**
     * Monsters have bodyparts, some of which are weaker than others. This method updates the text
     * fields relevant for the three different weapon types to show the average damage done to the
     * weakest bodyparts.
     * @param view The view to update.
     */
    private void setWeakText(View view) {
        getWeakText((LinearLayout)view.findViewById(R.id.weak_cut_layout), PhysicalType.CUT);
        getWeakText((LinearLayout)view.findViewById(R.id.weak_hit_layout), PhysicalType.HIT);
        getWeakText((LinearLayout)view.findViewById(R.id.weak_shot_layout), PhysicalType.SHOT);

        ((TextView)view.findViewById(R.id.weak_cut_avg)).setText(String.format("Avg.\n%s", monster.getAveragePhysicalDamage(PhysicalType.CUT)));
        ((TextView)view.findViewById(R.id.weak_hit_avg)).setText(String.format("Avg.\n%s", monster.getAveragePhysicalDamage(PhysicalType.HIT)));
        ((TextView)view.findViewById(R.id.weak_shot_avg)).setText(String.format("Avg.\n%s", monster.getAveragePhysicalDamage(PhysicalType.SHOT)));
    }

    /**
     * If a monster is not defined correctly in the database and does not have body parts, then this
     * simply shows an "information not available" message. Otherwise, it gets the weakest body
     * parts and displays them in a textview.
     *
     * Note: Bodypart"types" is an interesting thing to try and understand. Each bodypart can have
     * multiple states (such as unbroken, broken, etc) and we want to show information for each
     * state.
     * @param view The view to add the information to.
     * @param type The physical type of damage to calculate.
     */
    private void getWeakText(LinearLayout view, PhysicalType type) {
        if (monster.getBodyPartTypes().isEmpty()) { //Monster isn't defined correctly, show placeholder text.
            TextView textView = new TextView(App.getContext());
            textView.setText(R.string.no_info);
            textView.setTextAppearance(App.getContext(), R.style.AppTheme_DetailsText);
            view.addView(textView);
        } else { //For each state of the body part, add a textview showing the weakest bodyparts with that state.
            for (String partType : monster.getBodyPartTypes()) {
                TextView textView = new TextView(App.getContext());
                textView.setText(getPartTypeWeakText(partType, type));
                textView.setTextAppearance(App.getContext(), R.style.AppTheme_DetailsText);
                view.addView(textView);
            }
        }
    }

    /**
     * A helper method that generates the text for the bodypart weaknesses by getting the weakest
     * parts from the monster object and creating a string for the first three.
     * @param partType The name of the state of the bodypart to get the information for.
     * @param type The physical type of damage (such as cut, hit, shot).
     * @return A formatted string to display to the user.
     */
    private String getPartTypeWeakText(String partType, PhysicalType type) {
        StringBuilder sb = new StringBuilder(partType + ": ");
        int count = 0; //The number of bodyparts already added to the string.
        final int length = 3; //The number of bodyparts to add to the string.
        SortedMap<Integer, List<String>> bodyPartsMap = monster.getWeakestBodyParts(partType, type);
        for (Map.Entry<Integer, List<String>> entry : bodyPartsMap.entrySet()) {
            for (String bodyPart : entry.getValue()) {
                count++;
                sb.append(String.format("%s (%s), ", bodyPart, entry.getKey()));
                if (count == length) { //Remove the trailing comma and whitespace and return the string.
                    sb.setLength(sb.length() - 2);
                    return sb.toString();
                }
            }
        }
        //There are less than two bodyparts, so just return whatever we've already got less the trailing comma.
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    /**
     * Removes the search and desire sensor menu icons from the actionbar.
     * @param menu The menu to update.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.desire).setVisible(true);
        menu.findItem(R.id.desire).setOnMenuItemClickListener(openDesire(monsterName));
        menu.findItem(R.id.physiology).setVisible(true);
        menu.findItem(R.id.physiology).setOnMenuItemClickListener(openPhysiology(monsterName));
        ((App.DrawerLocker)getActivity()).setDrawerEnabled(false);
    }

    /**
     * Currently unused but is left in as a placeholder for future updates.
     * @param uri The button pressed.
     */
    @SuppressWarnings("unused")
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Currently unused but is left in as a placeholder for future updates.
     * @param context The context of the application.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Disables the listener when the fragment is detached.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Listens for selecting an image of an item and then switching to the item details fragment.
     * @param name The name of the selected item.
     * @return The new listener that switches fragments.
     */
    private View.OnClickListener switchItems(final String name) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ItemDetailsFragment.OnItemDetailsPassthroughListener)getActivity()).onItemSelected(name);
            }
        };
    }

    /**
     * Listens for selecting an image of an ailment and then switching to the ailment details
     * fragment.
     * @param ailmentType The type of ailment selected.
     * @return The new listener that switches fragments.
     */
    private View.OnClickListener switchAilments(final AilmentType ailmentType) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AilmentDetailsFragment.OnAilmentDetailsPassthroughListener)getActivity()).onAilmentSelected(ailmentType);
            }
        };
    }

    private MenuItem.OnMenuItemClickListener openPhysiology(final String name) {
        return new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ((OnPhysiologySelected)getActivity()).onPhysiologySelected(name);
                return true;
            }
        };
    }

    private MenuItem.OnMenuItemClickListener openDesire(final String name) {
        return new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                ((OnDesireSelected)getActivity()).onDesireSelected(name);
                return true;
            }
        };
    }

    /**
     * Currently unused but is left in as a placeholder for future updates.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public interface OnMonsterDetailsPassthroughListener {
        void onMonsterSelected(String monsterName);
    }

    public interface OnPhysiologySelected {
        void onPhysiologySelected(String monsterName);
    }

    public interface OnDesireSelected {
        void onDesireSelected(String monsterName);
    }
}
