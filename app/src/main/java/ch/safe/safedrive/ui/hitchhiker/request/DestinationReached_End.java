package ch.safe.safedrive.ui.hitchhiker.request;

import android.content.Context;
import android.content.Intent;
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
 * {@link DestinationReached_End.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DestinationReached_End#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DestinationReached_End extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View mView;
    private Button mBtnLeaveApp;
    private Button mBtnBackNewRequest;

    // Required empty public constructor
    public DestinationReached_End() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DestinationReached_End.
     */
    public static DestinationReached_End newInstance() {
        DestinationReached_End fragment = new DestinationReached_End();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_destination_reached__end, container, false);


        // the hitchhiker has finished his trip ---> set it to false in the hitchhiker activity
        ((HitchhikerActivity) this.getActivity()).setHitchhikerOnTrip(false);

        // when the user want a new request
        onPressBtnBackNewRequest();

        // when the user want to leave the app
        onPressBtnLeaveApp();

        return mView;
    }

    // when the user want a new request
    public void onPressBtnBackNewRequest(){

        // get the button New Request from the view
        mBtnBackNewRequest = (Button) mView.findViewById(R.id.buttonNewRequest);
        mBtnBackNewRequest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                /*
                remove all fragment stored in the fragment manager
                for (Fragment fragment:getFragmentManager().getFragments() {
                    getFragmentManager().beginTransaction().remove(fragment).commit();
                }
                */

                // start a new activity for a new request
                getActivity().recreate();
            }
        });
    }

    // when the user want to leave the app
    public void onPressBtnLeaveApp(){

        // get the button Leave App from view
        mBtnLeaveApp = (Button) mView.findViewById(R.id.buttonLeaveApp);
        mBtnLeaveApp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // finish the activity and exit the app
                getActivity().finish();
                System.exit(0);
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
