package edu.fsu.cs.mobile.project1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PostsActivity extends AppCompatActivity {
    public static final String TAG = "PostActivity";

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user; // https://firebase.google.com/docs/auth/android/manage-users

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        // Get GoogleSignInClient instance
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
            // Sign Out menu
            signOut();
            state = true;
        }
        return state;
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out & trigger intent
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Upon sign out, send to MainActivity
                        Intent toSignIn = new Intent(PostsActivity.this, MainActivity.class);
                        startActivity(toSignIn);
                    }
                });
    }
}
