package uk.co.jerobertson.monsterhunterdatabase;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

/**
 * The fragment class for the monsters list.
 */
public class MonsterListFragment extends Fragment {
    public static final String ARG_SEARCH = "search";

    private String search = null;
    private RecyclerView rView = null;
    private String previousSearch = null;

    private OnListFragmentInteractionListener mListener;

    /**
     * Fragments require empty public constructors.
     */
    public MonsterListFragment() {
        // Required empty public constructor
    }

    /**
     * When returning to this fragment, re-set the search so that the list of monsters shown is the
     * same as previous.
     * @param previousSearch The text in the searchview when this fragment was last displayed.
     */
    public void setPreviousSearch(String previousSearch) {
        this.previousSearch = previousSearch;
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
            search = getArguments().getString(ARG_SEARCH);
        }
    }

    /**
     * Sets the title of the actionbar and informs the activity the navdrawer should be available.
     */
    @Override
    @SuppressWarnings("ConstantConditions") //We know that the action bar won't be null.
    public void onResume() {
        super.onResume();
        SpannableString s = new SpannableString("Monsters");
        s.setSpan(new TypefaceSpan(App.getContext(), "imperator_small_caps.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((NavigationView)(getActivity()).findViewById(R.id.nav_view)).setCheckedItem(R.id.nav_monsters);
        ((MainActivity)getActivity()).inMenu = true;
    }

    /**
     * Inflates the layout and sets the text and adds items to the recyclerview.
     * @param inflater The layout inflater.
     * @param container The viewgroup container.
     * @param savedInstanceState A saved instance state.
     * @return The inflated view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monster_list, container, false);

        rView = view.findViewById(R.id.list_monster);
        rView.setAdapter(new MonsterListRecyclerViewAdapter(new MonsterContent(search).ITEMS, mListener));

        return view;
    }

    /**
     * Gets the recyclerview in this fragment so that the activity can update the items in it when
     * the user changes the search.
     * @return The main recylerview in this fragment.
     */
    public RecyclerView getRecyclerView() {
        return rView;
    }

    /**
     * Displays the search menu item but removes the desire sensor menu item from the actionbar.
     * @param menu The menu to update.
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.search).setVisible(true);
        menu.findItem(R.id.desire).setVisible(false);
        menu.findItem(R.id.physiology).setVisible(false);
        ((App.DrawerLocker)getActivity()).setDrawerEnabled(true);
        ((SearchView)menu.findItem(R.id.search).getActionView()).setQuery(previousSearch, false);
        if (((SearchView) menu.findItem(R.id.search).getActionView()).getQuery().length() != 0) {
            menu.findItem(R.id.search).expandActionView();
        }
    }

    /**
     * Currently unused but is left in as a placeholder for future updates.
     * @param context The context of the application.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * An interface for the activity to implement that enables switching to a monster details
     * fragment.
     */
    public interface OnListFragmentInteractionListener {
        void onMonsterListFragmentInteraction(MonsterContent.MonsterItem item);
    }
}
