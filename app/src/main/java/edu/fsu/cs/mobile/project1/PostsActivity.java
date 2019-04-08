package edu.fsu.cs.mobile.project1;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PostsActivity extends AppCompatActivity {
    public static final String TAG = "PostActivity";

    private FirebaseAuth mAuth;
    private FirebaseUser user; // https://firebase.google.com/docs/auth/android/manage-users

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        // Get Firebase instance and current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Instantiate PostsListFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Show PostsListFragment
        PostsListFragment fragment = new PostsListFragment();
        transaction.add(R.id.frameLayout_posts, fragment, PostsListFragment.TAG);
        transaction.commit();
    }
}
