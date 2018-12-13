package ch.safe.safedrive.ui.hitchhiker.request;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
public class MyTrip extends Fragment implements OnMapReadyCallback {
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
    private LocationListener locationListener;
    private HashMap<Date, Location> locationHashMap = new HashMap<>();
    private MapView mMapView;
    private GoogleMap googleMap;

    // access to firebase database
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private Context context;

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
        view = inflater.inflate(R.layout.fragment_my_trip, container, false);

        //Button report problem
        mButtonReportProblem = view.findViewById(R.id.buttonReportProblem);
        mButtonReportProblem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
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

                        lat = dataSnapshot.child("latitude").getValue(Double.class);
                        lng = dataSnapshot.child("longitude").getValue(Double.class);

                        destinationLocation = new Location("");
                        destinationLocation.setLatitude(lat);
                        destinationLocation.setLongitude(lng);

                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(destinationLocation.getLatitude(), destinationLocation.getLongitude())));

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

                updateRequest(mNumRequest);

                myRef = database.getReference("trips");

                // create the trip on firebase
                for(Map.Entry<Date, Location> entry : locationHashMap.entrySet()) {
                    Date key = entry.getKey();
                    Location value = entry.getValue();

                    myRef.child(mNumRequest).child(UUID.randomUUID().toString()).setValue(value);
                }

                // change fragment
                DestinationReached_GoodBad dest_gb = DestinationReached_GoodBad.newInstance(mNumRequest);
                getFragmentManager().beginTransaction().replace(R.id.flContent, dest_gb, "destination_goodbad").commit();
            }
        });

        // Get the view of the map
        mMapView = (MapView) view.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize the map
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                // For showing a move to my location button
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                googleMap.setMyLocationEnabled(true);
            }
        });

        return view;
    }

    private void updateRequest(String mNumRequest) {
        // get the reference for the pending request
        myRef = database.getReference("requests").child(mNumRequest);

        // if the user press on destination reached, the state of the button is set to True.
        hitchhikerRequest.setDestinationReached(true);

        // update in firebase the hitchhicker resquest
        myRef.updateChildren(hitchhikerRequest.toMap(), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                Toast.makeText(context, "Destination reached : \nRequest closed", Toast.LENGTH_SHORT).show();
            }
        });
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

        // check the permissions
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        locationListener = new LocationListener() {
            // each time the location change store the new location on firebase
            public void onLocationChanged(Location location) {
                locationHashMap.put(new Date(), location);

                // Add a marker in the maps
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().position(latLng).title(showMyAddress(location)));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // check if the user is near the destination
                if (isDestinationReached(location, destinationLocation)) {
                    lm.removeUpdates(this);

                    // if the destination is reached show a popup to confirm
                    showPopup();
                }
            }

            public void onProviderDisabled(String arg0) {

            }

            public void onProviderEnabled(String arg0) {

            }

            public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

            }
        };

        // call the method each 10 sec
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
    }

    // show a little popup to be sure the user has reached his destination
    private void showPopup() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle("Destination reached ?")
                .setMessage("Are you sure ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // set the destinationReached boolean to true
                        updateRequest(mNumRequest);

                        myRef = database.getReference("trips");

                        // create the trip on firebase
                        for(Map.Entry<Date, Location> entry : locationHashMap.entrySet()) {
                            Date key = entry.getKey();
                            Location value = entry.getValue();

                            myRef.child(mNumRequest).child(UUID.randomUUID().toString()).setValue(value);
                        }

                        // close the dialog
                        dialog.cancel();

                        // change fragment
                        DestinationReached_GoodBad dest_gb = DestinationReached_GoodBad.newInstance(mNumRequest);
                        getFragmentManager().beginTransaction().replace(R.id.flContent, dest_gb, "destination_goodbad").commit();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }

                        // call the method each 10 sec
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);

                        // close the dialog
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // method to check if the destination is reached
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

    // method to get the distance in km between 2 locations
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
    private String showMyAddress(Location location) {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());
        String out = "";

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            out = address + "\n";
        } catch (IOException e) {
            e.printStackTrace();
        }

        return out;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
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
