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
 * {@link DestinationReached_BadTrip.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationReached_BadTrip#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationReached_BadTrip extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String NUM_REQUEST = "numRequest";

    private View mView;
    private String mNumRequest;
    private Button mBtnSendTestimony;

    private OnFragmentInteractionListener mListener;

    public DestinationReached_BadTrip() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mNumRequest Parameter 1.

     * @return A new instance of fragment DestinationReached_BadTrip.
     */
    // TODO: Rename and change types and number of parameters
    public static DestinationReached_BadTrip newInstance(String mNumRequest) {
        DestinationReached_BadTrip fragment = new DestinationReached_BadTrip();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_destination_reached__bad_trip, container, false);

        mBtnSendTestimony = (Button) mView.findViewById(R.id.button_sendTestimony);

        return mView;
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
