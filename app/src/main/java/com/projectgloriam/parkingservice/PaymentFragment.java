package com.projectgloriam.parkingservice;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.projectgloriam.parkingservice.data.Result;
import com.projectgloriam.parkingservice.data.model.LoggedInUser;
import com.projectgloriam.parkingservice.model.APIResponse;
import com.projectgloriam.parkingservice.model.InvoiceDetails;
import com.projectgloriam.parkingservice.model.Ticket;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ApiInterface apiClient;
    private String userid;

    private UserViewModel userModel;

    public Button payButton;

    private RadioGroup paymentGroup;
    private RadioButton paymentChannelButton;
    private ProgressDialog dialog;

    public Park park;

    public PaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
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
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiClient = APIUtils.getAPIService();

        park = ParkFragmentArgs.fromBundle(getArguments()).getPark();

        //Check if session is set else return to login
        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userModel.getSession().observe(getViewLifecycleOwner(), session -> {
            // Perform an action with the latest session data
            if(session.isset()==false)
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_ticketFragment_to_loginFragment);

            userid = session.getuserid();
        });

        payButton = view.findViewById(R.id.payButton);

        paymentGroup= view.findViewById(R.id.paymentRadioGroup);

        payButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                int selectedId=paymentGroup.getCheckedRadioButtonId();
                paymentChannelButton = view.findViewById(selectedId);
                checkPaymentMethod(paymentChannelButton.getText().toString());

            }
        });

    }

    private void checkPaymentMethod( String channel) {
        String cash = getResources().getString(R.string.cash); //Cash
        String momo = getResources().getString(R.string.momo); //MoMo
        String visa = getResources().getString(R.string.visa); //VISA
        //String orderCode = UUID.randomUUID().toString();

        if (cash.equals(channel)) {
            createTicket();
        } else if (momo.equals(channel)) {
            Toast.makeText(getContext(), "MoMo option will be available soon", Toast.LENGTH_SHORT).show();

            //dialog = new ProgressDialog(getActivity());
            //dialog.setMessage("Creating Invoice, Please Wait");
            //dialog.show();
            //TODO: Implement MoMo method
        } else if (visa.equals(channel)) {
            Toast.makeText(getContext(), "VISA option will be available soon", Toast.LENGTH_SHORT).show();
            //TODO: Implement VISA method
        } else {
            Toast.makeText(getContext(), "Invalid Option", Toast.LENGTH_SHORT).show();
        }
    }

    public void createTicket() {
        apiClient.saveTicket(Integer.parseInt(userid), park.getId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Ticket>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to submit ticket to API: " + e.toString());
                        if (e instanceof EOFException) {
                            Toast.makeText(getContext(), "Sorry, you already have a pending ticket", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "An error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onNext(Ticket ticket) {
                            showResponse(ticket);
                    }
                });
    }

    public void showResponse(Ticket response) {
            String ticket = response.getCode();
            //Navigate to ticket details fragment with ticket code
            PaymentFragmentDirections.ActionPaymentFragmentToTicketDetailsFragment action = PaymentFragmentDirections.actionPaymentFragmentToTicketDetailsFragment(ticket);
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
    }
}