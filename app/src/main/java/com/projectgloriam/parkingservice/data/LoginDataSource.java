package com.projectgloriam.parkingservice.data;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.projectgloriam.parkingservice.HttpHandler;
import com.projectgloriam.parkingservice.MainActivity;
import com.projectgloriam.parkingservice.R;
import com.projectgloriam.parkingservice.Session;
import com.projectgloriam.parkingservice.Spot;
import com.projectgloriam.parkingservice.SpotAdapter;
import com.projectgloriam.parkingservice.UserViewModel;
import com.projectgloriam.parkingservice.data.model.LoggedInUser;
import com.projectgloriam.parkingservice.model.User;
import com.projectgloriam.parkingservice.network.APIUtils;
import com.projectgloriam.parkingservice.network.ApiInterface;

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

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private ApiInterface apiClient = APIUtils.getAPIService();

    public Result<LoggedInUser> login(String username, String password) {

        apiClient.authUser(username,password).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "User authentication to API unsuccessful: " + e.toString());
                        showLoginError();
                    }

                    @Override
                    public void onNext(User user) {
                        showResponse(user);
                    }
                });

        return new Result.Error(new IOException("Couldn't log in. Either wrong username or password. Please try again."));
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public Result<LoggedInUser> showLoginError() {

        return new Result.Error(new IOException("Couldn't log in. Either wrong username or password. Please try again."));
    }

    public Result<LoggedInUser> showResponse(User response) {

        LoggedInUser user =
                new LoggedInUser(
                        response.getId().toString(),
                        response.getFirst_name() +" "+response.getLast_name(),
                        response.getEmail());

        return new Result.Success<>(user);
    }
}