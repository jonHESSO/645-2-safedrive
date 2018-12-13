package ch.safe.safedrive.ui.hitchhiker.request;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.mail.*;

import ch.safe.safedrive.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SecurityWarning.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SecurityWarning#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecurityWarning extends Fragment {


    //Access firebase
    //Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser user;


    private static final String NUM_REQUEST = "numRequest";
    private String textAlert = "User in danger";
    private String mNumRequest;
    private View view;

    private Button mBtnAlertAdmin;
    private Button mBtnReturnTrip;
    private Button mBtnCallPolice;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SecurityWarning() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SecurityWarning.
     */
    public static SecurityWarning newInstance(String mNumRequest) {
        SecurityWarning fragment = new SecurityWarning();
        Bundle args = new Bundle();
        args.putString(NUM_REQUEST, mNumRequest);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNumRequest = getArguments().getString(NUM_REQUEST);
        }

        //The entry point for accessing a Firebase Database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_security_warning, container, false);



        //Change fragment
        //Button alert Admin
        mBtnAlertAdmin =  view.findViewById(R.id.btnWarningAdmin);
        mBtnAlertAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SecurityWarning_Admin swAdmin = SecurityWarning.newInstance(mNumRequest, textAlert);
               // getFragmentManager().beginTransaction().replace(R.id.flContent, swAdmin).commit();
            }
        });

        mBtnReturnTrip = view.findViewById(R.id.btnReturnTrip);
        mBtnReturnTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyTrip mt = MyTrip.newInstance(mNumRequest);
                getFragmentManager().beginTransaction().add(R.id.flContent, mt).hide(getFragmentManager().findFragmentByTag("security_warning")).addToBackStack(null).commit();
            }
        });


        //Button to call the police
        mBtnCallPolice = view.findViewById(R.id.btnCallPolice);
        mBtnCallPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callPolice("117");
            }
        });

       /* mBtnAlertAdmin = view.findViewById(R.id.btnWarningAdmin);
        mBtnAlertAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String emailLogin = user.getEmail();
                   Mail sender = new Mail(emailLogin);

                try {
                    sender.sendMail("Alert !",
                            "Alert for the person...",
                            emailLogin,
                            "alert.safedrive@gmail.com");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });*/

        return view;
    }




    public void callPolice(final String phoneNumber)
    {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }





    //Method to send a email
    protected void sendEmail()
    {
        mDatabaseReference = mFirebaseDatabase.getReference("user");


       /* String[] to = {"alert.safedrive@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);*/

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
