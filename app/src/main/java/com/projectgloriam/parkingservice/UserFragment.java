package com.projectgloriam.parkingservice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private UserViewModel userModel;

    private EditText firstname, lastname, phone, email, password;
    private Button register;
    private Button signUpRedirectButton;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        //Disable drawer layout
        ((DrawerLocker) getActivity()).setDrawerEnabled(false);

        // initializing all variables.
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        register = view.findViewById(R.id.register);
        signUpRedirectButton = view.findViewById(R.id.login_redirect_button);

        signUpRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to Login
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_userFragment_to_loginFragment);
            }
        });

        register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (firstname.getText().toString().matches("") || lastname.getText().toString().matches("") || phone.getText().toString().matches("") || email.getText().toString().matches("") || password.getText().toString().matches("") ) {
                    Toast.makeText(getActivity(), "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Register user via POST request to server
                registerUser();

                //Navigate to Maps
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_userFragment_to_mapsFragment);
            }
        });


    }

    private void registerUser(){
        try {
            //create a URL object
            URL url = new URL("http://quorissolutions.com/users.php");

            try {
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //set the request method property to POST
                con.setRequestMethod("POST");

                //send the request content in JSON form
                con.setRequestProperty("Content-Type", "application/json; utf-8");

                //Set the “Accept” request header to “application/json” to read the response in the desired format
                con.setRequestProperty("Accept", "application/json");

                con.setDoOutput(true);

                String jsonInputString = "{'firstname': '" + firstname + "', 'lastname': '" + lastname + "', 'phone': '" + phone + "', 'email': '" + email + "', 'password': '" + password + "'}";

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.e("Connection error", e.toString());
                    Toast.makeText(getContext().getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String jsonString = response.toString();
                    if(jsonString!=null) {
                        JSONArray jsonArray = new JSONArray(jsonString);

                        JSONObject jsonresponse = (JSONObject) jsonArray.get(0);
                        String id = jsonresponse.getString("id");
                        String firstName = jsonresponse.getString("firstname");
                        String lastName = jsonresponse.getString("lastname");
                        String email = jsonresponse.getString("email");

                        //Setting user preferences or sessions
                        userModel.setSession(new Session(getContext(), id, email, firstName+" "+lastName));
                    }
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.e("Connection error", e.toString());
                    Toast.makeText(getContext().getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                //e.printStackTrace();
                Log.e("Connection error", e.toString());
                Toast.makeText(getContext().getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
            Log.e("Connection error", e.toString());
            Toast.makeText(getContext().getApplicationContext(), "Connection error", Toast.LENGTH_LONG).show();
        }
    }
}