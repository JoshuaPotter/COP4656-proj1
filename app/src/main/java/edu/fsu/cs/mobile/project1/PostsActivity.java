package edu.fsu.cs.mobile.project1;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        // TODO: Add listener to fragmentmanager to update bottom nav on back buttom press
        // TODO: https://stackoverflow.com/questions/53813235/button-not-change-on-back-button-press-in-bottomnavigationview
        final AppCompatActivity activity = this;
        BottomNavigationView bottomNavigation = findViewById(R.id.posts_bottom_navigation);
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
                    // View account
                    state = toAccount();
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

    public boolean toCreatePost() {
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

    public boolean toViewPosts() {
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
            transaction.add(R.id.frameLayout_posts, fragment, PostsListFragment.TAG);
        }
        transaction.commit();

        return true;
    }

    public boolean toViewYourPosts() {
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

    // Show posts in your current location in mapview
    public boolean toViewPostsMap() {
        // Display MapViewFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Create new fragment
        MapViewFragment mapViewFragment = new MapViewFragment();

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show MapViewFragment
        transaction.addToBackStack(currentFragment.getTag());
        transaction.replace(R.id.frameLayout_posts,mapViewFragment);
        transaction.commit();

        return true;
    }

    // Show your posts in mapview
    public boolean toViewYourPostsMap() {
        // Display MapViewFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Set arguments
        Bundle bundle = new Bundle();
        bundle.putBoolean(PostsListFragment.SHOW_USERS_POSTS_FLAG, true);

        // Create new fragment
        MapViewFragment mapViewFragment = new MapViewFragment();
        mapViewFragment.setArguments(bundle);

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show MapViewFragment
        transaction.addToBackStack(currentFragment.getTag());
        transaction.replace(R.id.frameLayout_posts,mapViewFragment);
        transaction.commit();

        return true;
    }

    // Show account management page
    public boolean toAccount() {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Create new fragment
        AccountFragment accountFragment = new AccountFragment();

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show MapViewFragment
        transaction.addToBackStack(currentFragment.getTag());
        transaction.replace(R.id.frameLayout_posts, accountFragment);
        transaction.commit();

        return true;
    }
}
