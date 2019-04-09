package edu.fsu.cs.mobile.project1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;


public class PostCreateFragment extends Fragment implements LocationListener {
    public static final String TAG = PostCreateFragment.class.getCanonicalName();

    private FirebaseFirestore db;

    // Location
    private LocationManager manager;
    private double latitude;
    private double longitude;

    // UI Objects
    private Button submit_button;
    private Button cancel_button;
    private EditText et_title;
    private EditText et_message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_create, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Connect to Firestore DB
        db = FirebaseFirestore.getInstance();

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

        // Get UI Objects
        et_title = view.findViewById(R.id.editText_title);
        et_message = view.findViewById(R.id.editText_message);
        submit_button = view.findViewById(R.id.button_submit);
        cancel_button = view.findViewById(R.id.button_cancel);

        // On Click Listener for submit
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString();
                String message = et_message.getText().toString();

                if (title.matches("") || message.matches("")) {
                    Toast.makeText(getActivity(), "Missing title or message, could not create post.", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    createPost(title, message);
                }
            }
        });

        // On Click Listener for cancel
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go back to list
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    public void createPost(String title, String message) {
        // Get current location coordinates
        try {
            Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        } catch(SecurityException e) {
            Toast.makeText(getActivity(), "GPS cannot determine your location", Toast.LENGTH_SHORT)
                    .show();
        }

        // Get User ID
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // To be sent to firestore database, null timestamp will autopopulate by Firestore
        Post item = new Post(title, message, latitude, longitude, null, userId);

        // Add to Firestore DB
        addToDB(item);
    }

    public void addToDB(Post item) {
        // Setup Map object for Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("Location", new GeoPoint(item.getLatitude(), item.getLongitude()));
        data.put("Timestamp", FieldValue.serverTimestamp());
        data.put("Title", item.getTitle());
        data.put("Message", item.getMessage());
        data.put("User ID", item.getUserid());

        // Add to Firestore collection called "posts"
        db.collection("posts")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        // Get PostsListFragment from fragment manager and update it's posts
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        PostsListFragment fragment = (PostsListFragment) manager.findFragmentByTag(PostsListFragment.TAG);
                        fragment.getPosts(); // gets latest posts

                        // Go back to list
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(), "Error adding post", Toast.LENGTH_SHORT);
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        // If location changes, update coordinates
        Log.i("Message: ","Location changed, " + location.getAccuracy() + " , " + location.getLatitude()+ "," + location.getLongitude());

        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
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
}
