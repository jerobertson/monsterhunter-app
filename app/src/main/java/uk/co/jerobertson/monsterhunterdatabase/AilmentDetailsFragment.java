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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import uk.co.jerobertson.monsterhunterdatabase.data.Ailment;
import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;

/**
 * The fragment class for ailment details.
 *
 * @author James Robertson
 */
public class AilmentDetailsFragment extends Fragment {
    public static String ARG_AILMENT_NAME = "ailment";

    private AilmentType ailmentType;
    private Ailment ailment;

    private OnFragmentInteractionListener mListener;

    /**
     * Fragments require empty public constructors.
     */
    public AilmentDetailsFragment() {
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
            ailmentType = AilmentType.valueOf(getArguments().getString(ARG_AILMENT_NAME));
            ailment = App.getAilment(ailmentType);
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
        SpannableString s = new SpannableString("Ailment Details");
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
        View view = inflater.inflate(R.layout.fragment_ailment_details, container, false);

        //Set title/name
        TextView name = view.findViewById(R.id.name);
        name.setText(ailmentType.toString());

        //Set image/icon.
        ImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(App.getAilmentImage((ailmentType)));

        //Set description
        TextView description = view.findViewById(R.id.description);
        description.setText(ailment.getDESCRIPTION());

        //If the ailment doesn't have a cure, reduce its opacity, otherwise set its icon and update the text.
        ImageView cureImage = view.findViewById(R.id.cure_icon);
        if (ailment.getITEM_CURE() == null) {
            cureImage.setImageAlpha(51);
        } else {
            cureImage.setImageResource(App.getItemImage(ailment.getITEM_CURE()));
            cureImage.setOnClickListener(switchItems(ailment.getITEM_CURE()));
            ((TextView)view.findViewById(R.id.cure_text)).setText(ailment.getITEM_CURE());
        }

        //If the ailment has an alternative cure, set the text, otherwise hide the alt. views.
        if (ailment.getCURE() != null) {
            ((TextView)view.findViewById(R.id.alt_text)).setText(ailment.getCURE());
        } else {
            view.findViewById(R.id.alt_title).setVisibility(View.GONE);
            view.findViewById(R.id.alt_text).setVisibility(View.GONE);
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
                ((ItemDetailsFragment.OnItemDetailsPassthroughListener)getActivity()).onItemSelected(name);
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
     * An interface for the activity to implement that enables switching to a new ailment fragment.
     */
    public interface OnAilmentDetailsPassthroughListener {
        void onAilmentSelected(AilmentType type);
    }
}
