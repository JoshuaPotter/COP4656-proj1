package edu.fsu.cs.mobile.project1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;


public class PostsListFragment extends Fragment implements LocationListener {
    public static final String TAG = PostsListFragment.class.getCanonicalName();
    public static final String SHOW_USERS_POSTS_FLAG = "showUsersPost";

    private PostArrayAdapter adapter;
    private FirebaseFirestore db;
    private Bundle bundle;

    // Location
    private LocationManager manager;
    private double latitude;
    private double longitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bundle = getArguments();
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current location coordinates
        getLatLong();

        // Connect to Firestore DB
        db = FirebaseFirestore.getInstance();

        // Define PostArrayAdapter and set ListView
        ListView list = view.findViewById(R.id.listView_posts);
        adapter = new PostArrayAdapter(getActivity(), R.layout.row_post);

        // Check to see if we should show user's posts or posts in current location
        if(bundle != null && bundle.containsKey(SHOW_USERS_POSTS_FLAG)) {
            // Get user's posts
            FirestoreHelper.getMyPosts(adapter, db);
        } else {
            // Get latest posts in current location from Firestore
            FirestoreHelper.getPosts(adapter, db, latitude, longitude);
        }

        // Assign adapter to the ListView
        list.setAdapter(adapter);

        // Add Pull to Refresh interaction with ListView
        //  Source: https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide#swiperefreshlayout-with-listview
        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipe);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get latest posts
                FirestoreHelper.getPosts(adapter, db, latitude, longitude);

                // Show loader for 2 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    public PostArrayAdapter getAdapter() {
        return adapter;
    }

    @Override
    public void onLocationChanged(Location location) {
        // If location changes, update coordinates
        Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());

        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Required function by LocationListener
    }
    @Override
    public void onProviderEnabled(String provider) {
        // Required function by LocationListener
    }
    @Override
    public void onProviderDisabled(String provider) {
        // Required function by LocationListener
    }

    public void requestLocationPermissions() {
        // Request location permissions for latitude and longitude
        manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request permission
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            }
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
        } catch(SecurityException e) {
            Log.e(TAG, "Error creating location service", e);
        }
    }

    public void getLatLong() {
        requestLocationPermissions();

        // Get current location coordinates
        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } catch(SecurityException e) {
            Toast.makeText(getActivity(), "GPS cannot determine your location", Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
