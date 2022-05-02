package com.projectgloriam.parkingservice;

import static android.content.ContentValues.TAG;
import static android.content.Context.WINDOW_SERVICE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.projectgloriam.parkingservice.model.Ticket;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.EOFException;
import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Ticket ticket;
    String userid;

    // variables for imageview, edittext,
    // button, bitmap and qrencoder.
    private ImageView qrCodeIV;
    private TextView clock_in, clock_out;
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    private ApiInterface apiClient;

    private UserViewModel userModel;

    private OnFragmentInteractionListener mListener;

    public TicketDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TicketDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TicketDetailsFragment newInstance(String param1, String param2) {
        TicketDetailsFragment fragment = new TicketDetailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ticket_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiClient = APIUtils.getAPIService();

        //Check if session is set else return to login
        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userModel.getSession().observe(getViewLifecycleOwner(), session -> {
            // Perform an action with the latest session data
            if(session.isset()==false)
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_ticketDetailsFragment_to_loginFragment);

            //Fetch user id from session
            userid = session.getuserid();

            // initializing all variables.
            qrCodeIV = view.findViewById(R.id.idIVQrcode);
            clock_in = view.findViewById(R.id.clock_in);
            clock_out = view.findViewById(R.id.clock_out);

            Toast.makeText(getActivity(),
                    "Fetching ticket details",
                    Toast.LENGTH_LONG).show();


            if (getArguments().getParcelable("ticket") != null) {
                //Fetching ticket from previous fragment
                ticket = TicketDetailsFragmentArgs.fromBundle(getArguments()).getTicket();
                showResponse(ticket);
            } else {
                //Fetching ticket details from server
                fetchTicket();
            }
        });


    }

    private void fetchTicket(){
        apiClient.getTicket(Integer.parseInt(userid)).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Ticket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to request for ticket details: " + e.toString());

                        if (e instanceof EOFException) {
                            Toast.makeText(getContext(), "You have no active ticket", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "An error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(Ticket t) {
                        showResponse(t);
                        Log.i(TAG, "Ticket details requested.");
                    }
                });
    }

    public void showResponse(Ticket response) {
        String clockInText, clockOutText;

        //Setting clock in and out text view
        if(response.getClock_in().getYear() < 2021){
            clockInText = "0000-00-00 00:00:00";
        } else {
            clockInText = response.getClock_in().toString();
        }

        if(response.getClock_out().getYear() < 2021){
            clockOutText = "0000-00-00 00:00:00";
        } else {
            clockOutText = response.getClock_out().toString();
        }

        clock_in.setText("Clock in: " + clockInText);
        clock_out.setText("Clock out: " + clockOutText);

        // below line is for getting
        // the windowmanager service.
        WindowManager manager = (WindowManager) getActivity().getSystemService(WINDOW_SERVICE);

        // initializing a variable for default display.
        Display display = manager.getDefaultDisplay();

        // creating a variable for point which
        // is to be displayed in QR Code.
        Point point = new Point();
        display.getSize(point);

        // getting width and
        // height of a point
        int width = point.x;
        int height = point.y;

        // generating dimension from width and height.
        int dimen = width < height ? width : height;
        dimen = dimen * 3 / 4;

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        qrgEncoder = new QRGEncoder(response.getCode(), null, QRGContents.Type.TEXT, dimen);
        try {
            // getting our qrcode in the form of bitmap.
            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            qrCodeIV.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // this method is called for
            // exception handling.
            Log.e("Tag", e.toString());
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