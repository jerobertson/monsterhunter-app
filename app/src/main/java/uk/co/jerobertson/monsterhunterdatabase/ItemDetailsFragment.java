package uk.co.jerobertson.monsterhunterdatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import uk.co.jerobertson.monsterhunterdatabase.data.Item;
import uk.co.jerobertson.monsterhunterdatabase.data.Material;

/**
 * The fragment class for item details.
 *
 * @author James Robertson
 */
public class ItemDetailsFragment extends Fragment {
    public static String ARG_ITEM_NAME = "item";

    private String itemName;
    private Item item;

    private OnFragmentInteractionListener mListener;

    /**
     * Fragments require empty public constructors.
     */
    public ItemDetailsFragment() {
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
            itemName = getArguments().getString(ARG_ITEM_NAME);
            item = App.getItem(itemName);
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
        SpannableString s = new SpannableString("Item Details");
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
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);

        //Set title/name
        TextView name = view.findViewById(R.id.name);
        name.setText(itemName);

        //Set image/icon
        ImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(App.getItemImage(item.getNAME()));

        //Set price
        TextView price = view.findViewById(R.id.price);
        price.setText((item.getPRICE() == null) ? "?z" : item.getPRICE() + "z");

        //Set tags
        TextView tags = view.findViewById(R.id.tags);
        tags.setText(item.getTagString());

        //Set description
        TextView description = view.findViewById(R.id.description);
        description.setText(item.getDESCRIPTION());

        //Items have a "first" combo (which can be null) and then a number of alternatives (which can be 0).
        //First we decide whether to grey out or show the "first" combo, and afterwards we decide if we want to add any more combos.
        TextView combo1Text = view.findViewById(R.id.combo_1_name);
        TextView combo2Text = view.findViewById(R.id.combo_2_name);
        ImageView combo1Image = view.findViewById(R.id.combo_1);
        ImageView combo2Image = view.findViewById(R.id.combo_2);

        if (!item.getCOMBINATIONS().isEmpty()) {
            //Handle the "first" combo
            combo1Text.setText(item.getCOMBINATIONS().get(0).getITEM_1());
            combo2Text.setText(item.getCOMBINATIONS().get(0).getITEM_2());
            if (combo1Text.getText() == "") combo1Text.setText(R.string.not_in_database);
            if (combo2Text.getText() == "") combo2Text.setText(R.string.not_in_database);

            combo1Image.setImageResource(App.getItemImage(item.getCOMBINATIONS().get(0).getITEM_1()));
            combo2Image.setImageResource(App.getItemImage(item.getCOMBINATIONS().get(0).getITEM_2()));
            combo1Image.setImageAlpha(255);
            combo2Image.setImageAlpha(255);
            combo1Image.setOnClickListener(switchItems(item.getCOMBINATIONS().get(0).getITEM_1()));
            combo2Image.setOnClickListener(switchItems(item.getCOMBINATIONS().get(0).getITEM_2()));

            //Handle all alternative combos
            if (item.getCOMBINATIONS().size() > 1) {
                LinearLayout altCombo = view.findViewById(R.id.combination_alternative);
                for (int i = 1; i < item.getCOMBINATIONS().size(); i++) {
                    @SuppressLint("InflateParams")
                    View altComboItem = getLayoutInflater().inflate(R.layout.item_combination, null);
                    ImageView combo1 = altComboItem.findViewById(R.id.combo_1);
                    ImageView combo2 = altComboItem.findViewById(R.id.combo_2);
                    TextView combo1Name = altComboItem.findViewById(R.id.combo_1_name);
                    TextView combo2Name = altComboItem.findViewById(R.id.combo_2_name);

                    combo1.setImageResource(App.getItemImage(item.getCOMBINATIONS().get(i).getITEM_1()));
                    combo2.setImageResource(App.getItemImage(item.getCOMBINATIONS().get(i).getITEM_2()));
                    combo1.setOnClickListener(switchItems(item.getCOMBINATIONS().get(i).getITEM_1()));
                    combo2.setOnClickListener(switchItems(item.getCOMBINATIONS().get(i).getITEM_2()));

                    combo1Name.setText(item.getCOMBINATIONS().get(i).getITEM_1());
                    combo2Name.setText(item.getCOMBINATIONS().get(i).getITEM_2());
                    if (combo1Name.getText() == "") combo1Name.setText(R.string.not_in_database);
                    if (combo2Name.getText() == "") combo2Name.setText(R.string.not_in_database);
                    altCombo.addView(altComboItem);
                }
            }
        } else {
            combo1Image.setImageAlpha(51);
            combo2Image.setImageAlpha(51);
        }

