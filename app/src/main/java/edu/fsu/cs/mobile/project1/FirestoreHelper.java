package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {
    // Collection Title
    public static final String POSTS_COLLECTION = "posts";

    // 8km (~5 miles) radius
    public static final double RADIUS = 8;

    // Field Titles
    public static final String ID = "ID";
    public static final String TITLE = "Title";
    public static final String MESSAGE = "Message";
    public static final String USERID = "User ID";
    public static final String TIMESTAMP = "Timestamp";
    public static final String LOCATION = "l";
    public static final String UPVOTES = "Upvotes";

    // Get latest posts from current location in a list arrayadapter
    public static void getPosts(final View view, final PostArrayAdapter adapter, FirebaseFirestore db, double latitude, double longitude) {
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
                data.put(FirestoreHelper.ID, documentSnapshot.getId());
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

                // Hide loading animation and show new posts
                if(view != null) {
                    view.findViewById(R.id.animation_loading).setVisibility(View.GONE);
                }
                adapter.notifyDataSetChanged();
                Log.w("DocumentSnapshot", "Loaded documents from Firestore");
            }

            @Override
            public void onGeoQueryError(Exception e) {
                Log.w("DocumentSnapshot", "Error loading documents from Firestore " + e.getLocalizedMessage());
            }
        });
    }

    // Get posts from current location in mapview
    public static void getPosts(final FirebaseFirestore db, final ArrayList<Post> postArrayList, final GoogleMap mMap, final double latitude, final double longitude) {
        mMap.clear();
        postArrayList.clear();

        CollectionReference geoFirestoreRef = db.collection(POSTS_COLLECTION);

        Circle mapRadius;

        GeoFirestore geoFirestore = new GeoFirestore(geoFirestoreRef);
        GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(latitude, longitude), RADIUS);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                Map<String, Object> data = documentSnapshot.getData();
                data.put(FirestoreHelper.ID, documentSnapshot.getId());
                Post currentPost = new Post(data);
                LatLng postLocation = new LatLng(currentPost.getLatitude(),currentPost.getLongitude());
                mMap.addMarker(new MarkerOptions().position(postLocation).title(currentPost.getTitle()));
                // TODO: add onMarkerClick() event to create intent to view the post in PostViewFragment'
                // TODO: https://developers.google.com/maps/documentation/android-sdk/marker#marker_click_events
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
                // Add circle for user's radius
                mMap.addCircle(new CircleOptions()
                    .center(new LatLng(latitude, longitude))
                    .radius(RADIUS * 1000)
                    .strokeColor(Color.BLUE)
                    .fillColor(0x220000FF));
            }

            @Override
            public void onGeoQueryError(Exception e) {

            }
        });
    }

    // Get current user's posts for listview
    public static void getMyPosts(final View view, final PostArrayAdapter adapter, FirebaseFirestore db) {
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
                                Map<String, Object> data = document.getData();
                                data.put(FirestoreHelper.ID, document.getId());
                                Post item = new Post(data);
                                adapter.add(item);
                            }

                            // Hide loading animation and show new posts
                            if(view != null) {
                                view.findViewById(R.id.animation_loading).setVisibility(View.GONE);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.w("Firestore error: ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    // Get current user's posts for mapview
    public static void getMyPosts(final FirebaseFirestore db, final GoogleMap mMap) {
        mMap.clear();
        // Get posts from database based on current user's id and orders by timestamp
        db.collection(POSTS_COLLECTION)
                .whereEqualTo(USERID, FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Log.d("DocumentSnapshot", document.getId() + " => " + document.getData());

                                Map<String, Object> data = document.getData();
                                data.put(FirestoreHelper.ID, document.getId());
                                Post currentPost = new Post(data);
                                LatLng postLocation = new LatLng(currentPost.getLatitude(), currentPost.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(postLocation).title(currentPost.getTitle()));
                            }
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
        data.put(UPVOTES, item.getUpvotes());
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

                        FirestoreHelper.getPosts(fragment.getView(), fragment.getAdapter(), db, item.getLatitude(), item.getLongitude()); // gets latest posts

                        // Go back to list
                        activity.getSupportFragmentManager().popBackStack();
                        BottomNavigationView bottomNavigation = activity.findViewById(R.id.posts_bottom_navigation);
                        bottomNavigation.setSelectedItemId(R.id.bottomNav_posts);
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

    public static void deleteFromDB(final FirebaseFirestore db, final String id) {
        db.collection(FirestoreHelper.POSTS_COLLECTION)
                .document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore error: ", "Error deleting document", e);
                    }
                });
    }
}
