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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import ch.safedrive.safedrive.R;
import ch.safedrive.safedrive.firebase.PopulateFirebase;
import ch.safedrive.safedrive.model.Location;
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
    private Button mBtnSubmit;

    private View view;
    private TextView mtextViewCurrentDate;
    private Spinner mSpinnerCityFrom, mSpinnerCityTo;
    private String mCityFrom, mCityTo;

    FirebaseDatabase database;
    DatabaseReference myRef;

    // Access to firebase storage
    FirebaseStorage storage;
    StorageReference storageReference;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_request, container, false);

        // set the current date in the textview
        mtextViewCurrentDate = (TextView) view.findViewById(R.id.textViewCurrentDate);
        mtextViewCurrentDate.setText(mCurrentDate);

        mBtnSubmit = (Button) view.findViewById(R.id.buttonSubmit);
        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mIdPictureFirestore = storeImagePlate();
                storeRequest(mIdPictureFirestore);

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

    public void storeRequest(String mIdPicture) {

        mSpinnerCityFrom = view.findViewById(R.id.spinnerCityFrom);
        mSpinnerCityTo = view.findViewById(R.id.spinnerCityTo);
        mCityFrom = mSpinnerCityFrom.getSelectedItem().toString();
        mCityTo = mSpinnerCityTo.getSelectedItem().toString();
        mEditTextNumPlate = view.findViewById(R.id.id_edit_numPlate);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("requests");

        // create the new hitchhiker request and complete it
        Request mRequestHitchhiker = new Request();
        mRequestHitchhiker.setIdPlatePic(mIdPicture);
        mRequestHitchhiker.setPlate(mEditTextNumPlate.getText().toString());

    }

    // store the picture in firestore and return the id of the image stored
    public String storeImagePlate (){

        // get the instance and references to firestore
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if (mUriFilePath != null){

            // get a random id to store the picture
            mIDPictureFireStore = UUID.randomUUID().toString();

            // store the picture with an random id
            StorageReference ref = storageReference.child("license-plates/" + mIDPictureFireStore);

            ref.putFile(mUriFilePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        }
                    });
        }

        return mIDPictureFireStore;
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
