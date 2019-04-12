package edu.fsu.cs.mobile.project1;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
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

    // Get latest posts in current location
    public static void getPosts(final PostArrayAdapter adapter, FirebaseFirestore db) {
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
}
