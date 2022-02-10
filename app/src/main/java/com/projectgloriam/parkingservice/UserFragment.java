package com.projectgloriam.parkingservice;

import static android.content.ContentValues.TAG;

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

import com.projectgloriam.parkingservice.model.Ticket;
import com.projectgloriam.parkingservice.model.User;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private EditText firstname, lastname, phone, email, password, confirm_password;
    private Button register;
    private Button signUpRedirectButton;

    private ApiInterface apiClient;

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
        apiClient = APIUtils.getAPIService();
        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        // initializing all variables.
        firstname = view.findViewById(R.id.firstname);
        lastname = view.findViewById(R.id.lastname);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        confirm_password = view.findViewById(R.id.confirm_password);
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
                //Check empty fields
                if (firstname.getText().toString().matches("") || lastname.getText().toString().matches("") || phone.getText().toString().matches("") || email.getText().toString().matches("") || password.getText().toString().matches("") || confirm_password.getText().toString().matches("") ) {
                    Toast.makeText(getActivity(), "Please don't leave any field blank", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Checking password length is 5 or more
                if (password.getText().length() < 5 || confirm_password.getText().length() < 5 ) {
                    Toast.makeText(getActivity(), R.string.invalid_password, Toast.LENGTH_SHORT).show();
                    return;
                }

                if ( !password.getText().toString().equals(confirm_password.getText().toString()) ) {
                    Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Register user via POST request to server
                registerUser();


            }
        });


    }

    public void registerUser() {
        apiClient.saveUser(firstname.getText().toString(), lastname.getText().toString(), email.getText().toString(),password.getText().toString(),phone.getText().toString()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Unable to register user to API: " + e.toString());

                        Toast.makeText(getContext(), "An error has occurred. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(User user) {
                        showResponse(user);
                    }
                });
    }

    public void showResponse(User response) {
        //Setting user preferences or sessions
        userModel.setSession(new Session(getContext(), response.getId().toString(), response.getEmail(), response.getFirst_name()+" "+response.getLast_name()));
        //Navigate to Maps
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_userFragment_to_mapsFragment);
    }
}