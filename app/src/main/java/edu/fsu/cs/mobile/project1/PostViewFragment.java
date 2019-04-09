package edu.fsu.cs.mobile.project1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PostViewFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = PostViewFragment.class.getCanonicalName();

    private Post item;

    private TextView mTitle;
    private TextView mTimestamp;
    private TextView mMessage;
    private MapView mMapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        if(getArguments() != null) {
            item = bundle.getParcelable("post");
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get UI objects
        mTitle = view.findViewById(R.id.textView_title);
        mTimestamp = view.findViewById(R.id.textView_timestamp);
        mMessage = view.findViewById(R.id.textView_message);
        mMapView = view.findViewById(R.id.mapView);

        // Set text for TextView objects for this post
        mTitle.setText(item.getTitle());
        mTimestamp.setText(item.getTimestamp().toString());
        mMessage.setText(item.getMessage());

        // Setup map
        mMapView.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng location = new LatLng(item.getLatitude(), item.getLongitude());
//        map.addMarker(new MarkerOptions().position(location).title("Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLng(location));
    }
}
