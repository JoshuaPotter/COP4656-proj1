package edu.fsu.cs.mobile.project1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PostViewFragment extends Fragment implements OnMapReadyCallback {
    public static final String TAG = PostViewFragment.class.getCanonicalName();

    // Post object for this view fragment
    private Post item;

    // UI Objects
    private TextView mTitle;
    private TextView mTimestamp;
    private TextView mMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_view, container, false);

        // Grab post arguments
        Bundle bundle = getArguments();
        if(getArguments() != null) {
            item = bundle.getParcelable("post");
        }

        // Get View objects
        mTitle = view.findViewById(R.id.textView_title);
        mTimestamp = view.findViewById(R.id.textView_timestamp);
        mMessage = view.findViewById(R.id.textView_message);

        // Set text for TextView objects for this post
        mTitle.setText(item.getTitle());
        mTimestamp.setText(item.getFormattedTimestamp());
        mMessage.setText(item.getMessage());

        // Setup map
        SupportMapFragment mapFragment =
                (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // Position location coordinates on map when we have the response from Google Maps API
        LatLng location = new LatLng(item.getLatitude(), item.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
    }
}
