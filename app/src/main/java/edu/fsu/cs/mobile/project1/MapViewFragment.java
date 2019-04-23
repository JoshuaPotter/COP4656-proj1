package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MapViewFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = MapViewFragment.class.getCanonicalName();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);
        mapView = (MapView) view.findViewById(R.id.mapFragMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return view;
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
getLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mMap = googleMap;
        postArrayList = new ArrayList<Post>();

        // Populate map with posts
        FirestoreHelper.getPosts(db, postArrayList, mMap, latitude, longitude);

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,12));
    }
}
