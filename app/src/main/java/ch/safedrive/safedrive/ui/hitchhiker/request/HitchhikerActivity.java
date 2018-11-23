package ch.safedrive.safedrive.ui.hitchhiker.request;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.safedrive.safedrive.R;

public class HitchhikerActivity extends AppCompatActivity implements CreateRequest.OnFragmentInteractionListener {




    private FragmentManager fragmentManager = getSupportFragmentManager();
    private String date_n = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitchhiker);

        CreateRequest cr = CreateRequest.newInstance(date_n);

        // Insert the fragment by replacing any existing fragment
        fragmentManager.beginTransaction().replace(R.id.flContent, cr).commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
