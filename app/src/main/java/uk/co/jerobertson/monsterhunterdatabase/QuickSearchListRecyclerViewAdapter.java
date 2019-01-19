package uk.co.jerobertson.monsterhunterdatabase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.jerobertson.monsterhunterdatabase.QuickSearchListFragment.OnListFragmentInteractionListener;
import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;

/**
 * Handles the recyclerview for the quick-search list.
 *
 * @author James Robertson
 */
@SuppressWarnings("WeakerAccess")
public class QuickSearchListRecyclerViewAdapter extends RecyclerView.Adapter<QuickSearchListRecyclerViewAdapter.ViewHolder> {
    private final List<QuickSearchContent.QuickSearchItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    /**
     * Sets up the recyclerview with a list of items and an interaction listener.
     * @param items The items to display in the recyclerview.
     * @param listener The listener to use when an item is selected.
     */
    public QuickSearchListRecyclerViewAdapter(List<QuickSearchContent.QuickSearchItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    /**
     * Inflates each item in the recyclerview so their details can be added.
     * @param parent The parent view to add the item to.
     * @param viewType The type of view, currently unused.
     * @return The inflated viewholder.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_quicksearch, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Sets the text and description of the created viewholders to display in the recyclerview.
     * @param holder The 'frame' of an item whose content needs to be filled out.
     * @param position The position in the recyclerview the item goes.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mTypeView.setText(mValues.get(position).type.toString());
        if (mValues.get(position).type.equals(ListType.ITEM)) {
            holder.mIconView.setImageResource(App.getItemImage(mValues.get(position).name));
        } else if (mValues.get(position).type.equals(ListType.MONSTER)) {
            holder.mIconView.setImageResource(App.getMonsterImage(mValues.get(position).name));
        } else if (mValues.get(position).type.equals(ListType.AILMENT)) {
            holder.mIconView.setImageResource(App.getAilmentImage(AilmentType.valueOf(mValues.get(position).icon)));
        }

        //Set the listener for the item so that the details fragment can be opened.
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onQuickSearchListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    /**
     * Gets the number of items in the recyclerview.
     * @return The number of items.
     */
    @Override
    public int getItemCount() {
        return mValues.size();
    }

    /**
     * The item 'frame' or viewholder that is put inside the recyclerview for each item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mTypeView;
        public final ImageView mIconView;
        public QuickSearchContent.QuickSearchItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.name);
            mTypeView = view.findViewById(R.id.type);
            mIconView = view.findViewById(R.id.icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
