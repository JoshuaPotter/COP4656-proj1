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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.GeoQueryDataEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    // Collection Title
    public static final String POSTS_COLLECTION = "posts";

    // 16 km (~10 miles) radius
    public static final double RADIUS = 16;

    // Field Titles
    public static final String TITLE = "Title";
    public static final String MESSAGE = "Message";
    public static final String USERID = "User ID";
    public static final String TIMESTAMP = "Timestamp";
    public static final String LOCATION = "l";

    // Get latest posts in current location
    public static void getPosts(final PostArrayAdapter adapter, FirebaseFirestore db, double latitude, double longitude) {
        // Remove posts from adapter if any exist
        adapter.clear();

        // Get posts from database based on latitude and longitude parameters using GeoFirestore-Android library
        //   Source: https://github.com/imperiumlabs/GeoFirestore-Android
        CollectionReference geoFirestoreRef = db.collection(POSTS_COLLECTION);
        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(latitude, longitude), RADIUS);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                // For each document in our query within the geolocation, add it to the adapter as a post object
                Map<String, Object> data = documentSnapshot.getData();
                adapter.add(new Post(data));
                Log.w("DocumentSnapshot", data.toString());
            }

            @Override
            public void onDocumentExited(DocumentSnapshot documentSnapshot) {
                // For each document in query that has left the geolocation
            }

            @Override
            public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                // For each document in query that has moved within the geolocation
            }

            @Override
            public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                // Required for GeoFirestore
            }

            @Override
            public void onGeoQueryReady() {
                // Fires when all documents, if any, have been loaded

                // Sorts post objects in adapter using timestamps
                adapter.sortByTimestamp();
                adapter.notifyDataSetChanged();
                Log.w("DocumentSnapshot", "Loaded documents from Firestore");
            }

            @Override
            public void onGeoQueryError(Exception e) {
                Log.w("DocumentSnapshot", "Error loading documents from Firestore " + e.getLocalizedMessage());
            }
        });
    }

    // Get current user's posts
    public static void getMyPosts(final PostArrayAdapter adapter, FirebaseFirestore db) {
        // Remove posts from adapter if any exist
        adapter.clear();

        // Get posts from database based on current user's id and orders by timestamp
        db.collection(POSTS_COLLECTION)
                .whereEqualTo(USERID, FirebaseAuth.getInstance().getCurrentUser().getUid())
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("DocumentSnapshot", document.getId() + " => " + document.getData());

                                // Add post to adapter
                                Map<String, Object> data = new HashMap<>(document.getData());
                                Post item = new Post(data);
                                adapter.add(item);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("Firestore error: ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public static void addToDB(final FragmentActivity activity, final FirebaseFirestore db, final Post item) {
        // Setup Map object for Firestore
        Map<String, Object> data = new HashMap<>();
        data.put(TIMESTAMP, FieldValue.serverTimestamp());
        data.put(TITLE, item.getTitle());
        data.put(MESSAGE, item.getMessage());
        data.put(USERID, item.getUserid());

        // Add map object to Firestore collection
        db.collection(FirestoreHelper.POSTS_COLLECTION)
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Log.d("DocumentSnapshot added with ID: " + documentReference.getId());

                        // Add geohash to post just added for geoqueries
                        CollectionReference geoFirestoreRef = db.collection(POSTS_COLLECTION);
                        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
                        geoFirestore.setLocation(documentReference.getId(), new GeoPoint(item.getLatitude(), item.getLongitude()));

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
