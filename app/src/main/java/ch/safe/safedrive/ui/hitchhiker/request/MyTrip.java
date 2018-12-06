package ch.safe.safedrive.ui.hitchhiker.request;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    private String mNumRequest;
    private View view;
    private Button mButtonDestinationReached;
    private Request hitchhikerRequest;
    private OnFragmentInteractionListener mListener;

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
