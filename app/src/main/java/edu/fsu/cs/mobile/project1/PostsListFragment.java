package edu.fsu.cs.mobile.project1;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class PostsListFragment extends Fragment {
    public static final String TAG = PostsListFragment.class.getCanonicalName();

    private PostArrayAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_posts_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Connect to Firestore DB
        db = FirebaseFirestore.getInstance();

        // Define PostArrayAdapter and set ListView
        ListView list = view.findViewById(R.id.listView_posts);
        adapter = new PostArrayAdapter(getActivity(), R.layout.row_post);

        // Get latest posts from Firestore
        FirestoreHelper.getPosts(adapter, db);

        // Assign adapter to the ListView
        list.setAdapter(adapter);

        // Add Pull to Refresh interaction with ListView
        //  Source: https://guides.codepath.com/android/Implementing-Pull-to-Refresh-Guide#swiperefreshlayout-with-listview
        final SwipeRefreshLayout swipeContainer = view.findViewById(R.id.swipe);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Get latest posts
                FirestoreHelper.getPosts(adapter, db);

                // Show loader for 2 seconds
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }
}
