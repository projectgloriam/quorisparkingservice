package com.projectgloriam.parkingservice;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.projectgloriam.parkingservice.model.APIResponse;
import com.projectgloriam.parkingservice.model.InvoiceDetails;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    protected RecyclerView mRecyclerView;
    protected SpotAdapter mAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    public ArrayList<Spot> spotList;
    public Integer available_spots;
    public Integer total_spots;
    public Button newTicketButton;
    public Park park;
    private UserViewModel userModel;
    private ProgressDialog dialog;
    private ApiInterface apiClient;
    private String userid;
    private TextView rate;

    public ParkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ParkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ParkFragment newInstance(String param1, String param2) {
        ParkFragment fragment = new ParkFragment();
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
        return inflater.inflate(R.layout.fragment_park, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiClient = APIUtils.getAPIService();

        //Check if session is set else return to login
        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userModel.getSession().observe(getViewLifecycleOwner(), session -> {
            // Perform an action with the latest session data
            if(session.isset()==false)
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_ticketFragment_to_loginFragment);

            userid = session.getuserid();
        });

        park = ParkFragmentArgs.fromBundle(getArguments()).getPark();

        rate = view.findViewById(R.id.rate);

        //Displaying the rate per 30 minute to user before purchase
        rate.setText("The rate per 30 minutes is: " + park.getRate().toString());

        mRecyclerView = view.findViewById(R.id.spot_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a grid layout manager
        mLayoutManager = new GridLayoutManager(getActivity(), 6);
        mRecyclerView.setLayoutManager(mLayoutManager);
        newTicketButton = view.findViewById(R.id.newTicketButton);

        newTicketButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Navigate to Payment Fragment with park id
                ParkFragmentDirections.ActionParkFragmentToPaymentFragment action = ParkFragmentDirections.actionParkFragmentToPaymentFragment(park);
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);
            }
        });

        spotList = new ArrayList<Spot>();

        mAdapter = new SpotAdapter(spotList, getContext());
        mRecyclerView.setAdapter(mAdapter);

        //Run background thread to fetch JSON list of parks and upon success, launch Google map
        //new GetSpots().execute();

        available_spots = park.getAvailableSpots();

        total_spots = park.getTotalSpots();

        // adding item to SpotList
        for (int j = 0; j < total_spots; j++) {
            //By default, new Spot object's status is set to false
            spotList.add(new Spot());
        }

        //Setting available spots
        for (int k = 0; k < available_spots; k++) {
            spotList.get(k).setStatus(true);
        }

        if (available_spots>0){
            newTicketButton.setEnabled(true);
        }

        Toast.makeText(getActivity(),
                "There are "+total_spots.toString()+" spots. "+available_spots.toString()+" spots are available.",
                Toast.LENGTH_LONG).show();
    }

    //Fetch list of parks
    /*private class GetSpots extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Fetching list of parking spots",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://quorissolutions.com/spots.php?park_id=" + park.getId().toString();
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray parks = new JSONArray(jsonStr);

                    JSONObject s = parks.getJSONObject(0);

                    total_spots = Integer.parseInt(s.getString("total_spots"));
                    available_spots = Integer.parseInt(s.getString("available_spots"));

                    // adding item to SpotList
                    for (int j = 0; j < total_spots; j++) {
                        //By default, new Spot object's status is set to false
                        spotList.add(new Spot());
                    }

                    //Setting available spots
                    for (int k = 0; k < available_spots; k++) {
                        spotList.get(k).setStatus(true);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });

                }

            } else {
                Log.e(TAG, "Couldn't get json from server.");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);


        }
    }*/
}