package com.projectgloriam.parkingservice.data;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.projectgloriam.parkingservice.HttpHandler;
import com.projectgloriam.parkingservice.Spot;
import com.projectgloriam.parkingservice.SpotAdapter;
import com.projectgloriam.parkingservice.data.model.LoggedInUser;

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
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {


    public Result<LoggedInUser> login(String username, String password) {

        try {
            //handle loggedInUser authentication

            String response = fetchUser(username, password);

            if(response!=null){
                JSONArray jsonArray = new JSONArray(response);
                switch(jsonArray.get(0).toString()){
                    case "nouser": {
                        return new Result.Error(new IOException("User doesn't exist"));
                    }
                    case "invalid": {
                        return new Result.Error(new IOException("Invalid Password"));
                    }
                }

                JSONObject jsonresponse = (JSONObject) jsonArray.get(0);
                String id = jsonresponse.getString("id");
                String firstName = jsonresponse.getString("firstname");
                String lastName = jsonresponse.getString("lastname");
                String email = jsonresponse.getString("email");

                //LoggedInUser fakeUser = new LoggedInUser(java.util.UUID.randomUUID().toString(),"Jane Doe");

                LoggedInUser user =
                        new LoggedInUser(
                                id,
                                firstName+" "+lastName,
                                email);

                return new Result.Success<>(user);
            } else {
                return new Result.Error(new IOException("Error logging in"));
            }

        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }

    public String fetchUser(String username, String password){
        try {
            //create a URL object
            URL url = new URL("http://quorissolutions.com/auth.php");

            try {
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //set the request method property to POST
                con.setRequestMethod("POST");

                //send the request content in JSON form
                con.setRequestProperty("Content-Type", "application/json; utf-8");

                //Set the “Accept” request header to “application/json” to read the response in the desired format
                con.setRequestProperty("Accept", "application/json");

                con.setDoOutput(true);

                String jsonInputString = "{'name': '" + username + "', 'pass': '" + password + "'}";

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.e("Authentication error", e.toString());
                    return null;
                }

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    return response.toString();
                } catch (IOException e) {
                    //e.printStackTrace();
                    Log.e("Authentication error", e.toString());
                    return null;
                }

            } catch (IOException e) {
                //e.printStackTrace();
                Log.e("Authentication error", e.toString());
                return null;
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
            Log.e("Authentication error", e.toString());
            return null;
        }
    }
}