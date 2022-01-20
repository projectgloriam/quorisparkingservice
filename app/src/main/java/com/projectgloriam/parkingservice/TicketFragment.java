package com.projectgloriam.parkingservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.projectgloriam.parkingservice.model.APIResponse;
import com.projectgloriam.parkingservice.model.Ticket;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TicketFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private UserViewModel userModel;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    WebView web ;
    String url,orderCode,payToken;
    private ApiInterface apiClient;
    ProgressDialog dialog;
    String ticket;

    String userid;

    public TicketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TicketFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TicketFragment newInstance(String param1, String param2) {
        TicketFragment fragment = new TicketFragment();
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
        return inflater.inflate(R.layout.fragment_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Check if session is set else return to login
        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userModel.getSession().observe(getViewLifecycleOwner(), session -> {
            // Perform an action with the latest session data
            if(session.isset()==false)
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_ticketFragment_to_loginFragment);

            userid = session.getuserid();
        });

        //Fetching parameters from previous fragment
        url = TicketFragmentArgs.fromBundle(getArguments()).getUrl();
        orderCode = TicketFragmentArgs.fromBundle(getArguments()).getOrderCode();
        payToken = TicketFragmentArgs.fromBundle(getArguments()).getPayToken();
    }

    void navigateToSuccessPage(){
        TicketFragmentDirections.ActionTicketFragmentToTicketDetailsFragment ticketAction = TicketFragmentDirections.actionTicketFragmentToTicketDetailsFragment(orderCode);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(ticketAction);
    }


}