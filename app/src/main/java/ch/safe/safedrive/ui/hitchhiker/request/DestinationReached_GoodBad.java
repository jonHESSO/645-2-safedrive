package ch.safe.safedrive.ui.hitchhiker.request;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ch.safe.safedrive.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DestinationReached_GoodBad.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationReached_GoodBad#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationReached_GoodBad extends Fragment {
    // the fragment initialization parameters numRequest
    private static final String NUM_REQUEST = "numRequest";

    private View mView;
    private String mNumRequest;
    private Button mBtnGood;
    private Button mBtnBad;

    private OnFragmentInteractionListener mListener;

    // Required empty public constructor
    public DestinationReached_GoodBad() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mNumRequest Get the number of the current request.
     * @return A new instance of fragment DestinationReached_GoodBad.
     */
    public static DestinationReached_GoodBad newInstance(String mNumRequest) {
        DestinationReached_GoodBad fragment = new DestinationReached_GoodBad();
        Bundle args = new Bundle();
        args.putString(NUM_REQUEST, mNumRequest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // get the num request from the parameter
            mNumRequest = getArguments().getString(NUM_REQUEST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_destination_reached__good_bad, container, false);

        // when the user press on the button good
        onPressBtnGood();
        // when the user press on the button bad
        onPressBtnBad();

        return mView;
    }

    // when the user press on the good button : means that the trip was good
    public void onPressBtnGood(){
        // get the button from the view
        mBtnGood = (Button) mView.findViewById(R.id.buttonDestinationReached_Good);
        mBtnGood.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // start new fragment for the end of the create request
                DestinationReached_End dest_end = DestinationReached_End.newInstance();
                getFragmentManager().beginTransaction().add(R.id.flContent, dest_end).hide(getFragmentManager().findFragmentByTag("destination_goodbad")).addToBackStack(null).commit();
            }

        });

    }

    // when the user press on the bad button : means that he has a bad trip
    public void onPressBtnBad(){
        // get the bad button from the view
        mBtnBad = (Button) mView.findViewById(R.id.buttonDestinationReached_Bad);
        mBtnBad.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                // start new fragment in case of bad trip
                DestinationReached_BadTrip dest_badTrip = DestinationReached_BadTrip.newInstance(mNumRequest);
                getFragmentManager().beginTransaction().add(R.id.flContent, dest_badTrip).hide(getFragmentManager().findFragmentByTag("destination_goodbad")).addToBackStack(null).commit();
            }
        });
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
        void onFragmentInteraction(Uri uri);
    }
}
