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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

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
    private Button mButtonDestinationReached;
    private Button mButtonReportProblem;
    private Request hitchhikerRequest;
    private OnFragmentInteractionListener mListener;
    private LocationManager lm;
    private Location location, destinationLocation;
    private Double lat, lng;

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

        //Button report problem
        mButtonReportProblem = view.findViewById(R.id.buttonReportProblem);
        mButtonReportProblem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick (View view){
                SecurityWarning sw = SecurityWarning.newInstance(mNumRequest);
                getFragmentManager().beginTransaction().replace(R.id.flContent, sw, "security_warning").commit();
            }
        });

        // get the reference for the pending request
        myRef = database.getReference("requests").child(mNumRequest);

        // retrieved the data for the hitchhicker resquest
        ValueEventListener valueEventListenerHitchhicker = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hitchhikerRequest = dataSnapshot.getValue(Request.class);
                hitchhikerRequest.setId(dataSnapshot.getKey());

                myRef = database.getReference("locations").child(hitchhikerRequest.getLocationTo());
                ValueEventListener valueEventListenerDestination = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        lat = dataSnapshot.child("latitude"). getValue(Double .class);
                        lng = dataSnapshot.child("longitude").getValue(Double .class);

                        System.out.println(lat + " ========================= "+ lng);

                        destinationLocation = new Location("");
                        destinationLocation.setLatitude(lat);
                        destinationLocation.setLongitude(lng);

                        // call the method to get the current position of the user
                        getUserLocation();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                };

                myRef.addValueEventListener(valueEventListenerDestination);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.addValueEventListener(valueEventListenerHitchhicker);


        // button destination reached
        mButtonDestinationReached = (Button) view.findViewById(R.id.buttonDestinationReached);
        mButtonDestinationReached.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // get the reference for the pending request
                myRef = database.getReference("requests").child(mNumRequest);

                // if the user press on destination reached, the state of the button is set to True.
                hitchhikerRequest.setDestinationReached(true);

                // update in firebase the hitchhicker resquest
                myRef.updateChildren(hitchhikerRequest.toMap(), new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        Toast.makeText(context,"Destination reached : \nRequest closed",Toast.LENGTH_SHORT).show();
                    }
                });
                // change fragment
                DestinationReached_GoodBad dest_gb = DestinationReached_GoodBad.newInstance(mNumRequest);
                getFragmentManager().beginTransaction().replace(R.id.flContent, dest_gb, "destination_goodbad").commit();
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

    /**
     * Method to get the current user's location and store it on firebase
     */
    private void getUserLocation() {

        myRef = database.getReference("trips");

        // check the permissions
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

        final LocationListener locationListener = new LocationListener() {
            // each time the location change store the new location on firebase
            public void onLocationChanged(Location location) {
                myRef.child(mNumRequest).child(UUID.randomUUID().toString()).setValue(location);
                System.out.println("================ INSERT LOCATION ==================");

                // check if the user is near the destination
                if(isDestinationReached(location, destinationLocation))
                    System.out.println("Reached!");
                else
                    System.out.println("Not reached");
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

        // call the method each 10 sec
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);

        // create the trip on firebase
        if(location != null) {
            myRef.child(mNumRequest).child(UUID.randomUUID().toString()).setValue(location);
            if(isDestinationReached(location, destinationLocation))
                System.out.println("Reached!");
            else
                System.out.println("Not reached");
        }
    }

    private boolean isDestinationReached(Location currentLocation, Location finalLocation) {
        double latCurrentLocation = currentLocation.getLatitude();
        double lngCurrentLocation = currentLocation.getLongitude();
        double latFinalLocation = finalLocation.getLatitude();
        double lngFinalLocation = finalLocation.getLongitude();

        if (distance(latCurrentLocation, lngCurrentLocation, latFinalLocation, lngFinalLocation) < 0.1) {
            return true;
        }

        return false;
    }


    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; //

        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist;
    }

    // method to get the address from the location
    // used for debugging
    private void showMyAddress(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        Geocoder myLocation = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> myList;
        try {
            myList = myLocation.getFromLocation(latitude, longitude, 1);
            if(myList.size() == 1) {
                System.out.println(myList.get(0).toString());
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
