package edu.fsu.cs.mobile.project1;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.core.FirestoreClient;

import java.util.Date;


public class PostCreateFragment extends Fragment {
    public static final String TAG = PostsListFragment.class.getCanonicalName();

    // TODO: Send values to FireStore database using FireBase

    private Button submit_button;
    private Button cancel_button;

    private EditText et_title;
    private EditText et_message;

    private String title;
    private String message;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_post_create, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get UI Objects
        et_title = view.findViewById(R.id.editText_title);
        et_message = view.findViewById(R.id.editText_message);
        submit_button = view.findViewById(R.id.button_submit);
        cancel_button = view.findViewById(R.id.button_cancel);

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = et_title.getText().toString();
                message = et_message.getText().toString();

                if (title.matches("") || message.matches("")) {
                    Toast.makeText(getActivity(), "Missing title or message, could not create post.", Toast.LENGTH_SHORT)
                            .show();
                }
                else {
                    // Get User ID
                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    // Get Location
                    double latitude = 0.0,
                            longitude = 0.0;
                    try {
                        LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    } catch(SecurityException e) {
                        Toast.makeText(getActivity(), "GPS cannot determine your location", Toast.LENGTH_SHORT)
                                .show();
                    }

                    // Get Timestamp
                    String timestamp = new Timestamp(new Date()).toString();

                    // to be sent to firestore database
                    Post test_item = new Post(title, message, latitude, longitude, timestamp, userid);
                }
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // May have to be done a different way
                getActivity().onBackPressed();
            }
        });

    }

}
