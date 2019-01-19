package uk.co.jerobertson.monsterhunterdatabase;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;

/**
 * A dialog fragment to display a message asking the user if they want to close the app.
 * If they do, it uses finishAffinity to correctly minimise to be handled by the OS, if not, it
 * closes the navdrawer so the user can keep using the app.
 */
public class ExitFragment extends DialogFragment {
    /**
     * Creates the dialog and listeners for the app close menu.
     * @param savedInstanceState A saved instance of this fragment.
     * @return The created dialog.
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Return to camp?")
                .setPositiveButton("Exit app", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getActivity().finishAffinity();
                    }
                })
                .setNegativeButton("Not yet!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                        drawer.closeDrawer(GravityCompat.START);
                    }
                });
        return builder.create();
    }
}
