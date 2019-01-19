package uk.co.jerobertson.monsterhunterdatabase;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import java.lang.reflect.Field;

import uk.co.jerobertson.monsterhunterdatabase.data.AilmentType;

/**
 * The main activity where everything is controlled.
 * This app uses the "single activity, multiple fragments" paradigm used frequently by modern
 * developers, as it allows for a much smoother user experience, and also uses up less memory and
 * runs more efficiently.
 *
 * @author James Robertson
 */
public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        SearchView.OnQueryTextListener,
        QuickSearchListFragment.OnListFragmentInteractionListener,
        MonsterListFragment.OnListFragmentInteractionListener,
        MonsterDetailsFragment.OnFragmentInteractionListener,
        MonsterDetailsFragment.OnMonsterDetailsPassthroughListener,
        MonsterDetailsFragment.OnPhysiologySelected,
        MonsterDetailsFragment.OnDesireSelected,
        MonsterPhysiologyFragment.OnFragmentInteractionListener,
        MonsterDesireFragment.OnFragmentInteractionListener,
        ItemListFragment.OnListFragmentInteractionListener,
        ItemDetailsFragment.OnFragmentInteractionListener,
        ItemDetailsFragment.OnItemDetailsPassthroughListener,
        AilmentDetailsFragment.OnFragmentInteractionListener,
        AilmentDetailsFragment.OnAilmentDetailsPassthroughListener,
        App.DrawerLocker {

    private ActionBarDrawerToggle toggle;
    private QuickSearchListFragment quickSearchListFragment;
    private ItemListFragment itemListFragment;
    private MonsterListFragment monsterListFragment;
    private SearchView searchView;
    public boolean inMenu = true;

    /**
     * Sets up the activity by selecting a fragment to display, setting up the toolbar, and setting
     * up the navdraw.
     *
     * @param savedInstanceState A saved instance of this activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set the activity to display
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            monsterListFragment = new MonsterListFragment();
            monsterListFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, monsterListFragment).commit();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.syncState();
        drawer.openDrawer(GravityCompat.START);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);
    }

    /**
     * Forces activity to handle all intents with the new handleIntent method.
     * @param intent The new intent.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handles all intents across the whole of the app.
     * @param intent The intent to handle.
     */
    @SuppressWarnings("unused") //Placeholder for future updates.
    private void handleIntent(Intent intent) {
        /*
         * As this app follows the "single activity, multiple fragments" paradigm, this isn't really
         * used. However, it's been included as an example in-case it comes in useful down the line.
         * if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
         *     String query = intent.getStringExtra(SearchManager.QUERY);
         * }
         */
    }

    /**
     * The back button now opens the menu when on a list fragment, and prompts the user to close
     * the app if they press the back button while the menu is open. It works as normal while in a
     * details window. This gives the app a very simple to use and structured flow.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            new ExitFragment().show(getSupportFragmentManager(), "Exit App");
        } else if (inMenu){
            drawer.openDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * The default behaviour of the actionbar searchview is awful. You have to click on it multiple
     * times just to open up the keyboard, when it loses focus the keyboard sticks around, and
     * re-selecting it doesn't get the keyboard to pop up again unless you explcitly de-select then
     * reselect it! This method plays around with the settings so it's an altogether more enjoyable
     * experience for the user.
     * @param menu The actionbar.
     * @return Whether or not it has been set up successfully.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        menu.findItem(R.id.search).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                //If there's nothing in the action bar, focus and show the keyboard, otherwise just display what's in it and let the user select to modify it.
                if (searchView.getQuery().length() == 0) {
                    searchView.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                } else {
                    searchView.clearFocus();
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                return true;
            }
        });
        try { //There's literally no need for the close button... There's a back button right next to it.
            Field searchField = SearchView.class.getDeclaredField("mCloseButton");
            searchField.setAccessible(true);
            ImageView searchCloseButton = (ImageView) searchField.get(searchView);
            searchCloseButton.setEnabled(false);
            searchCloseButton.setImageDrawable(getResources().getDrawable(R.drawable.transparent));
        } catch (Exception e) { /* Don't bother doing anything if this fails. It's only a convenience thing anyway */ }

        return true;
    }

    /**
     * Handles the actionbar interaction. For now this just handles the home button.
     * @param item The menuitem that was selected.
     * @return True if it was handled successfully.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                //Gone down a rabbit-warren of item details? This button takes you straight back home!
                searchView.clearFocus();
                monsterListFragment = new MonsterListFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, monsterListFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                NavigationView navigationView = findViewById(R.id.nav_view);
                navigationView.getMenu().getItem(1).setChecked(true);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles navbar item selection.
     * @param item The navbar item that was selected.
     * @return True if the view was changed successfully.
     */
    @SuppressWarnings("StatementWithEmptyBody") //Adding empty statements for future updates.
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_quicksearch) {
            quickSearchListFragment = new QuickSearchListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, quickSearchListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        else if (id == R.id.nav_monsters) {
            monsterListFragment = new MonsterListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, monsterListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_items) {
            itemListFragment = new ItemListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, itemListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (id == R.id.nav_weapons) {
            //Coming soon! (In a future update)
        } else if (id == R.id.nav_armour) {
            //Coming soon! (In a future update)
        } else if (id == R.id.nav_skills) {
            //Coming soon! (In a future update)
        } else if (id == R.id.nav_locations) {
            //Coming soon! (In a future update)
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    /**
     * Handler for a user selecting an item in the quick-search list fragment.
     * This works out what kind of item was selected and then calls the correct method to handle it.
     * @param item The item the user selected.
     */
    @Override
    public void onQuickSearchListFragmentInteraction(QuickSearchContent.QuickSearchItem item) {
        if (item.type.equals(ListType.MONSTER)) {
            onMonsterSelected(item.name);
        } else if (item.type.equals(ListType.ITEM)) {
            onItemSelected(item.name);
        } else if (item.type.equals(ListType.AILMENT)) {
            onAilmentSelected(AilmentType.valueOf(item.icon));
        }
    }

    /**
     * Handler for a user selecting an item in the monster list fragment. Calls the relevant method
     * to handle it.
     * @param item The monster that the user selected.
     */
    @Override
    public void onMonsterListFragmentInteraction(MonsterContent.MonsterItem item) {
        onMonsterSelected(item.name);
    }

    /**
     * Creates the monster details fragment transaction.
     * @param name The name of the monster to display.
     */
    @Override
    public void onMonsterSelected(String name) {
        if (App.getMonster(name) == null) return;
        searchView.clearFocus();
        MonsterDetailsFragment monsterDetailsFragment = new MonsterDetailsFragment();
        Bundle args = new Bundle();
        args.putString(MonsterDetailsFragment.ARG_MOSTER_NAME, name);
        monsterDetailsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, monsterDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Handler for a user selecting an item in the item list fragment. Calls the relevant method to
     * handle it.
     * @param item The item that the user selected.
     */
    @Override
    public void onItemListFragmentInteraction(ItemContent.ItemItem item) {
        onItemSelected(item.name);
    }

    /**
     * Creates the item details fragment transaction.
     * @param name The item that the user selected.
     */
    @Override
    public void onItemSelected(String name) {
        if (App.getItem(name) == null) return;
        searchView.clearFocus();
        ItemDetailsFragment itemDetailsFragment = new ItemDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ItemDetailsFragment.ARG_ITEM_NAME, name);
        itemDetailsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, itemDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Creates the ailment details fragment transaction.
     * @param ailmentType The type of ailment the user selected.
     */
    @Override
    public void onAilmentSelected(AilmentType ailmentType) {
        if (App.getAilment(ailmentType) == null) return;
        searchView.clearFocus();
        AilmentDetailsFragment ailmentDetailsFragment = new AilmentDetailsFragment();
        Bundle args = new Bundle();
        args.putString(AilmentDetailsFragment.ARG_AILMENT_NAME, ailmentType.name());
        ailmentDetailsFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, ailmentDetailsFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Creates the monster physiology fragment transaction.
     * @param name The name of the monster currently being viewed.
     */
    @Override
    public void onPhysiologySelected(final String name) {
        MonsterPhysiologyFragment monsterPhysiologyFragment = new MonsterPhysiologyFragment();
        Bundle args = new Bundle();
        args.putString(MonsterPhysiologyFragment.ARG_MONSTER_NAME, name);
        monsterPhysiologyFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, monsterPhysiologyFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Creates the monster desire sensor fragment transaction.
     * @param name The name of the monster currently being viewed.
     */
    @Override
    public void onDesireSelected(final String name) {
        MonsterDesireFragment monsterDesireFragment = new MonsterDesireFragment();
        Bundle args = new Bundle();
        args.putString(MonsterPhysiologyFragment.ARG_MONSTER_NAME, name);
        monsterDesireFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, monsterDesireFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    /**
     * Placeholder method that is currently unused but may be useful in future updates.
     * @param uri Currently unused.
     */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * As the item lists are updated instantly rather than after the user presses search, this is
     * an empty method that simply says "it succeeded" but doesn't do anything. Necessary when
     * overwriting the terrible default functionality.
     * @param s The string the user typed.
     * @return Always true.
     */
    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    /**
     * When the user updates the text in the searchview, this method is called. It works out the
     * currently loaded list of items (items, monsters, or quick-search etc) and updates the
     * relevant recyclerview.
     * @param s The current string in the searchview.
     * @return Always true (to say that the lists have been updated).
     */
    @Override
    public boolean onQueryTextChange(String s) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        String loadedClass = fragmentManager.findFragmentById(R.id.fragment_container).getClass().toString();
        String simpleClass = loadedClass.substring(loadedClass.lastIndexOf(".") + 1);

        if (simpleClass.equals(QuickSearchListFragment.class.getSimpleName())) {
            quickSearchListFragment.setPreviousSearch(s);
            RecyclerView rView = quickSearchListFragment.getRecyclerView();
            QuickSearchListRecyclerViewAdapter adapter = new QuickSearchListRecyclerViewAdapter(new QuickSearchContent(s).ITEMS, this);
            rView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (simpleClass.equals(MonsterListFragment.class.getSimpleName())) {
            monsterListFragment.setPreviousSearch(s);
            RecyclerView rView = monsterListFragment.getRecyclerView();
            MonsterListRecyclerViewAdapter adapter = new MonsterListRecyclerViewAdapter(new MonsterContent(s).ITEMS, this);
            rView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else if (simpleClass.equals(ItemListFragment.class.getSimpleName())) {
            itemListFragment.setPreviousSearch(s);
            RecyclerView rView = itemListFragment.getRecyclerView();
            ItemListRecyclerViewAdapter adapter = new ItemListRecyclerViewAdapter(new ItemContent(s).ITEMS, this);
            rView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    /**
     * Enables/disables the navdrawer dependant on whether a details fragment is open. This allows
     * a user to go 'back' rather than open the navdrawer every time.
     * @param enabled Whether the drawer should be enabled or not.
     */
    @SuppressWarnings("ConstantConditions") //We know that the action bar won't be null.
    @Override
    public void setDrawerEnabled(boolean enabled) {
        if (enabled) getSupportActionBar().setDisplayHomeAsUpEnabled(false); //The order is important.

        int lockMode = enabled ? DrawerLayout.LOCK_MODE_UNLOCKED :
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED;
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(lockMode);
        toggle.setDrawerIndicatorEnabled(enabled);
        if (enabled) {
            toggle.setToolbarNavigationClickListener(null);
        } else {
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (!enabled) getSupportActionBar().setDisplayHomeAsUpEnabled(true); //The order is important.
    }
}
