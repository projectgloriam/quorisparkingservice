package com.projectgloriam.parkingservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MapsFragment extends Fragment {
    /**
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean permissionDenied = false;

    private GoogleMap map;

    public List<Park> parkList;

    private UserViewModel userModel;

    private OnFragmentInteractionListener mListener;

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {

            Boolean indicateHQ = false;

            map = googleMap;

            // Set a listener for marker click.
            map.setOnMarkerClickListener(onMarkerClickListener);

            LatLng mapPosition;

            //Creating and scaling marker
            int height = 150;
            int width = 150;
            BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.car);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            //Iterating through list of parks
            for(Park p : parkList) {

                //COORDINATE
                mapPosition = new LatLng(p.getLatitude(), p.getLongitude());

                if(indicateHQ==false) {
                    //zoom to first id (HQ)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapPosition, 15));
                    indicateHQ=true;
                }

                //list of markers
                map.addMarker(new MarkerOptions()
                        .position(mapPosition)
                        .title(p.getId().toString())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                //if place is full, use red tint or blue
                //BitmapDescriptorFactory.fromResource(R.drawable.arrow)
                //BitmapDescriptorFactory.defaultMarker(float hue)
                //BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                //The hue is a value between 0 and 360, representing points on a color wheel.
            }

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            map.setOnMyLocationButtonClickListener(onMyLocationButtonClickLister);
            map.setOnMyLocationClickListener(onMyLocationClickListener);

            enableMyLocation();
        }
    };

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager() , "dialog");
    }

    @Nullable GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickLister = () -> false;

    @Nullable GoogleMap.OnMyLocationClickListener onMyLocationClickListener = location -> {
        //location.getLongitude();
        //location.getLatitude();
    };

    @Nullable GoogleMap.OnMarkerClickListener onMarkerClickListener = marker -> {
        // Retrieve the data from the marker.

        //marker.getTitle();
        int park_id =  Integer. parseInt(marker.getTitle());
        
        Park selectedPark = null;
        for (Park park : parkList) {
            if (park.getId().equals(park_id)) {
                selectedPark = park;
            }
        }

        //Intent to LoginActivity with marker title as id
        MapsFragmentDirections.ActionMapsFragmentToParkFragment action = MapsFragmentDirections.actionMapsFragmentToParkFragment(selectedPark);
        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(action);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).

        return true;
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Check if session is set else return to login
        userModel = new ViewModelProvider((MainActivity) getActivity()).get(UserViewModel.class);
        userModel.getSession().observe(getViewLifecycleOwner(), session -> {
            // Perform an action with the latest session data
            if(session.isset()==false)
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.action_mapsFragment_to_loginFragment);
        });

        parkList = new ArrayList<>();

        //Run background thread to fetch JSON list of parks and upon success, launch Google map
        new GetParks().execute();

    }

    //Fetch list of parks
    private class GetParks extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(),"Fetching list of parks",Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String url = "http://quorissolutions.com/parks.php";
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONArray parks = new JSONArray(jsonStr);

                    // looping through All Contacts
                    for (int i = 0; i < parks.length(); ++i) {
                        JSONObject p = parks.getJSONObject(i);

                        Integer id = p.getInt("id");
                        String name = p.getString("name");
                        Double latitude = p.getDouble("latitude");
                        Double longitude = p.getDouble("longitude");
                        Double rate = p.getDouble("rate_per_30_minute");
                        Integer total_spots = p.getInt("total_spots");
                        Integer available_spots = p.getInt("available_spots");

                        // adding item to ParkList
                        parkList.add(new Park(id, name, latitude, longitude, rate, total_spots, available_spots));
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

            //Launch the map
            SupportMapFragment mapFragment =
                    (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(callback);
            }
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