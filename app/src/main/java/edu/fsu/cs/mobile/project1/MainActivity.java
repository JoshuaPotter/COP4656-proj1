package edu.fsu.cs.mobile.project1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    private static final int SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final TextView title = findViewById(R.id.loginTitleSample);
//        final String[] arrayTitle = getResources().getStringArray(R.array.titleItemsArray);
//        final TextView message = findViewById(R.id.loginMessageSample);
//        final String[] arrayMessage = getResources().getStringArray(R.array.messageItemsArray);
//
//
//        title.post(new Runnable() {
//            int i = 0;
//            @Override
//            public void run() {
//                title.setText(arrayTitle[i]);
//                message.setText(arrayMessage[i]);
//                i++;
//                if (i ==5)
//                    i = 0;
//                title.postDelayed(this, 5000);
//                message.postDelayed(this, 5000);
//            }
//        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        mGoogleSignInClient = AuthHelper.getClient(MainActivity.this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this); // google sign in listener

        // Get firebase authentication instance
        mAuth = FirebaseAuth.getInstance();
    }

    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser account = mAuth.getCurrentUser();
        updateUI(account);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if(i == R.id.google_sign_in_button) {
            AuthHelper.signIn(this, mGoogleSignInClient, SIGN_IN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignIn(task);
        }
    }

    private void handleGoogleSignIn(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user != null) {
            // Signed in state

            // Send to PostsActivity and finish this activity so user cannot come back unless they sign out
            // Automatically sends user to PostsActivity if already logged in from previous session
            Intent toPosts = new Intent(this, PostsActivity.class);
            startActivity(toPosts);
            finish();
        } else {
            // Signed out state
        }
    }

    public void snackbarMessage(View v) {
        Toast.makeText(this, "You must log in to see details.",
                Toast.LENGTH_LONG).show();
    }

}
