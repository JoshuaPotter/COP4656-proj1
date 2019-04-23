package edu.fsu.cs.mobile.project1;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PostsActivity extends AppCompatActivity {
    public static final String TAG = "PostActivity";

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseUser user; // https://firebase.google.com/docs/auth/android/manage-users

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        // Setup bottom navigation
        BottomNavigationView bottomNavigation = findViewById(R.id.posts_bottom_navigation);
        final AppCompatActivity activity = this;
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int i = item.getItemId();
                boolean state = false;
                if(i == R.id.bottomNav_posts) {
                    // View Latest Posts
                    state = toViewPosts();
                } else if (i == R.id.bottomNav_your_posts){
                    // View Your Posts
                    state = toViewYourPosts();
                } else if (i == R.id.bottomNav_add_post) {
                    // Add a post
                    state = toCreatePost();
                } else if (i == R.id.bottomNav_account) {
                    // Sign Out
                    //AuthHelper.signOut(activity, mGoogleSignInClient);
                    //state = true;
                }
                return state;
            }
        });

        // Get GoogleSignInClient instance
        mGoogleSignInClient = AuthHelper.getClient(PostsActivity.this);

        // Get Firebase instance and current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // Set base fragment
        bottomNavigation.setSelectedItemId(R.id.bottomNav_posts);

        // Set activity title
        getSupportActionBar().setTitle(PostsListFragment.TITLE);
    }

    private boolean toCreatePost() {
        // Display PostCreateFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Create new fragment
        PostCreateFragment fragment = new PostCreateFragment();

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show PostCreateFragment
        transaction.addToBackStack(currentFragment.getTag());
        transaction.replace(R.id.frameLayout_posts, fragment);
        transaction.commit();

        return true;
    }

    private boolean toViewPosts() {
        // Display PostsListFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Create new fragment
        PostsListFragment fragment = new PostsListFragment();

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show PostsListFragment
        if(currentFragment != null) {
            transaction.addToBackStack(currentFragment.getTag());
            transaction.replace(R.id.frameLayout_posts, fragment);
        } else {
            transaction.add(R.id.frameLayout_posts, fragment);
        }
        transaction.commit();

        return true;
    }

    private boolean toViewYourPosts() {
        // Display PostsListFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Set arguments
        Bundle bundle = new Bundle();
        bundle.putBoolean(PostsListFragment.SHOW_USERS_POSTS_FLAG, true);

        // Create new fragment
        PostsListFragment fragment = new PostsListFragment();
        fragment.setArguments(bundle);

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show PostsListFragment
        transaction.addToBackStack(currentFragment.getTag());
        transaction.replace(R.id.frameLayout_posts, fragment);
        transaction.commit();

        return true;
    }
}
