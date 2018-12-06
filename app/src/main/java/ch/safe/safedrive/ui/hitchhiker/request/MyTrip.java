package ch.safe.safedrive.ui.hitchhiker.request;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ch.safe.safedrive.model.Request;
import ch.safe.safedrive.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyTrip.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyTrip#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyTrip extends Fragment {
    private static String NUM_REQUEST = "param1";

    static public final int REQUEST_LOCATION = 1;
    private String mNumRequest;
    private View view;
    private Button mButtonDestinationReached, mButtonShowLocation;
    private Request hitchhikerRequest;
    private OnFragmentInteractionListener mListener;
    private TextView wai;
    private LocationManager lm;
    private Location location;

    // access to firebase database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Context context ;

    public MyTrip() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Number of the request
     * @return A new instance of fragment MyTrip.
     */
    // TODO: Rename and change types and number of parameters
    public static MyTrip newInstance(String param1) {
        MyTrip fragment = new MyTrip();
        Bundle args = new Bundle();
        args.putString(NUM_REQUEST, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this.getContext();
        if (getArguments() != null) {
            mNumRequest = getArguments().getString(NUM_REQUEST);
        }
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_my_trip, container, false);

        // get the reference for the pending request
        myRef = database.getReference("requests").child(mNumRequest);

        // retrieved the data for the hitchhicker resquest
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hitchhikerRequest = dataSnapshot.getValue(Request.class);
                hitchhikerRequest.setId(dataSnapshot.getKey());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        wai = (TextView) view.findViewById(R.id.textViewWai);

        // button destination reached
        mButtonDestinationReached = (Button) view.findViewById(R.id.buttonDestinationReached);
        mButtonDestinationReached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if the user press on destination reached, the state of the button is set to True.
                hitchhikerRequest.setDestinationReached(true);

                // update in firebase the hitchhicker resquest
                myRef.updateChildren(hitchhikerRequest.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(context,"Destination reached : \nRequest closed",Toast.LENGTH_SHORT).show();
                    }
                });

                // when the user press on the  button "Destination Reached"
                // start a new fragment for the Destination Reached view with 2 buttons Good and Bad
                DestinationReached_GoodBad dest_gb = DestinationReached_GoodBad.newInstance(mNumRequest);
                getFragmentManager().beginTransaction().replace(R.id.flContent, dest_gb).commit();

            }
        });

        mButtonShowLocation = (Button) view.findViewById(R.id.buttonShowLocation);
        mButtonShowLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoca();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void checkLoca() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }

        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location != null) {
            showMyAddress(location);
        }

        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                showMyAddress(location);
            }

            public void onProviderDisabled(String arg0) {
                // TODO Auto-generated method stub

            }

            public void onProviderEnabled(String arg0) {
                // TODO Auto-generated method stub

            }

            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
                // TODO Auto-generated method stub

            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);
    }

    // Also declare a private method

    private void showMyAddress(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder myLocation = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> myList;
        try {
            myList = myLocation.getFromLocation(latitude, longitude, 1);
            if(myList.size() == 1) {
                wai.setText(myList.get(0).toString());
            }
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }
}
