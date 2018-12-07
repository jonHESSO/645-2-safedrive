package ch.safe.safedrive.ui.hitchhiker.request;

import android.app.Fragment;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.android.gms.games.PlayerLevelInfo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.safe.safedrive.R;

public class HitchhikerActivity extends AppCompatActivity
        implements CreateRequest.OnFragmentInteractionListener, MyTrip.OnFragmentInteractionListener,
        DestinationReached_GoodBad.OnFragmentInteractionListener, DestinationReached_BadTrip.OnFragmentInteractionListener,
        DestinationReached_End.OnFragmentInteractionListener, SecurityWarning.OnFragmentInteractionListener, SecurityWarning_Admin.OnFragmentInteractionListener{

    // store the fragments
    private FragmentManager fragmentManager = getSupportFragmentManager();
    // get the current date
    private String date_n = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitchhiker);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
