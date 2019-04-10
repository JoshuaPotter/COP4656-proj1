package edu.fsu.cs.mobile.project1;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class YourPostsFragment extends Fragment {
    public static String TAG = YourPostsFragment.class.getCanonicalName();

    private PostArrayAdapter adapter;
    private FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_your_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        ListView list = view.findViewById(R.id.listViewL_your_posts);
        adapter = new PostArrayAdapter(getActivity(), R.layout.row_post);

        getPosts();

        list.setAdapter(adapter);
    }

    public void getPosts(){
        adapter.clear();

        db.collection("posts").whereEqualTo("User ID", FirebaseAuth.getInstance().getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task){
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Map<String, Object> data = new HashMap<>(document.getData());
                                double latitude = ((GeoPoint) data.get("Location")).getLatitude();
                                double longitude = ((GeoPoint) data.get("Location")).getLongitude();
                                String message = (String) data.get("Message");
                                Timestamp timestamp = (Timestamp) data.get("Timestamp");
                                String title = (String) data.get("Title");
                                String userId = (String) data.get("User ID");

                                Post item = new Post(title, message, latitude, longitude, timestamp.toDate(), userId);
                                adapter.add(item);
                            }
                        }
                        else{
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
        adapter.notifyDataSetChanged();
    }
}
