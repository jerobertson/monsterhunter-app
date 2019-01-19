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

import com.google.android.flexbox.FlexboxLayout;

import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;
import uk.co.jerobertson.monsterhunterdatabase.data.MonsterStatus;

/**
 * The fragment class for a monster's physiology.
 *
 * @author James Robertson
 */
public class MonsterPhysiologyFragment extends Fragment {
    public static String ARG_MONSTER_NAME = "monster";

    private String monsterName;
    private Monster monster;

    private OnFragmentInteractionListener mListener;

    /**
     * Fragments require empty public constructors.
     */
    public MonsterPhysiologyFragment() {
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
            monsterName = getArguments().getString(ARG_MONSTER_NAME);
            monster = App.getMonster(monsterName);
        }
    }

    /**
     * Sets the title of the actionbar and informs the activity the navdrawer should not be
     * available.
     */
    @SuppressWarnings("ConstantConditions") //We know that the action bar won't be null.
    @Override
    public void onResume() {
        super.onResume();
        SpannableString s = new SpannableString("Monster Physiology");
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
    @SuppressLint("SetTextI18n") //Just setting fields to numbers.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monster_physiology, container, false);

        //Set title/name
        TextView name = view.findViewById(R.id.name);
        name.setText(monsterName);

        //Set image/icon.
        ImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(App.getMonsterImage(monsterName));

        //Set aka
        TextView aka = view.findViewById(R.id.aka);
        aka.setText(monster.getAKA());

        //Set description
        TextView description = view.findViewById(R.id.description_text);
        description.setText(monster.getDESCRIPTION());

        //Set physiology
        TextView physiology = view.findViewById(R.id.physiology_text);
        physiology.setText(monster.getPHYSIOLOGY());

        FlexboxLayout inflictionsLayout = view.findViewById(R.id.inflictions_content);
        if (monster.getSTATUSES() != null) {
            for (MonsterStatus ms : monster.getSTATUSES()) {
                LinearLayout status = (LinearLayout) getLayoutInflater().inflate(R.layout.status_bar, inflictionsLayout, false);
                ((ImageView) status.findViewById(R.id.icon)).setImageResource(App.getAilmentImage(ms.getTYPE()));
                status.findViewById(R.id.icon).setOnClickListener(switchAilments(ms.getTYPE()));
                if (ms.getINITIAL() != null) {
                    ((TextView) status.findViewById(R.id.init)).setText(Integer.toString(ms.getINITIAL()));
                }
                if (ms.getINCREMENT() != null) {
                    ((TextView) status.findViewById(R.id.inc)).setText(Integer.toString(ms.getINCREMENT()));
                }
                if (ms.getMAX() != null) {
                    ((TextView) status.findViewById(R.id.max)).setText(Integer.toString(ms.getMAX()));
                }
                if (ms.getDURATION() != null) {
                    ((TextView) status.findViewById(R.id.dur)).setText(Integer.toString(ms.getDURATION()));
                }
                if (ms.getDAMAGE() != null) {
                    ((TextView) status.findViewById(R.id.dmg)).setText(Integer.toString(ms.getDAMAGE()));
                }
                inflictionsLayout.addView(status);
            }
        } else {
            view.findViewById(R.id.inflictions_title).setVisibility(View.GONE);
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

    /**
     * Currently unused but is left in as a placeholder for future updates.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
