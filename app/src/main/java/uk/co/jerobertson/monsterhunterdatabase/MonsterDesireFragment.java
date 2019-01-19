package uk.co.jerobertson.monsterhunterdatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import uk.co.jerobertson.monsterhunterdatabase.data.Material;
import uk.co.jerobertson.monsterhunterdatabase.data.Monster;
import uk.co.jerobertson.monsterhunterdatabase.data.RankType;
import uk.co.jerobertson.monsterhunterdatabase.data.TagType;

/**
 * The fragment class for the desire sensor.
 *
 * @author James Robertson
 */
public class MonsterDesireFragment extends Fragment {
    public static String ARG_MONSTER_NAME = "monster";

    private MonsterDesireFragment thisFragment = this;
    private String monsterName;
    private Monster monster;
    private RankType currentRank = RankType.LOW;
    private int currentTab = 0;
    private Map<String, Integer> itemsDesired = new HashMap<>(); //item name to amount wanted.
    private Map<TagType, Map<String, Integer>> methods = new HashMap<>(); //obtain method to a map of part names ("" for null part) to number of attempts.

    private GraphGeneration graphGeneration = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Fragments require empty public constructors.
     */
    public MonsterDesireFragment() {
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
     * available. Also sets the current tab to the previously opened tab.
     */
    @SuppressWarnings("ConstantConditions") //We know that the action bar won't be null.
    @Override
    public void onResume() {
        super.onResume();
        SpannableString s = new SpannableString("Desire Sensor");
        s.setSpan(new TypefaceSpan(App.getContext(), "imperator_small_caps.ttf"), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(s);
        ((MainActivity)getActivity()).inMenu = false;

        final TabHost host = this.getView().findViewById(R.id.item_tab);
        host.setCurrentTab(currentTab);
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
        final View view = inflater.inflate(R.layout.fragment_monster_desire, container, false);

        //Set title/name
        TextView name = view.findViewById(R.id.name);
        name.setText(monsterName);

        //Set image/icon.
        ImageView icon = view.findViewById(R.id.icon);
        icon.setImageResource(App.getMonsterImage(monsterName));

        //Set aka
        TextView aka = view.findViewById(R.id.aka);
        aka.setText(monster.getAKA());

        //Set up progress bar listener so the user can cancel the simulation if they want.
        ProgressBar progressBar = view.findViewById(R.id.simulation_progress);
        progressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (graphGeneration != null) graphGeneration.cancel(true);
            }
        });

        //Create the table of materials the monster drops at certain ranks.
        createItemTables(view, R.id.item_table_low, RankType.LOW);
        createItemTables(view, R.id.item_table_high, RankType.HIGH);
        createItemTables(view, R.id.item_table_g, RankType.G);

