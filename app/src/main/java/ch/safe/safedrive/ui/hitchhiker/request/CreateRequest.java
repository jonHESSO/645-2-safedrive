package ch.safe.safedrive.ui.hitchhiker.request;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import ch.safe.safedrive.R;
import ch.safe.safedrive.model.Location;
import ch.safe.safedrive.model.Request;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CreateRequest.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CreateRequest#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateRequest extends Fragment {
    private static final String CURRENT_DATE = "param1";
    private String mCurrentDate;
    private OnFragmentInteractionListener mListener;

    private ImageButton mbtnTakingPicture;
    private Uri mUriFilePath;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private EditText mEditTextNumPlate;
    private String mIDPictureFireStore;
    private String keyLocationFrom, keyLocationTo;
    private Button mBtnSubmit;
    private Button mBtnGetCurrentLocation;

    private View view;
    private TextView mtextViewCurrentDate;
    private Spinner mSpinnerCityFrom, mSpinnerCityTo;
    private String mCityFrom, mCityTo;
    private HashMap<String, String> locationsMapFrom = new HashMap<>();
    private HashMap<String, String> locationsMapTo = new HashMap<>();
    ArrayAdapter<String> locationsToAdapter ;
    ArrayAdapter<String> locationsFromAdapter ;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Access to firebase storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseUser user;

    private Context context ;
    private Activity activity ;

    int PERMISSION_ALL = 1;
    static final int PERMISSION_STORAGE_CAMERABUTTON = 4 ;
    static final int PERMISSION_LOCATION_LOCBUTTON = 3 ;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
    };

    private LocationListener mLocationListener ;
    private android.location.Location currentLocation ;
    private LocationManager mLocationManager ;


    public CreateRequest() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Current date.
     * @return A new instance of fragment CreateRequest.
     */
    public static CreateRequest newInstance(String param1) {
        CreateRequest fragment = new CreateRequest();
        Bundle args = new Bundle();
        args.putString(CURRENT_DATE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this.getContext() ;
        this.activity = this.getActivity();
        if (getArguments() != null) {
            mCurrentDate = getArguments().getString(CURRENT_DATE);
        }

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(!hasPermissions(context, PERMISSIONS)){
            requestPermissions(PERMISSIONS, PERMISSION_ALL);
        }

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                currentLocation = location ;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_request, container, false);

        // set the current date in the textview
        mtextViewCurrentDate = (TextView) view.findViewById(R.id.textViewCurrentDate);
        mtextViewCurrentDate.setText(mCurrentDate);

        mSpinnerCityFrom = view.findViewById(R.id.spinnerCityFrom);
        mSpinnerCityTo = view.findViewById(R.id.spinnerCityTo);
        mEditTextNumPlate = view.findViewById(R.id.id_edit_numPlate);

        // fill the spinner with locations
        fillSpinnerLocations();

        mBtnSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // store the request on firebase and go to the next fragment "myTrip"
                storeRequest();
            }
        });

        mBtnGetCurrentLocation = (Button) view.findViewById(R.id.btnGetCurrentLocation);
        mBtnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if(hasPermissions(context, PERMISSIONS[3]))
                {
                    getCurrentLocation();
                }
                else
                    {
                    requestPermissions(new String[]{PERMISSIONS[3]},3);
                }
            }
        });

        // When button to take a picture pressend, start the camera
        mbtnTakingPicture = (ImageButton) view.findViewById(R.id.id_takingPicture);
        mbtnTakingPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (hasPermissions(context, PERMISSIONS[4])) {
                    launchCamera();
                }
                else
                {
                    requestPermissions(new String[]{PERMISSIONS[4]}, PERMISSION_STORAGE_CAMERABUTTON);
                }

            }
        });


        return view;

    }

    public String readPicture(Bitmap bitmap) {
        TextRecognizer textRecognizer = new TextRecognizer.Builder(view.getContext().getApplicationContext()).build();

        Frame imageFrame = new Frame.Builder()

                .setBitmap(bitmap)                 // your image bitmap
                .build();

        String imageText = "";


        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            imageText = textBlock.getValue();
        }

        return imageText;
    }

    public void storeRequest() {

        mCityFrom = mSpinnerCityFrom.getSelectedItem().toString();
        mCityTo = mSpinnerCityTo.getSelectedItem().toString();

        myRef = database.getReference("locations");

        for(Map.Entry<String, String> entry : locationsMapFrom.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(value.equals(mSpinnerCityFrom.getSelectedItem().toString())){
                keyLocationFrom = key;
            }
        }

        for(Map.Entry<String, String> entry : locationsMapTo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(value.equals(mSpinnerCityTo.getSelectedItem().toString())){
                keyLocationTo = key;
            }
        }

        try {
            storeImagePlate();
        } catch(Exception e) {
            e.printStackTrace();
        }


        // create the new hitchhiker request and complete it
        Request mRequestHitchhiker = new Request();

        mRequestHitchhiker.setId(UUID.randomUUID().toString());
        mRequestHitchhiker.setIdPlatePic(mIDPictureFireStore);
        mRequestHitchhiker.setPlate(mEditTextNumPlate.getText().toString());
        mRequestHitchhiker.setDate(new Date());
        mRequestHitchhiker.setLocationFrom(keyLocationFrom);
        mRequestHitchhiker.setLocationTo(keyLocationTo);
        mRequestHitchhiker.setUser(user.getUid());
        mRequestHitchhiker.setDestinationReached(false);

        // add the request to firebase
        addRequestToFirebase (mRequestHitchhiker);

        //Change fragment
        MyTrip mt = MyTrip.newInstance(mRequestHitchhiker.getId());
        getFragmentManager().beginTransaction().replace(R.id.flContent, mt).commit();

    }

    public void fillSpinnerLocations() {
        myRef = database.getReference("locations");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<String> locationsFrom = new ArrayList<String>();
                final List<String> locationsTo = new ArrayList<String>();

                for (DataSnapshot locationsSnapshot: dataSnapshot.getChildren()) {
                    if(locationsSnapshot.child("depart").getValue(Boolean.class)) {
                        locationsFrom.add(locationsSnapshot.child("name").getValue(String.class));
                        locationsMapFrom.put(locationsSnapshot.child("id").getValue(String.class), locationsSnapshot.child("name").getValue(String.class));
                    }


                    if(locationsSnapshot.child("destination").getValue(Boolean.class)) {
                        locationsTo.add(locationsSnapshot.child("name").getValue(String.class));
                        locationsMapTo.put(locationsSnapshot.child("id").getValue(String.class), locationsSnapshot.child("name").getValue(String.class));
                    }
                }

                locationsToAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, locationsTo);
                locationsToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerCityTo.setAdapter(locationsToAdapter);

                locationsFromAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, locationsFrom);
                locationsFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerCityFrom.setAdapter(locationsFromAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }
        });
    }

    // store the picture in firestore and return the id of the image stored
    public void storeImagePlate () {

        if (mUriFilePath != null) {

            // set to null the id of the picture before to set an new id for the new picture
            mIDPictureFireStore = null;

            // get a random id to store the picture
            mIDPictureFireStore = UUID.randomUUID().toString();

            // store the picture with an random id
            StorageReference ref = storageReference.child("license-plates/" + mIDPictureFireStore);

            // put the file picture on firebase storage
            ref.putFile(mUriFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("Picture successfully uploaded!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            System.out.println(exception.getMessage());
                        }
                    });
        }
    }

    public void addRequestToFirebase(final Request mRequestHitchhiker){
        myRef = database.getReference("requests");
        myRef.child(mRequestHitchhiker.getId()).setValue(mRequestHitchhiker);
    }

    // receive the captured photo as a result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Control if a picture was taken
        if(data != null){

            // set a bitmap to control the content of the picutre taken
            Bitmap mBitmapTakingControl = (Bitmap) data.getExtras().get("data");

            // If the picture from the camera is really there
            if (requestCode == REQUEST_IMAGE_CAPTURE && mBitmapTakingControl != null) {

                // Get the path of the image
                mUriFilePath = data.getData();

                try {
                    // Take this picture and put it in the image button
                    Bitmap bitmapTakingPicture = (Bitmap) data.getExtras().get("data");
                    mbtnTakingPicture.setImageBitmap(bitmapTakingPicture);
                    System.out.println("========" + readPicture(bitmapTakingPicture) + "=========");

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
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

   /*

   //May be useful later for showing dialogs on older devices

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    */


    //TODO : implement enable/disable of ui elements if permissions are granted/denied
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_STORAGE_CAMERABUTTON:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchCamera();
                } else {
                    Toast.makeText(context, "Storage access denied Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_LOCATION_LOCBUTTON:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    Toast.makeText(context, "Location access denied Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void launchCamera()
    {
        Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentPicture, REQUEST_IMAGE_CAPTURE);
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation()
    {

        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, mLocationListener);

        if(currentLocation == null)
        {
            Toast.makeText(context, "No known location", Toast.LENGTH_SHORT).show();
            return ;
        }
        myRef = database.getReference("locations");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final HashMap<String, Double> locationsFrom = new HashMap<>();

                for (DataSnapshot locationsSnapshot : dataSnapshot.getChildren()) {
                    if (locationsSnapshot.child("depart").getValue(Boolean.class)) {
                        double distance = distance(currentLocation.getLatitude(), currentLocation.getLongitude(), (double)locationsSnapshot.child("latitude").getValue(), (double)locationsSnapshot.child("longitude").getValue());
                        locationsFrom.put(locationsSnapshot.child("name").getValue(String.class), distance);
                        locationsMapFrom.put(locationsSnapshot.child("id").getValue(String.class), locationsSnapshot.child("name").getValue(String.class));
                    }
                }

                Map.Entry closestEntry = entriesSortedByValues(locationsFrom).first();
                if ((double)closestEntry.getValue() <= 5)
                {
                    int position = locationsFromAdapter.getPosition((String)closestEntry.getKey());
                    mSpinnerCityFrom.setSelection(position,true);
                }
                else{
                    Toast.makeText(context,"No nearby depart points", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println(databaseError.getMessage());
            }

        });
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

    static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }



}
