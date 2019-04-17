package edu.fsu.cs.mobile.project1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

// This class provides helper functions for Google Sign In API
public final class AuthHelper {
    public static GoogleSignInClient getClient(Context context) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(context, gso);
    }

    public static void signIn(Activity activity, GoogleSignInClient mGoogleSignInClient, int resultCode) {
        // Launch the Intent for GoogleSignInClient.getSignInIntent(...);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, resultCode);
        // onActivityResult(...) is handled in each activity file
    }

    public static void signOut(Activity activity, GoogleSignInClient mGoogleSignInClient) {
        final Activity mActivity = activity;

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        // Google sign out & trigger UI update
        mGoogleSignInClient.signOut().addOnCompleteListener(activity,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Upon sign out, send to MainActivity and finish current activity so user
                        //  cannot go back
                        Intent toSignIn = new Intent(mActivity, MainActivity.class);
                        mActivity.startActivity(toSignIn);
                        mActivity.finish();
                    }
                });
    }
}
