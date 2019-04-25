package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    public static final String TAG = MapViewFragment.class.getCanonicalName();

    private Bundle bundle;
    private FirebaseFirestore db;
    private GoogleMap mMap;
    private MapView mapView;
    private ArrayList<Post> postArrayList;
    private double latitude;
    private double longitude;

    public MapViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = getArguments();
        db = FirebaseFirestore.getInstance();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        mapView = (MapView) view.findViewById(R.id.mapFragMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        getLocation();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.posts_options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.posts_map).setEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if(i == R.id.posts_list) {
            // Check if browsing current posts or user's posts, and set correct method for options
            if(bundle != null && bundle.containsKey(PostsListFragment.SHOW_USERS_POSTS_FLAG)) {
                ((PostsActivity) getActivity()).toViewYourPosts();
            } else {
                ((PostsActivity) getActivity()).toViewPosts();
            }
        } else if (i == R.id.posts_map) {
            if(bundle != null && bundle.containsKey(PostsListFragment.SHOW_USERS_POSTS_FLAG)) {
                ((PostsActivity) getActivity()).toViewYourPostsMap();
            } else {
                ((PostsActivity) getActivity()).toViewPostsMap();
            }
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mMap = googleMap;
        postArrayList = new ArrayList<Post>();

        // Populate map with posts
        getPosts();

        // Allow user to set their location on the map
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        try {
            mMap.setMyLocationEnabled(true);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }

        // Set user's location
        LatLng user = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,14));
        mMap.setOnInfoWindowClickListener(this);
    }

    private void getLocation() {
        try {
            LocationManager locationManager=(LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude=location.getLatitude();
            longitude=location.getLongitude();
        }
        catch (SecurityException e){
            Toast.makeText(getContext(),"Unable to determine GPS location",Toast.LENGTH_LONG).show();
        }
    }

    private void getPosts() {
        // Check to see if we should show user's posts or posts in current location
        if(bundle != null && bundle.containsKey(PostsListFragment.SHOW_USERS_POSTS_FLAG)) {
            // Get user's posts
            FirestoreHelper.getMyPosts(db, mMap);
        } else {
            // Get latest posts in current location from Firestore
            // Populate map with posts
            FirestoreHelper.getPosts(db, postArrayList, mMap, latitude, longitude);
        }
    }

    //we use onInfoWindowClick because onMarkerClick ran everytime you clicked the red marker
    @Override
    public void onInfoWindowClick(Marker marker) {
        Post markerPost=(Post) marker.getTag();
        //if post successfully retrieved from marker, open post in PVF
        //code is identical to the ArrayAdapter onclicklistener
        if (markerPost!=null){
            FragmentManager manager = ((FragmentActivity) getContext()).getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();

            // Create new fragment and set arguments
            Bundle bundle = new Bundle();
            bundle.putParcelable("post", markerPost);
            PostViewFragment fragment = new PostViewFragment();
            fragment.setArguments(bundle);

            // Hide PostsListFragment and show PostViewFragment
            transaction.addToBackStack(MapViewFragment.TAG);
            transaction.replace(R.id.frameLayout_posts, fragment);
            transaction.commit();
        }
    }
}
