package ch.safedrive.safedrive.ui.hitchhiker.request;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ch.safedrive.safedrive.R;

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

    ImageButton btnTakingPicture;
    static final int REQUEST_IMAGE_CAPTURE = 1;


    private View view;
    private TextView mtextViewCurrentDate;
    private Spinner mSpinnerCityFrom, mSpinnerCityTo;
    private String mCityFrom, mCityTo;

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(data != null){
            Bitmap bitmapTakingPicture1 = (Bitmap) data.getExtras().get("data");


            if (requestCode == REQUEST_IMAGE_CAPTURE && bitmapTakingPicture1 != null) {

                Bitmap bitmapTakingPicture = (Bitmap) data.getExtras().get("data");
                btnTakingPicture.setImageBitmap(bitmapTakingPicture);
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

        btnTakingPicture = (ImageButton) view.findViewById(R.id.id_takingPicture);
        btnTakingPicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intentPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentPicture, REQUEST_IMAGE_CAPTURE);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }

        mSpinnerCityFrom = view.findViewById(R.id.spinnerCityFrom);
        mSpinnerCityTo = view.findViewById(R.id.spinnerCityTo);
        mCityFrom = mSpinnerCityFrom.getSelectedItem().toString();
        mCityTo = mSpinnerCityTo.getSelectedItem().toString();

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


    @Override
    public void onResume() {
        super.onResume();

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