        //An item can be used to create multiple other items. Lets populate that list.
        LinearLayout createsLayout = view.findViewById(R.id.creates_items);
        if (!item.getCREATES().isEmpty()) {
            for (String i : item.getCREATES()) {
                View createsItem = getLayoutInflater().inflate(R.layout.fragment_item_sm, createsLayout, false);
                ImageView createsIcon = createsItem.findViewById(R.id.icon);
                TextView createsName = createsItem.findViewById(R.id.name);

                createsIcon.setImageResource(App.getItemImage(i));
                createsIcon.setOnClickListener(switchItems(i));
                createsName.setText(i);
                createsLayout.addView(createsItem);
            }
        } else {
            //If an item isn't used to create anything, set a placeholder "none" image.
            View createsItem = getLayoutInflater().inflate(R.layout.fragment_item_sm, createsLayout, false);
            ImageView createsIcon = createsItem.findViewById(R.id.icon);
            createsIcon.setImageAlpha(51);
            createsLayout.addView(createsItem);
        }

        //Show the list of monsters that drop this item (if any).
        LinearLayout dropsLayout = view.findViewById(R.id.drops_items);
        Set<String> addedMonsters = new HashSet<>();
        for (Material m : App.getMonsterMaterials()) {
            if (m.getITEM_NAME().equals(itemName)) {
                if (addedMonsters.contains(m.getMONSTER_NAME())) continue;
                addedMonsters.add(m.getMONSTER_NAME());

                View dropsItem = getLayoutInflater().inflate(R.layout.fragment_item_sm, createsLayout, false);
                ImageView dropsIcon = dropsItem.findViewById(R.id.icon);
                TextView dropsName = dropsItem.findViewById(R.id.name);

                dropsIcon.setImageResource(App.getMonsterImage(m.getMONSTER_NAME()));
                dropsIcon.setOnClickListener(switchMonsters(m.getMONSTER_NAME()));
                dropsName.setText(m.getMONSTER_NAME());
                dropsLayout.addView(dropsItem);
            }
        }
        if (addedMonsters.size() == 0) {
            //If this item isn't dropped by a monster, set a placeholder "none" image.
            View dropsItem = getLayoutInflater().inflate(R.layout.fragment_item_sm, createsLayout, false);
            ImageView dropsIcon = dropsItem.findViewById(R.id.icon);
            dropsIcon.setImageAlpha(51);
            dropsLayout.addView(dropsItem);
        }

        return view;
    }

    /**
     * Removes the search and desire sensor menu icons from the actionbar.
     * @param menu The menu to update.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(false);
        menu.findItem(R.id.desire).setVisible(false);
        menu.findItem(R.id.physiology).setVisible(false);
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
                ((OnItemDetailsPassthroughListener)getActivity()).onItemSelected(name);
            }
        };
    }

    private View.OnClickListener switchMonsters(final String name) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MonsterDetailsFragment.OnMonsterDetailsPassthroughListener)getActivity()).onMonsterSelected(name);
            }
        };
    }

    /**
     * Currently unused but is left in as a placeholder for future updates.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * An interface for the activity to implement that enables switch to a new item fragment.
     */
    public interface OnItemDetailsPassthroughListener {
        void onItemSelected(String name);
    }
}