        //Set up the tabhost to show each item table under a different tab.
        final TabHost host = view.findViewById(R.id.item_tab);
        host.setup();
        TabHost.TabSpec spec = host.newTabSpec("Low");
        spec.setContent(R.id.item_table_low);
        spec.setIndicator("Low");
        host.addTab(spec);
        spec = host.newTabSpec("High");
        spec.setContent(R.id.item_table_high);
        spec.setIndicator("High");
        host.addTab(spec);
        spec = host.newTabSpec("G");
        spec.setContent(R.id.item_table_g);
        spec.setIndicator("G");
        host.addTab(spec);
        host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            //If the tab changes, we should replace the currentRank, and clear the methods and desired items variables.
            @Override
            public void onTabChanged(String s) {
                if (graphGeneration != null) graphGeneration.cancel(true);
                currentRank = RankType.valueOf(s.toUpperCase());
                itemsDesired.clear();
                methods.clear();
                createPartTable(view);
                currentTab = host.getCurrentTab(); //Remember the current tab so we can go back to it if we re-create the fragment.
            }
        });

        //Create the table of ways to gather materials.
        createPartTable(view);

        //Set up the chart and add some dummy data.
        setUpChart(view);

        return view;
    }

    /**
     * Items can be obtained via different methods; this generates a list of all the methods
     * available to the user.
     * @param view The view which contains the table to update the contents of.
     */
    private void createPartTable(View view) {
        TableLayout partTable = view.findViewById(R.id.parts_table);
        partTable.removeAllViews();
        if (App.getMonsterMaterials(monsterName, currentRank) != null) { //Make sure this monster actually has some materials to get in this rank.
            Map<TagType, List<String>> addedTags = new HashMap<>();
            for (Material m : App.getMonsterMaterials(monsterName, currentRank)) {
                if (!addedTags.containsKey(m.getOBTAIN_BY())) { //Check that this method hasn't already been added to the list
                    addedTags.put(m.getOBTAIN_BY(), new ArrayList<String>());
                } else if (m.getBODY_PART() == null || m.getBODY_PART().equals("")) { //If this material doesn't have a bodypart then skip it.
                    continue;
                } else if (addedTags.get(m.getOBTAIN_BY()).contains(m.getBODY_PART())) { //If this material is already in the list then skip it.
                    continue;
                }
                addedTags.get(m.getOBTAIN_BY()).add(m.getBODY_PART()); //Add the method to the list so that we don't try to add it again later.

                TableRow materialRow = (TableRow) getLayoutInflater().inflate(R.layout.table_items, partTable, false);

                materialRow.findViewById(R.id.icon).setVisibility(View.GONE);

                //If the method doesn't have a bodypart, just show the obtain method, otherwise include the bodypart in the displayed text.
                if (m.getBODY_PART() == null) {
                    ((TextView)materialRow.findViewById(R.id.name)).setText(m.getOBTAIN_BY().toString());
                    ((TextView)materialRow.findViewById(R.id.var_type)).setText(m.getOBTAIN_BY().name());
                } else {
                    ((TextView)materialRow.findViewById(R.id.name)).setText(String.format("%s: %s", m.getOBTAIN_BY().toString(), m.getBODY_PART()));
                    ((TextView)materialRow.findViewById(R.id.var_type)).setText(m.getOBTAIN_BY().name());
                    ((TextView)materialRow.findViewById(R.id.var_part)).setText(m.getBODY_PART());
                }

                //We just want the styling of the edittext, we don't actually want the user to edit it (and the built in way to do this in Android doesn't work).
                ((EditText)materialRow.findViewById(R.id.count)).setTransformationMethod(null);
                ((EditText)materialRow.findViewById(R.id.count)).setKeyListener(null);

                //If the user clicks the plus button, add 1 attempt to this method and generate the graph.
                materialRow.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n") //Just inserting a number, no need for formatting.
                    @Override
                    public void onClick(View view) {
                        TableRow parent = (TableRow) view.getParent();
                        String count = ((EditText)parent.findViewById(R.id.count)).getText().toString();

                        if (count.equals("")) count = "0"; //Assume an empty text field is just 0 attempts.
                        Integer countInt = Integer.valueOf(count);
                        if (countInt == 99) return; //Don't allow for more than a double digit number of items.
                        ((EditText)parent.findViewById(R.id.count)).setText(Integer.toString(countInt + 1)); //Update the number in the edittext.

                        TagType type = TagType.valueOf(((TextView)parent.findViewById(R.id.var_type)).getText().toString());
                        String part = ((TextView)parent.findViewById(R.id.var_part)).getText().toString();

                        //If the method hasn't yet been added to the attempted methods list, add it. Then increase its attempt count by 1.
                        if (!methods.containsKey(type)) methods.put(type, new HashMap<String, Integer>());
                        methods.get(type).put(part, countInt + 1);

                        generateGraph(); //The methods have been updated, so we need to redraw the graph.
                    }
                });

                //If the user clicks the minus button, remove 1 attempt from this method and generate the graph.
                materialRow.findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n") //Just inserting a number, no need for formatting.
                    @Override
                    public void onClick(View view) {
                        TableRow parent = (TableRow) view.getParent();
                        String count = ((EditText)parent.findViewById(R.id.count)).getText().toString();

                        if (count.equals("")) count = "0"; //Assume an empty text field is just 0 attempts.
                        Integer countInt = Integer.valueOf(count);
                        if (countInt == 0) return; //Don't allow for less than 0 items.
                        ((EditText)parent.findViewById(R.id.count)).setText(Integer.toString(countInt - 1)); //Update the number in the edittext.

                        TagType type = TagType.valueOf(((TextView)parent.findViewById(R.id.var_type)).getText().toString());
                        String part = ((TextView)parent.findViewById(R.id.var_part)).getText().toString();

                        //If the method hasn't yet been added to the attempted methods list, add it. Then decrease its attempt count by 1.
                        if (!methods.containsKey(type)) methods.put(type, new HashMap<String, Integer>());
                        methods.get(type).put(part, countInt - 1);

                        generateGraph(); //The methods have been updated, so we need to redraw the graph.
                    }
                });

                partTable.addView(materialRow);
            }
        } else { //The monster has no materials, so there are no ways to obtain them, so hide the parts table.
            view.findViewById(R.id.parts_table).setVisibility(View.GONE);
        }
    }

    private void createItemTables(View view, int table, RankType rankType) {
        TableLayout itemTable = view.findViewById(table);
        if (App.getMonsterMaterials(monsterName, rankType) != null) {
            Set<String> addedMaterials = new HashSet<>();
            for (Material m : App.getMonsterMaterials(monsterName, rankType)) { //Make sure the monster actually has some materials at this rank.
                if (addedMaterials.contains(m.getITEM_NAME())) continue; //Skip this material if we've already added its item to the list.
                addedMaterials.add(m.getITEM_NAME());

                TableRow materialRow = (TableRow) getLayoutInflater().inflate(R.layout.table_items, itemTable, false);

                ((ImageView)materialRow.findViewById(R.id.icon)).setImageResource(App.getItemImage(m.getITEM_NAME()));
                materialRow.findViewById(R.id.icon).setOnClickListener(switchItems(m.getITEM_NAME()));

                ((TextView)materialRow.findViewById(R.id.name)).setText(m.getITEM_NAME());
                ((TextView)materialRow.findViewById(R.id.var_type)).setText(m.getITEM_NAME());

                ((EditText)materialRow.findViewById(R.id.count)).setTransformationMethod(null);
                ((EditText)materialRow.findViewById(R.id.count)).setKeyListener(null);

                //If the user clicks the plus button, add 1 to the number of desired items for this item and generate the graph.
                materialRow.findViewById(R.id.plus).setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n") //Just inserting a number, no need for formatting.
                    @Override
                    public void onClick(View view) {
                        TableRow parent = (TableRow) view.getParent();
                        String count = ((EditText)parent.findViewById(R.id.count)).getText().toString();

                        if (count.equals("")) count = "0"; //Assume an empty text field is just 0 desired items.
                        Integer countInt = Integer.valueOf(count);
                        if (countInt == 99) return; //Don't allow for more than a double digit number of desired items.
                        ((EditText)parent.findViewById(R.id.count)).setText(Integer.toString(countInt + 1));

                        //If the item hasn't yet been added to the desired items list, add it. Then increase its amount by 1.
                        String item = ((TextView)parent.findViewById(R.id.var_type)).getText().toString();
                        itemsDesired.put(item, countInt + 1);

                        generateGraph(); //The desired items has changed, so re-draw the graph.
                    }
                });

                //If the user clicks the minus button, minus 1 to the number of desired items for this item and generate the graph.
                materialRow.findViewById(R.id.minus).setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n") //Just inserting a number, no need for formatting.
                    @Override
                    public void onClick(View view) {
                        TableRow parent = (TableRow) view.getParent();
                        String count = ((EditText)parent.findViewById(R.id.count)).getText().toString();

                        if (count.equals("")) count = "0"; //Assume an empty text field is just 0 desired items.
                        Integer countInt = Integer.valueOf(count);
                        if (countInt == 0) return; //Don't allow for less than 0 items.
                        ((EditText)parent.findViewById(R.id.count)).setText(Integer.toString(countInt - 1));

                        //If the items hasn't yet been added to the desired items list, add it. Then decrease its amount by 1.
                        String item = ((TextView)parent.findViewById(R.id.var_type)).getText().toString();
                        itemsDesired.put(item, countInt - 1);

                        generateGraph(); //The desired items has changed, so re-draw the graph.
                    }
                });

                itemTable.addView(materialRow);
            }
        } else { //The monster has no materials, so there are no items to obtain, so hide the items table.
            view.findViewById(table).setVisibility(View.GONE);
        }
    }

    /**
     * Call this method to re-draw the graph. It cancels any previously existing async-task to draw
     * the graph and creates a new async-task.
     */
    private void generateGraph() {
        if (graphGeneration != null) graphGeneration.cancel(true);
        graphGeneration = new GraphGeneration(thisFragment);
        graphGeneration.execute();
    }

    /**
     * The default chart view needs some styling and initial setup - this method handles that.
     * @param view The view which contains the chart.
     */
    private void setUpChart(View view) {
        LineChart chart = view.findViewById(R.id.desire_chart);

        chart.getPaint(Chart.PAINT_INFO).setColor(getResources().getColor(R.color.colorAccent)); //Set the font colour to the app's accent.
        chart.getPaint(Chart.PAINT_INFO).setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/quicksand_regular.ttf")); //Set the chart's colour to the app's font.

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0)); //Add a dummy entry so it doesn't die horribly.
        LineDataSet dataSet = new LineDataSet(entries, "Results");
        LineData lineData = new LineData(dataSet);

        chart.getLegend().setEnabled(false);
        chart.setData(lineData);
        chart.setDescription(null);

        chart.getXAxis().setAxisMinimum(1);
        chart.getXAxis().setAxisMaximum(20);
        chart.getXAxis().setLabelCount(20, true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/quicksand_regular.ttf"));

        chart.getAxisRight().setEnabled(false); //Don't want a right axis.

        chart.getAxisLeft().setAxisMinimum(0);
        chart.getAxisLeft().setAxisMaximum(100);
        chart.getAxisLeft().setLabelCount(11, true);
        chart.getAxisLeft().setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        chart.getAxisLeft().setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/quicksand_regular.ttf"));

        chart.setScaleEnabled(false); //No need for scaling, the chart displays all the data and this isn't actually in any way useful.

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    /**
     * The async tasks that simulates all the selected methods and produces results.
     * Each method is simulated 10,000 times, with each simulation determining whether or not the
     * simulation has all of the desired items. The end result is a % of the time that the
     * simulation has got all of the desired items. This is stacked cumulatively for up to 20
     * hunts.
     *
     * Rounds was determined to be 10,000 as this gathers reliable results within 3 seconds of 6
     * year old hardware, and completes within 30 seconds (which can be cancelled, which is not
     * important as the user can still interact with the app in this time).
     *
     * To avoid memory leaks, this method is static and weakly references the fragment. This is
     * better programming practice than every example on the official Android development website...
     * C'mon Android mate, you can do better than that.
     */
    private static class GraphGeneration extends AsyncTask<Void, LineData, LineData> {
        private WeakReference<MonsterDesireFragment> fragmentWeakReference;
        private final int rounds = 10000; //Number of simulations.
        private final int hunts = 20; //Number of cumulative hunts.
        private Random rand = new Random();

        /**
         * On creation of this class, we need to weakly reference the fragment to pull data from it.
         * Almost all methods will check that this fragment is not null as their first line, which
         * looks stupid but is the correct thing to do.
         * @param context The fragment context to pull data from.
         */
        GraphGeneration(MonsterDesireFragment context) {
            fragmentWeakReference = new WeakReference<>(context);
        }

        /**
         * While simulating, a progress bar is shown to demonstrate how far through the simulation
         * we are. This is shown and hidden at the start and end of simulations.
         * @param visibility The visibility to set the progress bar to.
         */
        @SuppressWarnings("ConstantConditions") //We know the views are not null as we check for this in the first line.
        private void changeProgressBarVisibility(int visibility) {
            if (fragmentWeakReference.get().getView() == null) return;

            ProgressBar progressBar = fragmentWeakReference.get().getView().findViewById(R.id.simulation_progress);
            if (progressBar.getVisibility() != visibility) { //Check we're not calling this from a cancelled async task.
                LineChart chart = fragmentWeakReference.get().getView().findViewById(R.id.desire_chart);
                LineChart.LayoutParams params = chart.getLayoutParams();
                params.height = (visibility == View.VISIBLE)
                        ? params.height - progressBar.getLayoutParams().height //Make the chart smaller to make room for the progress bar. It makes the layout less jumpy and prettier.
                        : params.height + progressBar.getLayoutParams().height; //make the chart larger to accomodate the missing progress bar. ^
                chart.setLayoutParams(params);
            }
            progressBar.setProgress(0);
            progressBar.setVisibility(visibility);
        }

        /**
         * This method takes in the amount of successes against the amount of attempts, determines
         * a percentage success for each number of hunts and then converts this data into data that
         * can be added to a chart to be displayed.
         * @param successMap The map of hunts to successes.
         * @param currentRounds The amount of attempts that have been made on the success map.
         * @return The entries to display on the map.
         */
        private LineData generateLineData(final SparseIntArray successMap, final int currentRounds) {
            List<Entry> entries = new ArrayList<>();

            //We want the x-axis to start at 1, so add 0s to all the attempts before the first success.
            for (int i = 1; i < successMap.keyAt(0); i++) {
                entries.add(new Entry(i, 0));
            }

            //Create an entry for each success mapping.
            for (int i = 0; i < successMap.size(); i++) {
                int hunt = successMap.keyAt(i);
                int chance = (int)Math.round(((double)successMap.valueAt(i)) / currentRounds * 100);
                entries.add(new Entry(hunt, chance));
                if (chance > 96) break; //Don't bother showing anything beyond a value above 96, it'll clutter the graph.
            }

            if (entries.size() == 0) entries.add(new Entry(0,0)); //If we have no success, add a dummy entry so the whole library doesn't crash.

            //Now we have the data, we need to style it to look like part of the map.
            LineDataSet dataSet = new LineDataSet(entries, "Results");
            dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER); //Curved rather than straight lines between points.
            dataSet.setValueTextSize(App.getPxFromDp(6)); //Larger style text that's more readable.
            dataSet.setValueFormatter(new IValueFormatter() { //Remove the trailing ".0" from the labels.
                @Override
                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                    return Integer.toString((int)value);
                }
            });

            //These stylings all require assets and resources, so we have to check for it's continued existance first.
            if (fragmentWeakReference.get().getActivity() != null) {
                dataSet.setValueTypeface(Typeface.createFromAsset(fragmentWeakReference.get().getActivity().getAssets(), "fonts/quicksand_regular.ttf"));
                dataSet.setColor(fragmentWeakReference.get().getResources().getColor(R.color.colorPrimary));
                dataSet.setFillColor(fragmentWeakReference.get().getResources().getColor(R.color.colorPrimary));
                dataSet.setCircleColor(fragmentWeakReference.get().getResources().getColor(R.color.colorAccent));
                dataSet.setCircleColorHole(fragmentWeakReference.get().getResources().getColor(R.color.colorAccent));
                dataSet.setHighLightColor(fragmentWeakReference.get().getResources().getColor(R.color.colorAccent));
                dataSet.setDrawFilled(true); //Display colour beneath the line.
            }
            return new LineData(dataSet);
        }

        /**
         * This method must be called on the main UI thread. It replaces the existing chart data
         * with new data, sets up the axis and then informs the chart it needs re-drawing.
         * @param lineData The new data to update the chart with.
         */
        @SuppressWarnings("ConstantConditions") //We already check that the view still exists, so no further checks are required.
        private void updateChart(LineData lineData) {
            if (fragmentWeakReference.get().getView() == null) return;

            LineChart chart = fragmentWeakReference.get().getView().findViewById(R.id.desire_chart);
            chart.setData(lineData);
            chart.getXAxis().setAxisMinimum(1);
            chart.getXAxis().setAxisMaximum(lineData.getEntryCount()); //Max X value is the max hunt in the line data.
            chart.getXAxis().setLabelCount(lineData.getEntryCount(), true); //Force a label for each hunt.

            chart.notifyDataSetChanged();
            chart.invalidate();
        }

        /**
         * Before starting the simulation, we should display the progress bar so we can show the
         * user the progress being made.
         */
        @Override
        protected void onPreExecute() {
            if (fragmentWeakReference.get().getView() == null) return;

            changeProgressBarVisibility(View.VISIBLE);
        }

        /**
         * This is where the simulation (read insanity) occurs.
         *
         * This method first pulls the required data from the fragment, and then simulates
         * collecting items from the monster 10,000 times for each number of hunts (20) for a total
         * of 200,000 simulations. This was determined to be the smallest reliable number to within
         * a percent that doesn't seem too slow on old devices (see class description for more
         * info).
         *
         * Each simulation either results in a success or a failure, which we can take as an average
         * to calculate the probability of success. These results are published every 100
         * simulations to the UI thread.
         *
         * @param voids Yes this is best practice... and yes this is literally a list of nothings...
         * @return The simulated results converted to chart line data.
         */
        @Override
        protected LineData doInBackground(Void... voids) {
            Map<TagType, Map<String, Integer>> classMethods = fragmentWeakReference.get().methods;
            Map<String, Integer> classItemsDesired = fragmentWeakReference.get().itemsDesired;

            SparseIntArray successMap = new SparseIntArray(); //A map of hunt number against number of successes.
            for (int r = 0; r < rounds; r++) { //For every round...
                if (r % (rounds / 100) == 0) publishProgress(generateLineData(successMap, r)); //Publish our progress every 1% of the simulation.

                List<Map<String, Integer>> cumulativeItemMap = new ArrayList<>(); //A list whose index is the hunt number for each item to the number of times it was selected.
                for (int h = 0; h < hunts; h++) { //We simulate a number of hunts for a cumulative probability.
                    if (fragmentWeakReference.get().getActivity() == null) return null; //If we've left the fragment, just give up.
                    if (isCancelled()) return null; //If the task has been cancelled, just give up.

                    Map<String, Integer> selectedItemMap = new HashMap<>(); //A map of selected items to the number of times they are selected.

                    for (TagType type : classMethods.keySet()) {
                        for (Map.Entry<String, Integer> partEntry : classMethods.get(type).entrySet()) { //This acts as every chance to obtain something
                            String monsterName = fragmentWeakReference.get().monsterName;
                            RankType currentRank = fragmentWeakReference.get().currentRank;
                            int lastChance = 0; //We need to assign a number to each material so we can pick only one and randomly.

                            Map<Material, Integer> materialChanceMap = new LinkedHashMap<>(); //Map of materials to their chance number.
                            for (Material m : App.getMonsterMaterials(monsterName, currentRank)) { //We get all the possible materials for this monster at this rank...
                                if (m.getOBTAIN_BY().equals(type)) {
                                    if (partEntry.getKey().equals("") && m.getBODY_PART() == null ||
                                            partEntry.getKey().equals(m.getBODY_PART())) { //And if the material matches the currently iterating obtain type, add it to the chance map to be picked from.
                                        materialChanceMap.put(m, m.getOBTAIN_CHANCE() + lastChance);
                                        lastChance += m.getOBTAIN_CHANCE();
                                    }
                                }
                            }
                            for (int i = 0; i < partEntry.getValue(); i++) { //Carving twice? Loop twice.
                                int selection = rand.nextInt(lastChance) + 1;
                                for (Map.Entry<Material, Integer> entry : materialChanceMap.entrySet()) { //Now we iterate through every item we can possibly pick, pick one, and add it the master list.
                                    if (entry.getValue() >= selection) {
                                        if (!selectedItemMap.containsKey(entry.getKey().getITEM_NAME())) selectedItemMap.put(entry.getKey().getITEM_NAME(), 0);
                                        selectedItemMap.put(entry.getKey().getITEM_NAME(), selectedItemMap.get(entry.getKey().getITEM_NAME()) + entry.getKey().getCOUNT());
                                        break; //We only get to select one item each iteration, so break out.
                                    }
                                }
                            }
                        }
                    }
                    cumulativeItemMap.add(selectedItemMap);
                    if (cumulativeItemMap.size() > 1) { //If this is at least the second entry, we need to add the results from the previous entry to it.
                        Map<String, Integer> previousMap = cumulativeItemMap.get(h - 1);
                        for (Map.Entry<String, Integer> previousEntry : previousMap.entrySet()) {
                            if (cumulativeItemMap.get(h).containsKey(previousEntry.getKey())) { //If the previous entry already has this item, combine the total scores.
                                cumulativeItemMap.get(h).put(previousEntry.getKey(), cumulativeItemMap.get(h).get(previousEntry.getKey()) + previousEntry.getValue());
                            } else { //The previous entry did not have the item, so just add this entry's score.
                                cumulativeItemMap.get(h).put(previousEntry.getKey(), previousEntry.getValue());
                            }
                        }
                    }
                }

                //Now we have a cumulative entry for each hunt, we should iterate through each one and see if the cumulative hunt was a success.
                for (int h = 0; h < cumulativeItemMap.size(); h++) {
                    Map<String, Integer> huntMap = cumulativeItemMap.get(h);
                    boolean failed = false;

                    for (Map.Entry<String, Integer> desiredEntry : classItemsDesired.entrySet()) {
                        if (desiredEntry.getValue() != 0 && //If we actually desired the item and...
                                (!huntMap.containsKey(desiredEntry.getKey()) || //...if the map for this hunt doesn't have the entry or...
                                huntMap.get(desiredEntry.getKey()) < desiredEntry.getValue())) { //...if the hunt map has less than the desire items.
                            failed = true; //Then we've failed this hunt.
                            break; //Failing one item means failing them all, so break out and just move on.
                        }
                    }

                    if (!failed) successMap.put(h + 1, successMap.get(h + 1) + 1); //If we haven't failed, increment the success counter for this hunt number.
                }
            }

            //The success map now contains the number of successful times the hunt number got the required items, so we can generate a probability using this.
            return generateLineData(successMap, rounds);
        }

        /**
         * Every 1% of simulation, we update the progress bar and the chart for the user.
         * @param lineDatas The linedata to update the chart with.
         */
        @SuppressWarnings("ConstantConditions") //We've already checked the view isn't null, so we know we'll find the progress bar.
        @Override
        protected void onProgressUpdate(LineData... lineDatas) {
            if (fragmentWeakReference.get().getView() == null) return;

            LineData lineData = lineDatas[0];

            ProgressBar progressBar = fragmentWeakReference.get().getView().findViewById(R.id.simulation_progress);
            progressBar.incrementProgressBy(1);

            updateChart(lineData);
        }

        /**
         * When the simulation has finished, we can update the chart a final time and hide the
         * progress bar.
         * @param lineData The linedata to update the chart with.
         */
        @Override
        protected void onPostExecute(LineData lineData) {
            if (fragmentWeakReference.get().getView() == null) return;

            changeProgressBarVisibility(View.GONE);

            updateChart(lineData);
        }

        /**
         * If the simulation is cancelled, the progress bar should be hidden. However, we check the
         * fragment to see if another simulation has taken this task's place, and if it has we
         * should choose not to hide the progress bar instead.
         */
        @Override
        protected void onCancelled() {
            if (fragmentWeakReference.get().getView() == null) return;

            if (!fragmentWeakReference.get().graphGeneration.isCancelled()) return;

            changeProgressBarVisibility(View.GONE);
        }
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
     * Hides the keyboard when the user navigates away from the fragment.
     */
    @Override
    public void onPause() {
        InputMethodManager imm = (InputMethodManager)App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && this.getView() != null) {
            imm.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
        }
        super.onPause();
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
}
