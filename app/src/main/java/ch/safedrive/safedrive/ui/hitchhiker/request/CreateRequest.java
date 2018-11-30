package ch.safedrive.safedrive.ui.hitchhiker.request;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ch.safedrive.safedrive.R;
import ch.safedrive.safedrive.model.Request;

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

    private View view;
    private TextView mtextViewCurrentDate;
    private Spinner mSpinnerCityFrom, mSpinnerCityTo;
    private String mCityFrom, mCityTo;
    private HashMap<String, String> locationsMapFrom = new HashMap<>();
    private HashMap<String, String> locationsMapTo = new HashMap<>();

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Access to firebase storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentDate = getArguments().getString(CURRENT_DATE);
        }

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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

        // When button to take a picture pressend, start the camera
        mbtnTakingPicture = (ImageButton) view.findViewById(R.id.id_takingPicture);
        mbtnTakingPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentPicture, REQUEST_IMAGE_CAPTURE);
            }
        });

        return view;
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
        mRequestHitchhiker.setDestinationReached(false);

        // add the request to firebase
        addRequestToFirebase (mRequestHitchhiker);

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

                ArrayAdapter<String> locationsToAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, locationsFrom);
                locationsToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerCityFrom.setAdapter(locationsToAdapter);

                ArrayAdapter<String> locationsFromAdapter = new ArrayAdapter<String>(view.getContext(), android.R.layout.simple_spinner_item, locationsTo);
                locationsFromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpinnerCityTo.setAdapter(locationsFromAdapter);
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

        String asdf = mRequestHitchhiker.getIdPlatePic();
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
}
