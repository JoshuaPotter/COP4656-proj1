package edu.fsu.cs.mobile.project1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryDataEventListener;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static edu.fsu.cs.mobile.project1.FirestoreHelper.POSTS_COLLECTION;
import static edu.fsu.cs.mobile.project1.FirestoreHelper.RADIUS;

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

    // TODO: Rename and change types and number of parameters
    public static MapViewFragment newInstance(String param1, String param2) {
        MapViewFragment fragment = new MapViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_map_view, container, false);
        mapView=(MapView)view.findViewById(R.id.mapFragMap);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        return view;
    }

    private void getPosts() {
            postArrayList.clear();
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            CollectionReference geoFirestoreRef = db.collection(POSTS_COLLECTION);
            GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
            GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(latitude,longitude),RADIUS);
            geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                @Override
                public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                    Post currentPost=new Post(documentSnapshot.getData());
                    LatLng temp=new LatLng(currentPost.getLatitude(),currentPost.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(temp).title(currentPost.getTitle()));
                }

                @Override
                public void onDocumentExited(DocumentSnapshot documentSnapshot) {

                }

                @Override
                public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                }

                @Override
                public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(Exception e) {

                }
            });
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
        mMap = googleMap;
        postArrayList=new ArrayList<Post>();
        getPosts();
        LatLng user=new LatLng(latitude,longitude);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user,17));
    }
}
