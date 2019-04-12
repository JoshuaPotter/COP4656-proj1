package edu.fsu.cs.mobile.project1;

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

        // Get GoogleSignInClient instance
        mGoogleSignInClient = AuthHelper.getClient(PostsActivity.this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.posts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        boolean state = false;
        if(i == R.id.menuItem_sign_out) {
            // Sign Out menu item
            AuthHelper.signOut(this, mGoogleSignInClient);
            state = true;
        } else if (i == R.id.menuItem_create_post) {
            // Create New Post menu item
            state = toCreatePost();
        } else if (i == R.id.menuItem_view_your_posts){
            // View Your Posts menu item
            state = toViewYourPosts();

        }
        return state;
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

    private boolean toViewYourPosts(){
        // Display PostViewFragment
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // Create new fragment
        YourPostsFragment fragment = new YourPostsFragment();

        // Get current fragment
        Fragment currentFragment = manager.findFragmentById(R.id.frameLayout_posts);

        // Hide current fragment and show PostCreateFragment
        transaction.addToBackStack(currentFragment.getTag());
        transaction.replace(R.id.frameLayout_posts, fragment);
        transaction.commit();

        return true;
    }
}
