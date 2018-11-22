package ch.safedrive.safedrive.ui.hitchhiker.request;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import ch.safedrive.safedrive.R;

public class HitchhikerActivity extends AppCompatActivity implements CreateRequest.OnFragmentInteractionListener, Taking_picture.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitchhiker);

        Fragment fragment = null;
        Class fragmentClass = null;

        fragmentClass = CreateRequest.class;

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
