package ch.safe.safedrive.ui.hitchhiker.request;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.android.gms.games.PlayerLevelInfo;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.safe.safedrive.R;
import ch.safe.safedrive.ui.login.LoginActivity;

public class HitchhikerActivity extends AppCompatActivity
        implements CreateRequest.OnFragmentInteractionListener, MyTrip.OnFragmentInteractionListener,
        DestinationReached_GoodBad.OnFragmentInteractionListener, DestinationReached_BadTrip.OnFragmentInteractionListener,
        DestinationReached_End.OnFragmentInteractionListener, SecurityWarning.OnFragmentInteractionListener, SecurityWarning_Admin.OnFragmentInteractionListener{

    // store the fragments
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // get the current date
    private String date_n = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

    // check if the hitchhiker is on trip
    private boolean isHitchhikerOnTrip = false;

    // Drawer Layout
    private DrawerLayout mDrawerLayout;

    // authentification for the user
    private FirebaseAuth auth;

    // setter for hitchhiker
    public void setHitchhikerOnTrip(boolean hitchhikerOnTrip) {
        isHitchhikerOnTrip = hitchhikerOnTrip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitchhiker);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // get the view for the drawer
        mDrawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {

                        // if the hitchhiker is not on trip, he can log out
                        if (isHitchhikerOnTrip == false){

                            // set item as selected to persist highlight
                            menuItem.setChecked(true);

                            // close drawer when item is tapped
                            mDrawerLayout.closeDrawers();

                            // hitchhiker logout
                            finish();
                            auth.signOut();

                            // go back to login activity
                            Intent intent = new Intent(HitchhikerActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(HitchhikerActivity.this, "you cannot logout, You are on trip", Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
        );

        // create the fragment create request
        CreateRequest cr = CreateRequest.newInstance(date_n);

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, cr).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
