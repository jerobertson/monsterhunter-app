package uk.co.jerobertson.monsterhunterdatabase;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import uk.co.jerobertson.monsterhunterdatabase.MonsterListFragment.OnListFragmentInteractionListener;

/**
 * Handles the recyclerview for the monster list.
 *
 * @author James Robertson
 */
public class MonsterListRecyclerViewAdapter extends RecyclerView.Adapter<MonsterListRecyclerViewAdapter.ViewHolder> {
    private final List<MonsterContent.MonsterItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    /**
     * Sets up the recyclerview with a list of items and an interaction listener.
     * @param items The items to display in the recyclerview.
     * @param listener The listener to use when an item is selected.
     */
    @SuppressWarnings("WeakerAccess")
    public MonsterListRecyclerViewAdapter(List<MonsterContent.MonsterItem> items, OnListFragmentInteractionListener listener) {
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
                .inflate(R.layout.fragment_monster, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Sets the text and description of the created viewholders to display in the recyclerview.
     * @param holder The 'frame' of a monster whose content needs to be filled out.
     * @param position The position in the recyclerview the item goes.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mAkaView.setText(mValues.get(position).aka);
        holder.mIconView.setImageResource(App.getMonsterImage(mValues.get(position).name));

        //Set the listener for the item so that the monster details can be opened.
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onMonsterListFragmentInteraction(holder.mItem);
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
     * The item 'frame' or viewholder that is put inside the recyclerview for each monster.
     */
    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mNameView;
        public final TextView mAkaView;
        public final ImageView mIconView;
        public MonsterContent.MonsterItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mNameView = view.findViewById(R.id.name);
            mAkaView = view.findViewById(R.id.aka);
            mIconView = view.findViewById(R.id.icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
