package com.projectgloriam.parkingservice.ui.login;

import static android.content.ContentValues.TAG;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.projectgloriam.parkingservice.DrawerLocker;
import com.projectgloriam.parkingservice.MainActivity;
import com.projectgloriam.parkingservice.Session;
import com.projectgloriam.parkingservice.UserViewModel;
import com.projectgloriam.parkingservice.databinding.FragmentLoginBinding;

import com.projectgloriam.parkingservice.R;
import com.projectgloriam.parkingservice.model.User;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

import java.util.Objects;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginFragment extends Fragment {

    private LoginViewModel loginViewModel;
    private FragmentLoginBinding binding;

    public static final String Id = "IdKey";
    public static final String Name = "nameKey";
    public static final String Email = "emailKey";
    private ApiInterface apiClient;
    private UserViewModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiClient = APIUtils.getAPIService();

        userModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = binding.username;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.login;
        final ProgressBar loadingProgressBar = binding.loading;
        final Button registerRedirectButton = binding.registerRedirectButton;

        registerRedirectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Navigate to Register fragment
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_userFragment);
            }
        });

        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        /*loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser(loginResult.getSuccess());
                }
            }
        });*/

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    authenticateUser(usernameEditText.getText().toString(),passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(usernameEditText.getText().toString(),passwordEditText.getText().toString());
                authenticateUser(usernameEditText.getText().toString(),passwordEditText.getText().toString());
            }
        });
    }

    private void authenticateUser(String email, String password) {
        apiClient.authUser(email,password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "User authentication to API unsuccessful: " + e.toString());
                        if (getContext() != null && getContext().getApplicationContext() != null) {
                            Toast.makeText(getContext().getApplicationContext(), "User login failed. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onNext(User user) {
                        updateUiWithUser(user);
                    }
                });
    }

    private void updateUiWithUser(User user) {
        String fullName = user.getFirst_name() + " " + user.getLast_name();
        String welcome = getString(R.string.welcome) + " " + fullName;
        // TODO : initiate successful logged in experience
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(getContext().getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
        }

        //Setting user preferences or sessions
        userModel.setSession(new Session(getContext(), user.getId().toString(), user.getEmail(), fullName));

        //Navigate to Maps Fragment
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_mapsFragment);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Toast.makeText(
                    getContext().getApplicationContext(),
                    errorString,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}