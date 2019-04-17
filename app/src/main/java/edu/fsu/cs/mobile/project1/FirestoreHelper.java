package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    // Collection Title
    public static final String POSTS_COLLECTION = "posts";

    // Field Titles
    public static final String TITLE = "Title";
    public static final String MESSAGE = "Message";
    public static final String USERID = "User ID";
    public static final String TIMESTAMP = "Timestamp";
    public static final String LOCATION = "Location";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";

    // Get latest posts in current location
    public static void getPosts(final PostArrayAdapter adapter, FirebaseFirestore db, double latitude, double longitude) {
        // Convert latitude and longitude to 1 mile
        final double latitudeInDecimalDegrees = 69.172;
        final double longitudeInDecimalDegrees = Math.cos(latitude) * latitudeInDecimalDegrees;
        final double latitudeInMile = 1 / latitudeInDecimalDegrees;
        final double longitudeInMile = 1 / longitudeInDecimalDegrees;

        // Remove posts from adapter if any exist
        adapter.clear();

        // Get posts from database
        db.collection(POSTS_COLLECTION)
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d(TAG, document.getId() + " => " + document.getData());

                                // Add post to adapter
                                Map<String, Object> data = new HashMap<>(document.getData());
                                Post item = new Post(data);
                                adapter.add(item);
                            }
                        } else {
                            Log.w("Firestore error: ", "Error getting documents.", task.getException());
                        }
                    }
                });

        // Update adapter
        adapter.notifyDataSetChanged();
    }

    // Get current user's posts
    public static void getMyPosts(final PostArrayAdapter adapter, FirebaseFirestore db) {
        // Remove posts from adapter if any exist
        adapter.clear();

        // Get posts from database
        db.collection(POSTS_COLLECTION)
                .whereEqualTo(USERID, FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d(TAG, document.getId() + " => " + document.getData());

                                // Add post to adapter
                                Map<String, Object> data = new HashMap<>(document.getData());
                                Post item = new Post(data);
                                adapter.add(item);
                            }
                        } else {
                            Log.w("Firestore error: ", "Error getting documents.", task.getException());
                        }
                    }
                });

        // Update adapter
        adapter.notifyDataSetChanged();
    }

    public static void addToDB(final FragmentActivity activity, final FirebaseFirestore db, final Post item) {
        // Setup Map object for Firestore
        Map<String, Object> data = new HashMap<>();
        data.put(LATITUDE, item.getLatitude());
        data.put(LONGITUDE, item.getLongitude());
        data.put(TIMESTAMP, FieldValue.serverTimestamp());
        data.put(TITLE, item.getTitle());
        data.put(MESSAGE, item.getMessage());
        data.put(USERID, item.getUserid());

        // Add to Firestore collection called "posts"
        db.collection(FirestoreHelper.POSTS_COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                        // Get PostsListFragment from fragment manager and update it's posts
                        FragmentManager manager = activity.getSupportFragmentManager();
                        PostsListFragment fragment = (PostsListFragment) manager.findFragmentByTag(PostsListFragment.TAG);
                        FirestoreHelper.getPosts(fragment.getAdapter(), db, item.getLatitude(), item.getLongitude()); // gets latest posts

                        // Go back to list
                        activity.getSupportFragmentManager().popBackStack();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore error: ", "Error adding document", e);
                        Toast.makeText(activity, "Error adding post", Toast.LENGTH_SHORT);
                    }
                });
    }
}